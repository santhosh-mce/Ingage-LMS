package com.lms.Backend.career.service;

import com.lms.Backend.career.entity.Career;
import com.lms.Backend.career.entity.CareerCourse;
import com.lms.Backend.career.entity.CareerEnrollment;
import com.lms.Backend.career.repository.CareerCourseRepository;
import com.lms.Backend.career.repository.CareerEnrollmentRepository;
import com.lms.Backend.career.repository.CareerRepository;
import com.lms.Backend.certificate.entity.Certificate;
import com.lms.Backend.certificate.repository.CertificateRepository;
import com.lms.Backend.course.entity.Course;
import com.lms.Backend.learning.entity.Enrollment;
import com.lms.Backend.learning.entity.EnrollmentStatus;
import com.lms.Backend.learning.repository.EnrollmentRepository;
import com.lms.Backend.user.entity.User;
import com.lms.Backend.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.Year;
import java.util.*;

@Service
@Transactional
public class CareerProgressService {

    private static final Logger log = LoggerFactory.getLogger(CareerProgressService.class);

    private final CareerEnrollmentRepository careerEnrollmentRepository;
    private final CareerCourseRepository careerCourseRepository;
    private final CareerRepository careerRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CertificateRepository certificateRepository;
    private final UserRepository userRepository;

    public CareerProgressService(
        CareerEnrollmentRepository careerEnrollmentRepository,
        CareerCourseRepository careerCourseRepository,
        CareerRepository careerRepository,
        EnrollmentRepository enrollmentRepository,
        CertificateRepository certificateRepository,
        UserRepository userRepository
    ) {
        this.careerEnrollmentRepository = careerEnrollmentRepository;
        this.careerCourseRepository = careerCourseRepository;
        this.careerRepository = careerRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.certificateRepository = certificateRepository;
        this.userRepository = userRepository;
    }

    /**
     * Calculates the learner's progress in a Career Path including required courses
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getCareerProgressDetail(UUID userId, Long careerId) {
        Career career = careerRepository.findById(careerId)
            .orElseThrow(() -> new IllegalArgumentException("Career path not found with id: " + careerId));

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("careerId", career.getId());
        result.put("careerTitle", career.getTitle());
        result.put("careerSlug", career.getSlug());

        if (userId == null) {
            result.put("enrolled", false);
            result.put("progressPercentage", 0);
            result.put("completed", false);
            result.put("certificateAvailable", false);
            return result;
        }

        Optional<CareerEnrollment> enrollmentOpt = careerEnrollmentRepository.findByUserIdAndCareerId(userId, careerId);
        boolean isEnrolled = enrollmentOpt.isPresent();
        CareerEnrollment enrollment = enrollmentOpt.orElse(null);

        result.put("enrolled", isEnrolled);
        result.put("status", isEnrolled ? enrollment.getStatus().name() : "NOT_ENROLLED");

        // Check required courses completion
        List<CareerCourse> mappedCourses = careerCourseRepository.findByCareerIdOrderByDisplayOrderAsc(careerId);
        List<Map<String, Object>> coursesStatus = new ArrayList<>();
        int requiredCount = 0;
        int completedRequiredCount = 0;

        for (CareerCourse cc : mappedCourses) {
            Course c = cc.getCourse();
            if (c == null) continue;

            boolean isRequired = cc.isRequiredForCompletion();
            boolean isIncluded = cc.isIncluded();
            if (isRequired) requiredCount++;

            Optional<Enrollment> cEnrollOpt = enrollmentRepository.findByUserIdAndCourseId(userId, c.getId());
            int courseProgress = 0;
            boolean courseCompleted = false;

            if (cEnrollOpt.isPresent()) {
                Enrollment ce = cEnrollOpt.get();
                courseProgress = ce.getProgressPercentage() != null ? ce.getProgressPercentage() : 0;
                courseCompleted = ce.getStatus() == EnrollmentStatus.COMPLETED || courseProgress >= 100;
            }

            if (isRequired && courseCompleted) {
                completedRequiredCount++;
            }

            Map<String, Object> cInfo = new LinkedHashMap<>();
            cInfo.put("courseId", c.getId());
            cInfo.put("courseTitle", c.getTitle());
            cInfo.put("isIncluded", isIncluded);
            cInfo.put("isRequiredForCompletion", isRequired);
            cInfo.put("progress", courseProgress);
            cInfo.put("completed", courseCompleted);
            coursesStatus.add(cInfo);
        }

        int calculatedProgress = 0;
        if (requiredCount > 0) {
            calculatedProgress = Math.round(((float) completedRequiredCount / requiredCount) * 100);
        } else if (isEnrolled) {
            calculatedProgress = enrollment.getProgressPercentage() != null ? enrollment.getProgressPercentage() : 0;
        }

        boolean isCompleted = isEnrolled && (calculatedProgress >= 100 || enrollment.getStatus() == EnrollmentStatus.COMPLETED);

        // Update enrollment progress if changed
        if (isEnrolled && enrollment.getProgressPercentage() != calculatedProgress) {
            enrollment.setProgressPercentage(calculatedProgress);
            if (calculatedProgress >= 100 && enrollment.getStatus() != EnrollmentStatus.COMPLETED) {
                enrollment.setStatus(EnrollmentStatus.COMPLETED);
                enrollment.setCompletedAt(Instant.now());
            }
            careerEnrollmentRepository.save(enrollment);
        }

        boolean certAvailable = certificateRepository.existsByUserIdAndCareerId(userId, careerId);

        result.put("progressPercentage", calculatedProgress);
        result.put("completed", isCompleted);
        result.put("certificateAvailable", certAvailable || isCompleted);
        result.put("requiredCoursesCount", requiredCount);
        result.put("completedRequiredCoursesCount", completedRequiredCount);
        result.put("courses", coursesStatus);

        return result;
    }

    /**
     * Issues a verified Career Path Certificate upon 100% completion
     */
    public Certificate claimCareerCertificate(UUID userId, Long careerId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Career career = careerRepository.findById(careerId)
            .orElseThrow(() -> new IllegalArgumentException("Career path not found: " + careerId));

        CareerEnrollment enrollment = careerEnrollmentRepository.findByUserIdAndCareerId(userId, careerId)
            .orElseThrow(() -> new IllegalArgumentException("User is not enrolled in this career path"));

        // Check if certificate already issued
        Optional<Certificate> existing = certificateRepository.findByUserIdAndCareerId(userId, careerId);
        if (existing.isPresent()) {
            return existing.get();
        }

        // Verify completion
        Map<String, Object> progressDetail = getCareerProgressDetail(userId, careerId);
        int progress = (Integer) progressDetail.get("progressPercentage");
        if (progress < 100 && enrollment.getStatus() != EnrollmentStatus.COMPLETED) {
            throw new IllegalStateException("Career path requirements are not yet complete. Current progress: " + progress + "%");
        }

        // Generate unique certificate ID in format ING-DA-2026-XXXXXX
        String prefix = "ING-";
        if (career.getSlug() != null && career.getSlug().contains("data-analyst")) {
            prefix += "DA-";
        } else if (career.getSlug() != null && career.getSlug().contains("data-scientist")) {
            prefix += "DS-";
        } else if (career.getSlug() != null) {
            String code = career.getSlug().replace("-", "").toUpperCase();
            prefix += code.substring(0, Math.min(3, code.length())) + "-";
        } else {
            prefix += "CP-";
        }

        String year = String.valueOf(Year.now().getValue());
        String randomSuffix = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        String certNumber = prefix + year + "-" + randomSuffix;
        String verificationCode = UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();

        Certificate certificate = new Certificate(certNumber, verificationCode, user, career);
        certificate.setCompletionDate(Instant.now());
        certificate.setIssuedAt(Instant.now());
        certificate.setCourseName(career.getTitle() + " Career Path");
        certificate.setInstructorName("InGage Industry Mentorship Board");

        Certificate saved = certificateRepository.save(certificate);

        enrollment.setStatus(EnrollmentStatus.COMPLETED);
        enrollment.setCompletedAt(Instant.now());
        enrollment.setProgressPercentage(100);
        careerEnrollmentRepository.save(enrollment);

        log.info("[CareerProgressService] Issued career certificate {} for user {} on career {}",
            saved.getCertificateNumber(), user.getEmail(), career.getTitle());

        return saved;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getUserCareerEnrollments(UUID userId) {
        if (userId == null) return Collections.emptyList();
        List<CareerEnrollment> list = careerEnrollmentRepository.findByUserIdOrderByEnrolledAtDesc(userId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (CareerEnrollment ce : list) {
            Map<String, Object> map = new LinkedHashMap<>();
            Career c = ce.getCareer();
            if (c == null) continue;
            map.put("id", ce.getId());
            map.put("careerId", c.getId());
            map.put("title", c.getTitle());
            map.put("slug", c.getSlug());
            map.put("category", c.getCategory());
            map.put("level", c.getLevel());
            map.put("duration", c.getDuration());
            map.put("status", ce.getStatus().name());
            map.put("progressPercentage", ce.getProgressPercentage());
            map.put("enrolledAt", ce.getEnrolledAt() != null ? ce.getEnrolledAt().toString() : null);
            map.put("completedAt", ce.getCompletedAt() != null ? ce.getCompletedAt().toString() : null);
            result.add(map);
        }
        return result;
    }
}
