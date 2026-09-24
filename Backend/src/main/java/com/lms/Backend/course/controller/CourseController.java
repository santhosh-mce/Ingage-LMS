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
    private final com.lms.Backend.course.service.QuizService quizService;
    private final com.lms.Backend.course.service.CourseAccessService courseAccessService;
    private final com.lms.Backend.user.repository.UserRepository userRepository;

    public CourseController(
        CourseService courseService,
        com.lms.Backend.learning.controller.LearningController learningController,
        com.lms.Backend.course.service.QuizService quizService,
        com.lms.Backend.course.service.CourseAccessService courseAccessService,
        com.lms.Backend.user.repository.UserRepository userRepository
    ) {
        this.courseService = courseService;
        this.learningController = learningController;
        this.quizService = quizService;
        this.courseAccessService = courseAccessService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<List<CourseResponse>> getCourses(
        @RequestParam(required = false) String search,
        @RequestParam(required = false) String keyword
    ) {
        String query = search != null && !search.isBlank() ? search : keyword;
        return ResponseEntity.ok(courseService.getPublishedCourses(query));
    }

    @GetMapping("/search")
    public ResponseEntity<List<CourseResponse>> searchCourses(
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) String search
    ) {
        String query = keyword != null && !keyword.isBlank() ? keyword : search;
        return ResponseEntity.ok(courseService.getPublishedCourses(query));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseResponse> getCourseById(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    @GetMapping("/{id}/access")
    public ResponseEntity<com.lms.Backend.course.dto.CourseAccessDetailDto> getCourseAccessDetail(
        @PathVariable Long id,
        java.security.Principal principal
    ) {
        java.util.UUID userId = null;
        if (principal != null && principal.getName() != null) {
            userId = userRepository.findByEmailIgnoreCase(principal.getName())
                .map(com.lms.Backend.user.entity.User::getId)
                .orElse(null);
        }
        return ResponseEntity.ok(courseAccessService.getCourseAccessDetail(userId, id));
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

    @GetMapping("/{courseId}/lessons/{lessonId}/quiz")
    public ResponseEntity<List<com.lms.Backend.course.dto.QuizQuestionDto>> getLessonQuiz(
        @PathVariable Long courseId,
        @PathVariable Long lessonId,
        java.security.Principal principal
    ) {
        return ResponseEntity.ok(quizService.getQuizQuestionsForLesson(courseId, lessonId, principal));
    }

    @org.springframework.web.bind.annotation.PostMapping("/{courseId}/lessons/{lessonId}/quiz/submit")
    public ResponseEntity<com.lms.Backend.course.dto.QuizSubmitResponse> submitLessonQuiz(
        @PathVariable Long courseId,
        @PathVariable Long lessonId,
        @org.springframework.web.bind.annotation.RequestBody com.lms.Backend.course.dto.QuizSubmitRequest request,
        java.security.Principal principal
    ) {
        return ResponseEntity.ok(quizService.submitQuizAnswer(courseId, lessonId, request, principal));
    }
}
