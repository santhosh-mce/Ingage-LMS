package com.lms.Backend.certificate.service;

import com.lms.Backend.certificate.entity.Certificate;
import com.lms.Backend.certificate.entity.CertificateStatus;
import com.lms.Backend.certificate.repository.CertificateRepository;
import com.lms.Backend.course.entity.Course;
import com.lms.Backend.course.entity.CourseLesson;
import com.lms.Backend.course.entity.LessonType;
import com.lms.Backend.course.repository.CourseLessonRepository;
import com.lms.Backend.course.repository.CourseRepository;
import com.lms.Backend.learning.entity.Enrollment;
import com.lms.Backend.learning.entity.EnrollmentStatus;
import com.lms.Backend.learning.entity.LessonProgress;
import com.lms.Backend.learning.repository.EnrollmentRepository;
import com.lms.Backend.learning.repository.LessonProgressRepository;
import com.lms.Backend.notification.entity.Notification;
import com.lms.Backend.notification.repository.NotificationRepository;
import com.lms.Backend.user.entity.User;
import com.lms.Backend.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CertificateService {

    private static final Logger log = LoggerFactory.getLogger(CertificateService.class);

    private final CertificateRepository certificateRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CourseLessonRepository courseLessonRepository;
    private final LessonProgressRepository lessonProgressRepository;
    private final NotificationRepository notificationRepository;
    private final CertificatePdfService certificatePdfService;

    public CertificateService(
        CertificateRepository certificateRepository,
        UserRepository userRepository,
        CourseRepository courseRepository,
        EnrollmentRepository enrollmentRepository,
        CourseLessonRepository courseLessonRepository,
        LessonProgressRepository lessonProgressRepository,
        NotificationRepository notificationRepository,
        CertificatePdfService certificatePdfService
    ) {
        this.certificateRepository = certificateRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.courseLessonRepository = courseLessonRepository;
        this.lessonProgressRepository = lessonProgressRepository;
        this.notificationRepository = notificationRepository;
        this.certificatePdfService = certificatePdfService;
    }

    /**
     * Strictly verifies course completion requirements and issues/returns certificate.
     * Idempotent: If certificate already exists, returns existing certificate.
     */
    @Transactional
    public Certificate generateCertificate(UUID userId, Long courseId) {
        // 1. Check if certificate already exists (Idempotent duplicate prevention)
        Optional<Certificate> existing = certificateRepository.findByUserIdAndCourseId(userId, courseId);
        if (existing.isPresent()) {
            Certificate cert = existing.get();
            // Ensure PDF is generated on disk
            if (cert.getPdfPath() == null || cert.getPdfPath().isBlank()) {
                certificatePdfService.generateAndStoreCertificatePdf(cert);
                certificateRepository.save(cert);
            }
            return cert;
        }

        // 2. Verify User & Course exist
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new IllegalArgumentException("Course not found: " + courseId));

        // 3. Verify Enrollment
        Enrollment enrollment = enrollmentRepository.findByUserIdAndCourseId(userId, courseId)
            .orElseThrow(() -> new IllegalArgumentException("Student is not enrolled in course: " + course.getTitle()));

        // 4. Verify Course Completion (Lessons & Quizzes)
        long totalLessons = courseLessonRepository.countBySection_Course_Id(courseId);
        long completedLessons = lessonProgressRepository.countByEnrollmentIdAndCompletedTrue(enrollment.getId());

        boolean isEnrollmentMarkedDone = enrollment.getStatus() == EnrollmentStatus.COMPLETED ||
                (enrollment.getProgressPercentage() != null && enrollment.getProgressPercentage() >= 100);

        if (!isEnrollmentMarkedDone) {
            // Check if all lessons are completed
            if (totalLessons > 0 && completedLessons < totalLessons) {
                throw new IllegalStateException(
                    String.format("Course requirements not met. Only %d of %d lessons completed (100%% required).",
                        completedLessons, totalLessons)
                );
            }

            // Verify any required Quiz lessons
            List<CourseLesson> allLessons = courseLessonRepository.findBySection_Course_IdOrderBySection_DisplayOrderAscDisplayOrderAsc(courseId);
            for (CourseLesson lesson : allLessons) {
                if (lesson.getLessonType() == LessonType.QUIZ) {
                    Optional<LessonProgress> qp = lessonProgressRepository.findByEnrollmentIdAndLessonId(enrollment.getId(), lesson.getId());
                    if (qp.isEmpty() || !qp.get().isCompleted()) {
                        throw new IllegalStateException("Final quiz assessment '" + lesson.getTitle() + "' must be completed to earn certificate.");
                    }
                }
            }

            // Mark enrollment as COMPLETED in backend
            enrollment.setStatus(EnrollmentStatus.COMPLETED);
            enrollment.setProgressPercentage(100);
            if (enrollment.getCompletedAt() == null) {
                enrollment.setCompletedAt(Instant.now());
            }
            enrollmentRepository.save(enrollment);
        }

        // 5. Generate Unique Certificate Number and Verification Code (e.g. ING-2026-7842, ING-2026-7843)
        int year = LocalDate.now().getYear();
        long seq = 7842 + certificateRepository.count();
        String certificateNumber;
        do {
            certificateNumber = String.format("ING-%d-%04d", year, seq);
            seq++;
        } while (certificateRepository.existsByCertificateNumberIgnoreCase(certificateNumber));

        String verificationCode = certificateNumber;

        Certificate certificate = new Certificate(certificateNumber, verificationCode, user, course);
        Instant compDate = enrollment.getCompletedAt() != null ? enrollment.getCompletedAt() : Instant.now();
        certificate.setCompletionDate(compDate);
        certificate.setCertificateUrl("/certificate/verify/" + verificationCode);
        certificate.setStatus(CertificateStatus.ACTIVE);

        // 6. Generate and Store PDF on disk
        certificatePdfService.generateAndStoreCertificatePdf(certificate);

        Certificate saved = certificateRepository.save(certificate);

        // 7. Send in-app notification to learner
        try {
            Notification notification = new Notification(
                user,
                "Official Certificate Issued!",
                "Congratulations! You have completed " + course.getTitle() + ". Certificate ID: " + certificateNumber,
                "CERTIFICATE",
                "/certificate/verify/" + verificationCode
            );
            notificationRepository.save(notification);
        } catch (Exception e) {
            log.warn("Could not save certificate notification: {}", e.getMessage());
        }

        log.info("Certificate successfully issued: ID={}, Number={}, User={}, Course={}",
                saved.getId(), certificateNumber, user.getEmail(), course.getTitle());
        return saved;
    }

    public Optional<Certificate> getCertificateById(Long id) {
        return certificateRepository.findById(id);
    }

    public Optional<Certificate> getCertificateByIdAndUserId(Long id, UUID userId) {
        return certificateRepository.findByIdAndUserId(id, userId);
    }

    public Optional<Certificate> getCertificateByVerificationCode(String verificationCode) {
        if (verificationCode == null || verificationCode.trim().isEmpty()) return Optional.empty();
        return certificateRepository.findByVerificationCodeIgnoreCase(verificationCode.trim());
    }

    public Optional<Certificate> findByCodeOrNumber(String codeOrNumber) {
        if (codeOrNumber == null || codeOrNumber.trim().isEmpty()) return Optional.empty();
        String clean = codeOrNumber.trim();
        return certificateRepository.findByCertificateNumberIgnoreCaseOrVerificationCodeIgnoreCase(clean, clean);
    }

    public List<Certificate> getAllCertificates() {
        return certificateRepository.findAllByOrderByIssuedAtDesc();
    }

    public List<Certificate> getUserCertificates(UUID userId) {
        return certificateRepository.findByUserIdOrderByIssuedAtDesc(userId);
    }

    public Optional<Certificate> getUserCertificateForCourse(UUID userId, Long courseId) {
        return certificateRepository.findByUserIdAndCourseId(userId, courseId);
    }

    public byte[] getCertificatePdfBytes(Certificate certificate) {
        return certificatePdfService.getCertificatePdfBytes(certificate);
    }

    public byte[] generateCertificatePdf(Certificate certificate) {
        return certificatePdfService.getCertificatePdfBytes(certificate);
    }

    public byte[] generateSampleCertificatePdf() {
        return certificatePdfService.generateSampleCertificatePdf();
    }
}
