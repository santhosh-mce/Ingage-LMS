package com.lms.Backend.course.controller;

import com.lms.Backend.course.dto.CourseResponse;
import com.lms.Backend.course.service.CourseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/courses")
public class CourseController {

    private final CourseService courseService;
    private final com.lms.Backend.learning.controller.LearningController learningController;

    public CourseController(
        CourseService courseService,
        com.lms.Backend.learning.controller.LearningController learningController
    ) {
        this.courseService = courseService;
        this.learningController = learningController;
    }

    @GetMapping
    public ResponseEntity<List<CourseResponse>> getCourses(@RequestParam(required = false) String search) {
        return ResponseEntity.ok(courseService.getPublishedCourses(search));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseResponse> getCourseById(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    @GetMapping("/{id}/content")
    public ResponseEntity<java.util.Map<String, Object>> getCourseContent(
        @PathVariable Long id,
        java.security.Principal principal
    ) {
        return ResponseEntity.ok(courseService.getCourseContent(id, principal));
    }

    @GetMapping("/{id}/enrollment-status")
    public ResponseEntity<com.lms.Backend.course.dto.CourseEnrollmentStatusResponse> getEnrollmentStatus(
        @PathVariable Long id,
        java.security.Principal principal
    ) {
        return ResponseEntity.ok(courseService.getEnrollmentStatus(id, principal));
    }

    @GetMapping("/{id}/learn")
    public ResponseEntity<java.util.Map<String, Object>> getCourseLearn(
        @PathVariable Long id,
        java.security.Principal principal
    ) {
        return ResponseEntity.ok(courseService.getCourseLearnAccess(id, principal));
    }

    @GetMapping("/{courseId}/lessons/{lessonId}/video")
    public ResponseEntity<org.springframework.core.io.support.ResourceRegion> streamCourseLessonVideo(
        @PathVariable Long courseId,
        @PathVariable Long lessonId,
        @org.springframework.web.bind.annotation.RequestHeader org.springframework.http.HttpHeaders headers,
        java.security.Principal principal
    ) throws java.io.IOException {
        return learningController.streamLessonVideo(courseId, lessonId, headers, principal);
    }
}
