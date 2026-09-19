package com.lms.Backend.admin.service;

import com.lms.Backend.certificate.entity.Certificate;
import com.lms.Backend.certificate.repository.CertificateRepository;
import com.lms.Backend.certificate.service.CertificateService;
import com.lms.Backend.course.entity.CourseLesson;
import com.lms.Backend.course.repository.CourseLessonRepository;
import com.lms.Backend.learning.entity.Enrollment;
import com.lms.Backend.learning.entity.EnrollmentStatus;
import com.lms.Backend.learning.entity.LessonProgress;
import com.lms.Backend.learning.repository.EnrollmentRepository;
import com.lms.Backend.learning.repository.LessonProgressRepository;
import com.lms.Backend.notification.entity.Notification;
import com.lms.Backend.notification.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class AdminProgressService {

    private final EnrollmentRepository enrollmentRepository;
    private final LessonProgressRepository lessonProgressRepository;
    private final CourseLessonRepository courseLessonRepository;
    private final CertificateRepository certificateRepository;
    private final CertificateService certificateService;
    private final NotificationRepository notificationRepository;

    public AdminProgressService(
        EnrollmentRepository enrollmentRepository,
        LessonProgressRepository lessonProgressRepository,
        CourseLessonRepository courseLessonRepository,
        CertificateRepository certificateRepository,
        CertificateService certificateService,
        NotificationRepository notificationRepository
    ) {
        this.enrollmentRepository = enrollmentRepository;
        this.lessonProgressRepository = lessonProgressRepository;
        this.courseLessonRepository = courseLessonRepository;
        this.certificateRepository = certificateRepository;
        this.certificateService = certificateService;
        this.notificationRepository = notificationRepository;
    }

    public List<Map<String, Object>> getProgressList(String search, Long courseId, String statusFilter) {
        List<Enrollment> enrollments = enrollmentRepository.findAll();
        List<Map<String, Object>> result = new ArrayList<>();

        for (Enrollment e : enrollments) {
            if (courseId != null && !e.getCourse().getId().equals(courseId)) {
                continue;
            }

            if (statusFilter != null && !statusFilter.equalsIgnoreCase("ALL")) {
                if (statusFilter.equalsIgnoreCase("COMPLETED") && e.getStatus() != EnrollmentStatus.COMPLETED) {
                    continue;
                }
                if (statusFilter.equalsIgnoreCase("IN_PROGRESS") && e.getStatus() != EnrollmentStatus.ACTIVE) {
                    continue;
                }
            }

            if (search != null && !search.trim().isEmpty()) {
                String q = search.trim().toLowerCase();
                boolean matchName = e.getUser().getName().toLowerCase().contains(q);
                boolean matchEmail = e.getUser().getEmail().toLowerCase().contains(q);
                boolean matchCourse = e.getCourse().getTitle().toLowerCase().contains(q);
                if (!matchName && !matchEmail && !matchCourse) {
                    continue;
                }
            }

            long totalLessons = courseLessonRepository.countBySection_Course_Id(e.getCourse().getId());
            long completedLessons = lessonProgressRepository.countByEnrollmentIdAndCompletedTrue(e.getId());

            Map<String, Object> item = new LinkedHashMap<>();
            item.put("enrollmentId", e.getId());
            item.put("userId", e.getUser().getId());
            item.put("userName", e.getUser().getName());
            item.put("userEmail", e.getUser().getEmail());
            item.put("courseId", e.getCourse().getId());
            item.put("courseTitle", e.getCourse().getTitle());
            item.put("progress", e.getProgressPercentage());
            item.put("lessonsCompleted", completedLessons);
            item.put("lessonsTotal", totalLessons);
            item.put("status", e.getStatus().name());
            item.put("lastWatched", e.getLastAccessedAt() != null ? e.getLastAccessedAt().toString() : "");
            item.put("enrollmentDate", e.getEnrolledAt().toString());
            item.put("completionDate", e.getCompletedAt() != null ? e.getCompletedAt().toString() : null);

            Optional<Certificate> cert = certificateRepository.findByUserIdAndCourseId(e.getUser().getId(), e.getCourse().getId());
            item.put("certificateNumber", cert.map(Certificate::getCertificateNumber).orElse(null));
            item.put("verificationCode", cert.map(Certificate::getVerificationCode).orElse(null));

            result.add(item);
        }

        return result;
    }

    @Transactional
    public Map<String, Object> markLessonComplete(UUID userId, Long lessonId) {
        CourseLesson lesson = courseLessonRepository.findById(lessonId)
            .orElseThrow(() -> new IllegalArgumentException("Lesson not found"));

        Long courseId = lesson.getSection().getCourse().getId();
        Enrollment enrollment = enrollmentRepository.findByUserIdAndCourseId(userId, courseId)
            .orElseThrow(() -> new IllegalArgumentException("User not enrolled in this course"));

        // Find or create lesson progress
        LessonProgress lp = lessonProgressRepository.findByEnrollmentIdAndLessonId(enrollment.getId(), lessonId)
            .orElseGet(() -> new LessonProgress(enrollment, lesson, false));

        lp.setCompleted(true);
        lp.setCompletedAt(Instant.now());
        lp.setLastAccessedAt(Instant.now());
        lessonProgressRepository.save(lp);

        // Calculate progress percentage based on required lessons
        long totalRequired = courseLessonRepository.countBySection_Course_IdAndRequiredTrue(courseId);
        if (totalRequired == 0) {
            totalRequired = courseLessonRepository.countBySection_Course_Id(courseId);
        }

        long completedRequired = 0;
        List<LessonProgress> allProgress = lessonProgressRepository.findByEnrollmentId(enrollment.getId());
        for (LessonProgress p : allProgress) {
            if (p.isCompleted() && (p.getLesson().isRequired() || totalRequired == 0)) {
                completedRequired++;
            }
        }

        int progressPercentage = totalRequired > 0 ? (int) Math.min(100, Math.round(((double) completedRequired / totalRequired) * 100.0)) : 100;
        enrollment.setProgressPercentage(progressPercentage);
        enrollment.setLastAccessedAt(Instant.now());

        boolean newlyCompleted = false;
        Certificate cert = null;

        // When completion reaches 100%:
        // 1. Mark enrollment as COMPLETED.
        // 2. Store completion date.
        // 3. Generate certificate.
        // 4. Save certificate record.
        // 5. Notify user.
        if (progressPercentage >= 100 && enrollment.getStatus() != EnrollmentStatus.COMPLETED) {
            enrollment.setStatus(EnrollmentStatus.COMPLETED);
            enrollment.setCompletedAt(Instant.now());
            newlyCompleted = true;

            // Generate certificate idempotently
            cert = certificateService.generateCertificate(userId, courseId);
        }

        enrollmentRepository.save(enrollment);

        return Map.of(
            "progressPercentage", progressPercentage,
            "completed", enrollment.getStatus() == EnrollmentStatus.COMPLETED,
            "newlyCompleted", newlyCompleted,
            "certificateNumber", cert != null ? cert.getCertificateNumber() : null,
            "verificationCode", cert != null ? cert.getVerificationCode() : null
        );
    }
}
