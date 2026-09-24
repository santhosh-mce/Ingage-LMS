package com.lms.Backend.credential.controller;

import com.lms.Backend.credential.dto.CredentialCourseDto;
import com.lms.Backend.credential.service.CredentialCourseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping
@PreAuthorize("hasRole('ADMIN')")
public class AdminCredentialCourseController {

    private final CredentialCourseService courseService;

    public AdminCredentialCourseController(CredentialCourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping({"/admin/credential-courses", "/api/admin/credential-courses"})
    public ResponseEntity<List<CredentialCourseDto>> getAllAdminCourses() {
        return ResponseEntity.ok(courseService.getAllAdminCourses());
    }

    @PostMapping({"/admin/credential-courses", "/api/admin/credential-courses"})
    public ResponseEntity<CredentialCourseDto> createCourse(@RequestBody CredentialCourseDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(courseService.createAdminCourse(dto));
    }

    @PutMapping({"/admin/credential-courses/{id}", "/api/admin/credential-courses/{id}"})
    public ResponseEntity<CredentialCourseDto> updateCourse(
        @PathVariable Long id,
        @RequestBody CredentialCourseDto dto
    ) {
        return ResponseEntity.ok(courseService.updateAdminCourse(id, dto));
    }

    @DeleteMapping({"/admin/credential-courses/{id}", "/api/admin/credential-courses/{id}"})
    public ResponseEntity<Map<String, String>> deleteCourse(@PathVariable Long id) {
        courseService.deleteAdminCourse(id);
        return ResponseEntity.ok(Map.of("message", "Google credential course deleted successfully", "id", id.toString()));
    }

    @PatchMapping({"/admin/credential-courses/{id}/publish", "/api/admin/credential-courses/{id}/publish"})
    public ResponseEntity<CredentialCourseDto> togglePublish(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.togglePublish(id));
    }
}
