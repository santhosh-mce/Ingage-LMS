package com.lms.Backend.admin.controller;

import com.lms.Backend.admin.service.AdminActivityLogService;
import com.lms.Backend.admin.service.AdminCourseService;
import com.lms.Backend.course.entity.Course;
import com.lms.Backend.course.entity.CourseCategory;
import com.lms.Backend.course.entity.CourseLesson;
import com.lms.Backend.course.entity.CourseSection;
import com.lms.Backend.course.repository.CourseCategoryRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCourseController {

    private final AdminCourseService courseService;
    private final CourseCategoryRepository categoryRepository;
    private final AdminActivityLogService logService;

    public AdminCourseController(
        AdminCourseService courseService,
        CourseCategoryRepository categoryRepository,
        AdminActivityLogService logService
    ) {
        this.courseService = courseService;
        this.categoryRepository = categoryRepository;
        this.logService = logService;
    }

    @GetMapping("/courses")
    public ResponseEntity<List<Map<String, Object>>> getCourses(
        @RequestParam(value = "search", required = false) String search,
        @RequestParam(value = "status", defaultValue = "ALL") String status
    ) {
        return ResponseEntity.ok(courseService.getAllCourses(search, status));
    }

    @GetMapping("/courses/{id}")
    public ResponseEntity<Map<String, Object>> getCourseById(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourseDetailsWithContent(id));
    }

    @PostMapping("/courses")
    public ResponseEntity<Course> createCourse(
        @RequestBody Map<String, Object> payload,
        Principal principal,
        HttpServletRequest request
    ) {
        Course created = courseService.createCourse(payload);
        logService.log(null, principal != null ? principal.getName() : "admin", "CREATE_COURSE", "Course", created.getId().toString(), "Created: " + created.getTitle(), request);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/courses/{id}")
    public ResponseEntity<Course> updateCourse(
        @PathVariable Long id,
        @RequestBody Map<String, Object> payload,
        Principal principal,
        HttpServletRequest request
    ) {
        Course updated = courseService.updateCourse(id, payload);
        logService.log(null, principal != null ? principal.getName() : "admin", "UPDATE_COURSE", "Course", id.toString(), "Updated: " + updated.getTitle(), request);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/courses/{id}/publish")
    public ResponseEntity<Map<String, Object>> publishCourse(
        @PathVariable Long id,
        Principal principal,
        HttpServletRequest request
    ) {
        Map<String, Object> res = courseService.publishCourse(id);
        logService.log(null, principal != null ? principal.getName() : "admin", "PUBLISH_COURSE", "Course", id.toString(), res.toString(), request);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/courses/{id}/unpublish")
    public ResponseEntity<Map<String, Object>> unpublishCourse(
        @PathVariable Long id,
        Principal principal,
        HttpServletRequest request
    ) {
        courseService.unpublishCourse(id);
        logService.log(null, principal != null ? principal.getName() : "admin", "UNPUBLISH_COURSE", "Course", id.toString(), "Unpublished", request);
        return ResponseEntity.ok(Map.of("success", true, "message", "Course unpublished"));
    }

    @PostMapping("/courses/{id}/archive")
    public ResponseEntity<Map<String, Object>> archiveCourse(
        @PathVariable Long id,
        Principal principal,
        HttpServletRequest request
    ) {
        Map<String, Object> res = courseService.archiveCourse(id);
        logService.log(null, principal != null ? principal.getName() : "admin", "ARCHIVE_COURSE", "Course", id.toString(), res.toString(), request);
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/courses/{id}")
    public ResponseEntity<Map<String, Object>> deleteCourse(
        @PathVariable Long id,
        Principal principal,
        HttpServletRequest request
    ) {
        Map<String, Object> res = courseService.deleteCourse(id);
        logService.log(null, principal != null ? principal.getName() : "admin", "DELETE_COURSE", "Course", id.toString(), res.toString(), request);
        return ResponseEntity.ok(res);
    }

    // Sections
    @PostMapping("/courses/{id}/sections")
    public ResponseEntity<CourseSection> addSection(
        @PathVariable Long id,
        @RequestBody Map<String, Object> payload
    ) {
        return ResponseEntity.ok(courseService.addSection(id, payload));
    }

    @PutMapping("/sections/{id}")
    public ResponseEntity<CourseSection> updateSection(
        @PathVariable Long id,
        @RequestBody Map<String, Object> payload
    ) {
        return ResponseEntity.ok(courseService.updateSection(id, payload));
    }

    @DeleteMapping("/sections/{id}")
    public ResponseEntity<Map<String, Object>> deleteSection(@PathVariable Long id) {
        courseService.deleteSection(id);
        return ResponseEntity.ok(Map.of("success", true));
    }

    // Lessons
    @PostMapping("/sections/{id}/lessons")
    public ResponseEntity<CourseLesson> addLesson(
        @PathVariable Long id,
        @RequestBody Map<String, Object> payload
    ) {
        return ResponseEntity.ok(courseService.addLesson(id, payload));
    }

    @PutMapping("/lessons/{id}")
    public ResponseEntity<CourseLesson> updateLesson(
        @PathVariable Long id,
        @RequestBody Map<String, Object> payload
    ) {
        return ResponseEntity.ok(courseService.updateLesson(id, payload));
    }

    @DeleteMapping("/lessons/{id}")
    public ResponseEntity<Map<String, Object>> deleteLesson(@PathVariable Long id) {
        courseService.deleteLesson(id);
        return ResponseEntity.ok(Map.of("success", true));
    }

    // Analytics & Students
    @GetMapping("/courses/{id}/analytics")
    public ResponseEntity<Map<String, Object>> getCourseAnalytics(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourseAnalytics(id));
    }

    @GetMapping("/courses/{id}/students")
    public ResponseEntity<List<Map<String, Object>>> getCourseStudents(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourseStudents(id));
    }

    // Categories
    @GetMapping("/categories")
    public ResponseEntity<List<CourseCategory>> getCategories() {
        return ResponseEntity.ok(categoryRepository.findAllByOrderByDisplayOrderAsc());
    }

    @PostMapping("/categories")
    public ResponseEntity<CourseCategory> createCategory(@RequestBody CourseCategory cat) {
        if (cat.getSlug() == null || cat.getSlug().isBlank()) {
            cat.setSlug(cat.getName().toLowerCase().trim().replaceAll("[^a-z0-9]+", "-"));
        }
        return ResponseEntity.ok(categoryRepository.save(cat));
    }
}
