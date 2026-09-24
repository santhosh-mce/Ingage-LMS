package com.lms.Backend.admin.service;

import com.lms.Backend.certificate.entity.Certificate;
import com.lms.Backend.certificate.repository.CertificateRepository;
import com.lms.Backend.certificate.service.CertificateService;
import com.lms.Backend.course.entity.CourseLesson;
import com.lms.Backend.course.entity.LessonType;
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

    @Transactional(readOnly = true)
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
    public Map<String, Object> saveProgress(UUID userId, Long lessonId, Map<String, Object> payload) {
        CourseLesson lesson = courseLessonRepository.findById(lessonId)
            .orElseThrow(() -> new IllegalArgumentException("Lesson not found"));

        Long courseId = lesson.getSection().getCourse().getId();
        Enrollment enrollment = enrollmentRepository.findByUserIdAndCourseId(userId, courseId)
            .orElseThrow(() -> new IllegalArgumentException("User not enrolled in this course"));

        LessonProgress lp = lessonProgressRepository.findByEnrollmentIdAndLessonId(enrollment.getId(), lessonId)
            .orElseGet(() -> new LessonProgress(enrollment, lesson, false));

        if (!lp.isCompleted() && payload != null) {
            int newWatch = 0;
            if (payload.get("watchDurationSeconds") != null) {
                newWatch = ((Number) payload.get("watchDurationSeconds")).intValue();
            } else if (payload.get("currentTime") != null) {
                newWatch = (int) Math.round(((Number) payload.get("currentTime")).doubleValue());
            }
            int existing = lp.getWatchDurationSeconds() != null ? lp.getWatchDurationSeconds() : 0;
            lp.setWatchDurationSeconds(Math.max(existing, newWatch));
            lp.setLastAccessedAt(Instant.now());
            lessonProgressRepository.save(lp);
        }

        enrollment.setLastAccessedAt(Instant.now());
        enrollmentRepository.save(enrollment);

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("saved", true);
        res.put("watchDurationSeconds", lp.getWatchDurationSeconds() != null ? lp.getWatchDurationSeconds() : 0);
        res.put("completed", lp.isCompleted());
        return res;
    }

    @Transactional
    public Map<String, Object> markLessonComplete(UUID userId, Long lessonId) {
        return markLessonComplete(userId, lessonId, null);
    }

    @Transactional
    public Map<String, Object> markLessonComplete(UUID userId, Long lessonId, Map<String, Object> payload) {
        CourseLesson lesson = courseLessonRepository.findById(lessonId)
            .orElseThrow(() -> new IllegalArgumentException("Lesson not found"));

        Long courseId = lesson.getSection().getCourse().getId();
        Enrollment enrollment = enrollmentRepository.findByUserIdAndCourseId(userId, courseId)
            .orElseThrow(() -> new IllegalArgumentException("User not enrolled in this course"));

        // Find or create lesson progress
        LessonProgress lp = lessonProgressRepository.findByEnrollmentIdAndLessonId(enrollment.getId(), lessonId)
            .orElseGet(() -> new LessonProgress(enrollment, lesson, false));

        // If already completed, return existing progress status and certificate info
        if (lp.isCompleted()) {
            Optional<Certificate> existingCert = certificateRepository.findByUserIdAndCourseId(userId, courseId);
            Map<String, Object> already = new LinkedHashMap<>();
            already.put("progressPercentage", enrollment.getProgressPercentage() != null ? enrollment.getProgressPercentage() : 0);
            already.put("progress", enrollment.getProgressPercentage() != null ? enrollment.getProgressPercentage() : 0);
            already.put("completed", enrollment.getStatus() == EnrollmentStatus.COMPLETED);
            already.put("courseCompleted", enrollment.getStatus() == EnrollmentStatus.COMPLETED);
            already.put("newlyCompleted", false);
            already.put("alreadyCompleted", true);
            already.put("lessonId", lessonId);
            already.put("lessonCompleted", true);
            already.put("certificateGenerated", existingCert.isPresent());
            already.put("certificateId", existingCert.map(Certificate::getId).orElse(null));
            already.put("certificateNumber", existingCert.map(Certificate::getCertificateNumber).orElse(null));
            already.put("verificationCode", existingCert.map(Certificate::getVerificationCode).orElse(null));
            return already;
        }

        // Validate completion for VIDEO lessons
        if (lesson.getLessonType() == LessonType.VIDEO) {
            double duration = 0.0;
            if (payload != null && payload.get("duration") != null) {
                duration = ((Number) payload.get("duration")).doubleValue();
            } else if (lesson.getDurationSeconds() != null && lesson.getDurationSeconds() > 0) {
                duration = lesson.getDurationSeconds();
            }

            double currentTime = 0.0;
            if (payload != null && payload.get("currentTime") != null) {
                currentTime = ((Number) payload.get("currentTime")).doubleValue();
            }

            int watchDuration = lp.getWatchDurationSeconds() != null ? lp.getWatchDurationSeconds() : 0;
            if (payload != null && payload.get("watchDurationSeconds") != null) {
                int sentWatch = ((Number) payload.get("watchDurationSeconds")).intValue();
                watchDuration = Math.max(watchDuration, sentWatch);
            }

            // Enforce validation if video duration is known
            if (duration > 0) {
                // Must reach within 5 seconds of the end of video (or past duration)
                if (currentTime < duration - 5.0) {
                    throw new IllegalArgumentException("Video lesson has not reached 100% completion (playback position: " 
                        + (int)currentTime + "s / " + (int)duration + "s).");
                }
                // Must have watched at least 85% of duration
                if (watchDuration < duration * 0.85) {
                    throw new IllegalArgumentException("Video lesson completion requires watching the video (watched: " 
                        + watchDuration + "s of " + (int)duration + "s).");
                }
            }

            lp.setWatchDurationSeconds((int) Math.max(watchDuration, duration > 0 ? duration : watchDuration));
        }

        lp.setCompleted(true);
        lp.setCompletedAt(Instant.now());
        lp.setLastAccessedAt(Instant.now());
        lessonProgressRepository.save(lp);

        // Calculate progress percentage based on all curriculum lessons
        long totalLessons = courseLessonRepository.countBySection_Course_Id(courseId);
        long completedLessons = 0;
        List<LessonProgress> allProgress = lessonProgressRepository.findByEnrollmentId(enrollment.getId());
        for (LessonProgress p : allProgress) {
            if (p.isCompleted()) {
                completedLessons++;
            }
        }

        int progressPercentage = totalLessons > 0 ? (int) Math.min(100, Math.round(((double) completedLessons / totalLessons) * 100.0)) : 100;
        enrollment.setProgressPercentage(progressPercentage);
        enrollment.setLastAccessedAt(Instant.now());

        boolean newlyCompleted = false;
        Certificate cert = null;

        // When completion reaches 100%:
        // 1. Mark enrollment as COMPLETED.
        // 2. Store completion date.
        // 3. Generate certificate idempotently.
        // 4. Save certificate record in PostgreSQL.
        // 5. Notify user.
        if (progressPercentage >= 100) {
            if (enrollment.getStatus() != EnrollmentStatus.COMPLETED) {
                enrollment.setStatus(EnrollmentStatus.COMPLETED);
                enrollment.setCompletedAt(Instant.now());
                newlyCompleted = true;
            }

            // Generate certificate idempotently
            cert = certificateService.generateCertificate(userId, courseId);
        }

        enrollmentRepository.save(enrollment);

        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("progressPercentage", progressPercentage);
        resp.put("progress", progressPercentage);
        resp.put("completed", enrollment.getStatus() == EnrollmentStatus.COMPLETED);
        resp.put("courseCompleted", enrollment.getStatus() == EnrollmentStatus.COMPLETED);
        resp.put("newlyCompleted", newlyCompleted);
        resp.put("lessonId", lessonId);
        resp.put("lessonCompleted", true);
        resp.put("certificateGenerated", cert != null);
        resp.put("certificateId", cert != null ? cert.getId() : null);
        resp.put("certificateNumber", cert != null ? cert.getCertificateNumber() : null);
        resp.put("verificationCode", cert != null ? cert.getVerificationCode() : null);
        return resp;
    }
}
