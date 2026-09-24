package com.lms.Backend.certificate.controller;

import com.lms.Backend.certificate.entity.Certificate;
import com.lms.Backend.certificate.service.CertificatePdfService;
import com.lms.Backend.certificate.service.CertificateService;
import com.lms.Backend.course.entity.Course;
import com.lms.Backend.course.repository.CourseLessonRepository;
import com.lms.Backend.course.repository.CourseRepository;
import com.lms.Backend.learning.entity.Enrollment;
import com.lms.Backend.learning.entity.EnrollmentStatus;
import com.lms.Backend.learning.repository.EnrollmentRepository;
import com.lms.Backend.learning.repository.LessonProgressRepository;
import com.lms.Backend.user.entity.User;
import com.lms.Backend.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping({"/api/certificates", "/certificates"})
public class CertificateController {

    private static final Logger log = LoggerFactory.getLogger(CertificateController.class);

    private final CertificateService certificateService;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CourseLessonRepository courseLessonRepository;
    private final LessonProgressRepository lessonProgressRepository;

    public CertificateController(
        CertificateService certificateService,
        UserRepository userRepository,
        CourseRepository courseRepository,
        EnrollmentRepository enrollmentRepository,
        CourseLessonRepository courseLessonRepository,
        LessonProgressRepository lessonProgressRepository
    ) {
        this.certificateService = certificateService;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.courseLessonRepository = courseLessonRepository;
        this.lessonProgressRepository = lessonProgressRepository;
    }

    /**
     * Generate or return existing certificate for an enrolled course upon 100% completion.
     * POST /api/certificates/generate/{courseId}
     * POST /api/certificates/claim/{courseId}
     */
    @PostMapping({"/generate/{courseId}", "/claim/{courseId}"})
    public ResponseEntity<?> generateCertificate(@PathVariable Long courseId, Principal principal) {
        if (principal == null || principal.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Authentication required"));
        }

        User user = userRepository.findByEmailIgnoreCase(principal.getName())
            .orElseThrow(() -> new IllegalArgumentException("Authenticated user not found"));

        try {
            Certificate cert = certificateService.generateCertificate(user.getId(), courseId);
            Map<String, Object> dto = buildCertificateDto(cert);
            return ResponseEntity.ok(dto);
        } catch (IllegalStateException e) {
            log.warn("Certificate generation rejected for user {}: {}", user.getEmail(), e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }

    /**
     * Get all certificates belonging to the currently authenticated student.
     * GET /api/certificates/my
     */
    @GetMapping("/my")
    public ResponseEntity<?> getMyCertificates(Principal principal) {
        if (principal == null || principal.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = userRepository.findByEmailIgnoreCase(principal.getName())
            .orElseThrow(() -> new IllegalArgumentException("Authenticated user not found"));

        // Automatically issue certificates for any 100% completed course enrollments
        try {
            List<Enrollment> enrollments = enrollmentRepository.findByUserIdOrderByEnrolledAtDesc(user.getId());
            for (Enrollment e : enrollments) {
                if (e.getCourse() == null) continue;
                boolean isCompleted = e.getStatus() == EnrollmentStatus.COMPLETED ||
                    (e.getProgressPercentage() != null && e.getProgressPercentage() >= 100);

                if (!isCompleted) {
                    long total = courseLessonRepository.countBySection_Course_Id(e.getCourse().getId());
                    long comp = lessonProgressRepository.countByEnrollmentIdAndCompletedTrue(e.getId());
                    if (total > 0 && comp >= total) {
                        e.setStatus(EnrollmentStatus.COMPLETED);
                        e.setProgressPercentage(100);
                        if (e.getCompletedAt() == null) {
                            e.setCompletedAt(java.time.Instant.now());
                        }
                        enrollmentRepository.save(e);
                        isCompleted = true;
                    }
                }

                if (isCompleted) {
                    try {
                        certificateService.generateCertificate(user.getId(), e.getCourse().getId());
                    } catch (Exception ignored) {}
                }
            }
        } catch (Exception ignored) {}

        List<Certificate> certs = certificateService.getUserCertificates(user.getId());
        List<Map<String, Object>> certList = new ArrayList<>();

        for (Certificate c : certs) {
            certList.add(buildCertificateDto(c));
        }

        // Return standardized wrapper containing success flag and certificates list
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("success", true);
        response.put("certificates", certList);
        return ResponseEntity.ok(response);
    }

    /**
     * Get certificate details by ID. Enforces ownership check.
     * GET /api/certificates/{certificateId}
     */
    @GetMapping("/{certificateId}")
    public ResponseEntity<?> getCertificateById(@PathVariable Long certificateId, Principal principal) {
        if (principal == null || principal.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User currentUser = userRepository.findByEmailIgnoreCase(principal.getName())
            .orElseThrow(() -> new IllegalArgumentException("Authenticated user not found"));

        boolean isAdmin = currentUser.getRole() != null && "ADMIN".equalsIgnoreCase(currentUser.getRole().name());
        Optional<Certificate> certOpt = isAdmin
            ? certificateService.getCertificateById(certificateId)
            : certificateService.getCertificateByIdAndUserId(certificateId, currentUser.getId());

        if (certOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "error", "Certificate not found or access denied"
            ));
        }

        return ResponseEntity.ok(buildCertificateDto(certOpt.get()));
    }

    /**
     * Download certificate PDF file as attachment.
     * GET /api/certificates/{certificateId}/download
     */
    @GetMapping("/{certificateId}/download")
    public ResponseEntity<?> downloadCertificatePdf(@PathVariable Long certificateId, Principal principal) {
        if (principal == null || principal.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User currentUser = userRepository.findByEmailIgnoreCase(principal.getName())
            .orElseThrow(() -> new IllegalArgumentException("Authenticated user not found"));

        boolean isAdmin = currentUser.getRole() != null && "ADMIN".equalsIgnoreCase(currentUser.getRole().name());
        Optional<Certificate> certOpt = isAdmin
            ? certificateService.getCertificateById(certificateId)
            : certificateService.getCertificateByIdAndUserId(certificateId, currentUser.getId());

        if (certOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Certificate cert = certOpt.get();
        byte[] pdfBytes = certificateService.getCertificatePdfBytes(cert);
        String safeCourse = CertificatePdfService.sanitizeFilename(cert.getCourse() != null ? cert.getCourse().getTitle() : "Course");
        String filename = "Certificate-" + safeCourse + "-" + cert.getCertificateNumber() + ".pdf";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(
            ContentDisposition.attachment().filename(filename).build()
        );
        headers.setContentLength(pdfBytes.length);

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

    /**
     * View certificate PDF in browser (inline).
     * GET /api/certificates/{certificateId}/view
     */
    @GetMapping("/{certificateId}/view")
    public ResponseEntity<?> viewCertificatePdf(@PathVariable Long certificateId, Principal principal) {
        if (principal == null || principal.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User currentUser = userRepository.findByEmailIgnoreCase(principal.getName())
            .orElseThrow(() -> new IllegalArgumentException("Authenticated user not found"));

        boolean isAdmin = currentUser.getRole() != null && "ADMIN".equalsIgnoreCase(currentUser.getRole().name());
        Optional<Certificate> certOpt = isAdmin
            ? certificateService.getCertificateById(certificateId)
            : certificateService.getCertificateByIdAndUserId(certificateId, currentUser.getId());

        if (certOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Certificate cert = certOpt.get();
        byte[] pdfBytes = certificateService.getCertificatePdfBytes(cert);
        String safeCourse = CertificatePdfService.sanitizeFilename(cert.getCourse() != null ? cert.getCourse().getTitle() : "Course");
        String filename = "Certificate-" + safeCourse + "-" + cert.getCertificateNumber() + ".pdf";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(
            ContentDisposition.inline().filename(filename).build()
        );
        headers.setContentLength(pdfBytes.length);

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

    /**
     * Testing / Demo PDF download endpoint (as requested in prompt for Postman/Browser verification).
     * GET /api/certificates/download
     */
    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadTestCertificate() {
        byte[] pdfBytes = certificateService.generateSampleCertificatePdf();
        String filename = "Certificate-CERT-2026-000001.pdf";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(
            ContentDisposition.attachment().filename(filename).build()
        );
        headers.setContentLength(pdfBytes.length);

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

    /**
     * Sample Certificate download endpoint for learners to test design before completion.
     * GET /api/certificates/sample/download
     */
    @GetMapping("/sample/download")
    public ResponseEntity<byte[]> downloadSampleCertificate() {
        byte[] pdfBytes = certificateService.generateSampleCertificatePdf();
        String filename = "Ingage-Sample-Certificate-Full-Stack-Development.pdf";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(
            ContentDisposition.attachment().filename(filename).build()
        );
        headers.setContentLength(pdfBytes.length);

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

    /**
     * Public Certificate Verification endpoint. Returns safe metadata.
     * GET /api/certificates/verify/{certificateNumber}
     */
    @GetMapping("/verify/{certificateNumber}")
    public ResponseEntity<?> verifyCertificate(@PathVariable String certificateNumber) {
        Optional<Certificate> certOpt = certificateService.findByCodeOrNumber(certificateNumber);
        if (certOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "valid", false,
                "message", "Certificate not found or invalid certificate ID."
            ));
        }

        Certificate cert = certOpt.get();
        java.time.Instant compDate = cert.getCompletionDate() != null ? cert.getCompletionDate() : cert.getIssuedAt();
        String formattedDate = DateTimeFormatter.ofPattern("dd MMMM yyyy")
            .withZone(ZoneId.systemDefault())
            .format(compDate != null ? compDate : java.time.Instant.now());

        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("valid", true);
        resp.put("certificateNumber", cert.getCertificateNumber());
        resp.put("verificationCode", cert.getVerificationCode());
        resp.put("studentName", cert.getStudentName());
        resp.put("courseName", cert.getCourseName());
        resp.put("courseCategory", cert.getCourse().getCategory() != null ? cert.getCourse().getCategory() : "Professional Learning");
        resp.put("courseDuration", cert.getCourse().getDuration() != null ? cert.getCourse().getDuration() : "");
        resp.put("instructor", cert.getInstructorName() != null ? cert.getInstructorName() : "");
        resp.put("completionDate", compDate != null ? compDate.toString() : "");
        resp.put("formattedDate", formattedDate);
        resp.put("status", cert.getStatus().name());
        return ResponseEntity.ok(resp);
    }

    /**
     * Fetch certificate for a specific course if already completed.
     * GET /api/certificates/course/{courseId}
     */
    @GetMapping("/course/{courseId}")
    public ResponseEntity<?> getCourseCertificate(@PathVariable Long courseId, Principal principal) {
        if (principal == null || principal.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User currentUser = userRepository.findByEmailIgnoreCase(principal.getName())
            .orElseThrow(() -> new IllegalArgumentException("Authenticated user not found"));

        Optional<Certificate> certOpt = certificateService.getUserCertificateForCourse(currentUser.getId(), courseId);
        if (certOpt.isEmpty()) {
            // Check if user completed the course, and auto-generate if so
            try {
                Certificate newCert = certificateService.generateCertificate(currentUser.getId(), courseId);
                return ResponseEntity.ok(buildCertificateDto(newCert));
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "issued", false,
                    "message", "No certificate found for this course."
                ));
            }
        }

        return ResponseEntity.ok(buildCertificateDto(certOpt.get()));
    }

    private Map<String, Object> buildCertificateDto(Certificate c) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", c.getId());
        dto.put("certificateNumber", c.getCertificateNumber());
        dto.put("verificationCode", c.getVerificationCode());
        dto.put("studentName", c.getStudentName());
        dto.put("courseId", c.getCourse().getId());
        dto.put("courseTitle", c.getCourseName());
        dto.put("courseName", c.getCourseName());
        dto.put("courseCategory", c.getCourse().getCategory() != null ? c.getCourse().getCategory() : "Professional Learning");
        dto.put("courseDuration", c.getCourse().getDuration() != null ? c.getCourse().getDuration() : "");
        dto.put("instructor", c.getInstructorName() != null ? c.getInstructorName() : "");
        dto.put("issuedAt", c.getIssuedAt() != null ? c.getIssuedAt().toString() : "");

        java.time.Instant compDate = c.getCompletionDate() != null ? c.getCompletionDate() : c.getIssuedAt();
        dto.put("completionDate", compDate != null ? compDate.toString() : "");

        String formattedDate = DateTimeFormatter.ofPattern("dd MMMM yyyy")
            .withZone(ZoneId.systemDefault())
            .format(compDate != null ? compDate : java.time.Instant.now());
        dto.put("formattedDate", formattedDate);

        dto.put("status", c.getStatus().name());
        dto.put("verificationUrl", "/certificate/verify/" + c.getVerificationCode());
        dto.put("downloadUrl", "/api/certificates/" + c.getId() + "/download");
        dto.put("viewUrl", "/api/certificates/" + c.getId() + "/view");
        return dto;
    }
}
