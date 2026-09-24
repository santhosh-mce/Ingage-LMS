package com.lms.Backend.learning.controller;

import com.lms.Backend.course.entity.Course;
import com.lms.Backend.course.entity.CourseLesson;
import com.lms.Backend.course.repository.CourseLessonRepository;
import com.lms.Backend.learning.entity.Enrollment;
import com.lms.Backend.learning.repository.EnrollmentRepository;
import com.lms.Backend.learning.service.VideoService;
import com.lms.Backend.user.entity.User;
import com.lms.Backend.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/learning")
public class LearningController {

    private static final Logger log = LoggerFactory.getLogger(LearningController.class);

    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final CourseLessonRepository courseLessonRepository;
    private final VideoService videoService;
    private final com.lms.Backend.course.service.CourseAccessService courseAccessService;

    public LearningController(
        EnrollmentRepository enrollmentRepository,
        UserRepository userRepository,
        CourseLessonRepository courseLessonRepository,
        VideoService videoService,
        com.lms.Backend.course.service.CourseAccessService courseAccessService
    ) {
        this.enrollmentRepository = enrollmentRepository;
        this.userRepository = userRepository;
        this.courseLessonRepository = courseLessonRepository;
        this.videoService = videoService;
        this.courseAccessService = courseAccessService;
    }

    @GetMapping("/my-enrollments")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Map<String, Object>>> getMyEnrollments(Principal principal) {
        User user = userRepository.findByEmailIgnoreCase(principal.getName())
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<Enrollment> enrollments = enrollmentRepository.findByUserIdOrderByEnrolledAtDesc(user.getId());
        List<Map<String, Object>> result = new ArrayList<>();

        for (Enrollment e : enrollments) {
            Map<String, Object> dto = new LinkedHashMap<>();
            dto.put("id", e.getId());
            dto.put("courseId", e.getCourse().getId());
            dto.put("courseTitle", e.getCourse().getTitle());
            dto.put("courseSlug", e.getCourse().getSlug());
            dto.put("thumbnail", e.getCourse().getThumbnail());
            dto.put("category", e.getCourse().getCategory());
            dto.put("level", e.getCourse().getLevel());
            dto.put("duration", e.getCourse().getDuration());
            dto.put("instructor", e.getCourse().getInstructor());
            dto.put("status", e.getStatus().name());
            dto.put("progressPercentage", e.getProgressPercentage());
            dto.put("enrolledAt", e.getEnrolledAt() != null ? e.getEnrolledAt().toString() : null);
            dto.put("lastAccessedAt", e.getLastAccessedAt() != null ? e.getLastAccessedAt().toString() : null);

            // Access source tagging
            try {
                com.lms.Backend.course.dto.CourseAccessDetailDto accessDetail = courseAccessService.getCourseAccessDetail(user.getId(), e.getCourse().getId());
                dto.put("accessType", accessDetail.getAccessType());
                dto.put("careerPathId", accessDetail.getCareerPathId());
                dto.put("careerPathName", accessDetail.getCareerPathName());
                dto.put("careerPathSlug", accessDetail.getCareerPathSlug());
            } catch (Exception ignored) {}

            result.add(dto);
        }

        return ResponseEntity.ok(result);
    }

    /**
     * Protected HTTP Range Video Streaming Endpoint.
     * Accessible via /api/learning/lessons/{lessonId}/video or /api/learning/{lessonId}/video.
     * Uses VideoService abstraction:
     * - Returns lesson-specific video if configured/available.
     * - Seamlessly falls back to default development video (uploads/videos/video.mp4).
     * - Validates user authentication & course enrollment.
     */
    @GetMapping({"/lessons/{lessonId}/video", "/{lessonId}/video"})
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public ResponseEntity<ResourceRegion> streamLessonVideo(
        @PathVariable Long lessonId,
        @RequestHeader HttpHeaders headers,
        Principal principal
    ) throws IOException {
        return streamLessonVideo(null, lessonId, headers, principal);
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public ResponseEntity<ResourceRegion> streamLessonVideo(
        Long courseId,
        Long lessonId,
        HttpHeaders headers,
        Principal principal
    ) throws IOException {
        Optional<CourseLesson> lessonOpt = courseLessonRepository.findByIdWithSectionAndCourse(lessonId);
        if (lessonOpt.isEmpty()) {
            log.warn("[VideoSecurity] Lesson not found for lessonId={}", lessonId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        CourseLesson lesson = lessonOpt.get();
        Course course = lesson.getSection() != null ? lesson.getSection().getCourse() : null;
        if (course == null) {
            log.warn("[VideoSecurity] Course not found for lessonId={}", lessonId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        if (courseId != null && !course.getId().equals(courseId)) {
            log.warn("[VideoSecurity] Mismatched courseId: requested={}, actual={}", courseId, course.getId());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        boolean isFreePreview = lesson.isFreePreview();
        boolean isFreeCourse = course.getPrice() != null && course.getPrice() == 0;

        // If not a free preview and not a free course, authentication and active enrollment are strictly required
        if (!isFreePreview && !isFreeCourse) {
            if (principal == null || principal.getName() == null) {
                log.warn("[VideoSecurity] Access denied: Unauthenticated request for protected lessonId={}", lessonId);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            User user = userRepository.findByEmailIgnoreCase(principal.getName()).orElse(null);
            if (user == null) {
                log.warn("[VideoSecurity] Access denied: User not found for email={} (lessonId={})", principal.getName(), lessonId);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            boolean isAdmin = user.getRole() != null && "ADMIN".equalsIgnoreCase(user.getRole().name());
            boolean hasAccess = isAdmin || courseAccessService.hasCourseAccess(user.getId(), course.getId());

            if (!hasAccess) {
                log.warn("[VideoSecurity] Access forbidden: User '{}' does not have access to course '{}' (id={}) for lessonId={}",
                    user.getEmail(), course.getTitle(), course.getId(), lessonId);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            log.info("[VideoSecurity] Access granted: User='{}' streaming protected video for lessonId={}, courseId={}",
                user.getEmail(), lessonId, course.getId());
        } else {
            log.info("[VideoSecurity] Streaming free preview / free course video for lessonId={}, courseId={}",
                lessonId, course.getId());
        }

        // Resolve video resource via VideoService abstraction (supports future cloud storage & default fallback)
        Resource videoResource;
        try {
            videoResource = videoService.getVideoResource(lesson);
        } catch (FileNotFoundException e) {
            log.error("[VideoSecurity] Video resource missing for lessonId={}: {}", lessonId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        long contentLength = videoResource.contentLength();

        HttpRange range = headers.getRange().isEmpty() ? null : headers.getRange().get(0);
        ResourceRegion region;
        long chunkSize = 1024 * 1024L; // 1MB chunk size

        if (range != null) {
            long start = range.getRangeStart(contentLength);
            long end = range.getRangeEnd(contentLength);
            long rangeLength = Math.min(chunkSize, end - start + 1);
            region = new ResourceRegion(videoResource, start, rangeLength);
        } else {
            long rangeLength = Math.min(chunkSize, contentLength);
            region = new ResourceRegion(videoResource, 0, rangeLength);
        }

        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
            .contentType(MediaTypeFactory.getMediaType(videoResource).orElse(MediaType.valueOf("video/mp4")))
            .header(HttpHeaders.CACHE_CONTROL, "private, no-store, max-age=0, must-revalidate")
            .header(HttpHeaders.PRAGMA, "no-cache")
            .header("X-Content-Type-Options", "nosniff")
            .body(region);
    }
}