package com.lms.Backend.admin.controller;

import com.lms.Backend.admin.service.AdminActivityLogService;
import com.lms.Backend.admin.service.AdminCareerService;
import com.lms.Backend.career.entity.Career;
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
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/careers")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCareerController {

    private final AdminCareerService careerService;
    private final AdminActivityLogService logService;

    public AdminCareerController(AdminCareerService careerService, AdminActivityLogService logService) {
        this.careerService = careerService;
        this.logService = logService;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getCareers() {
        return ResponseEntity.ok(careerService.getAllCareers());
    }

    @PostMapping
    public ResponseEntity<Career> createCareer(
        @RequestBody Map<String, Object> payload,
        Principal principal,
        HttpServletRequest request
    ) {
        Career created = careerService.createCareer(payload);
        logService.log(null, principal != null ? principal.getName() : "admin", "CREATE_CAREER", "Career", created.getId().toString(), "Created: " + created.getTitle(), request);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Career> updateCareer(
        @PathVariable Long id,
        @RequestBody Map<String, Object> payload,
        Principal principal,
        HttpServletRequest request
    ) {
        Career updated = careerService.updateCareer(id, payload);
        logService.log(null, principal != null ? principal.getName() : "admin", "UPDATE_CAREER", "Career", id.toString(), "Updated: " + updated.getTitle(), request);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{id}/publish")
    public ResponseEntity<Career> togglePublish(
        @PathVariable Long id,
        @RequestBody(required = false) Map<String, Object> payload,
        Principal principal,
        HttpServletRequest request
    ) {
        Boolean published = null;
        if (payload != null && payload.containsKey("published")) {
            published = Boolean.parseBoolean(String.valueOf(payload.get("published")));
        }
        Career updated = careerService.togglePublish(id, published);
        logService.log(null, principal != null ? principal.getName() : "admin",
            updated.isPublished() ? "PUBLISH_CAREER" : "UNPUBLISH_CAREER", "Career", id.toString(),
            "Set published=" + updated.isPublished() + " for " + updated.getTitle(), request);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Career> toggleStatus(
        @PathVariable Long id,
        @RequestBody(required = false) Map<String, Object> payload,
        Principal principal,
        HttpServletRequest request
    ) {
        Boolean active = null;
        if (payload != null && payload.containsKey("active")) {
            active = Boolean.parseBoolean(String.valueOf(payload.get("active")));
        }
        Career updated = careerService.toggleStatus(id, active);
        logService.log(null, principal != null ? principal.getName() : "admin",
            updated.isActive() ? "ACTIVATE_CAREER" : "DEACTIVATE_CAREER", "Career", id.toString(),
            "Set active=" + updated.isActive() + " for " + updated.getTitle(), request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteCareer(
        @PathVariable Long id,
        Principal principal,
        HttpServletRequest request
    ) {
        careerService.deleteCareer(id);
        logService.log(null, principal != null ? principal.getName() : "admin", "DELETE_CAREER", "Career", id.toString(), "Deleted", request);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @PostMapping("/{id}/assign-course/{courseId}")
    public ResponseEntity<Map<String, Object>> assignCourse(
        @PathVariable Long id,
        @PathVariable Long courseId,
        Principal principal,
        HttpServletRequest request
    ) {
        careerService.assignCourseToCareer(id, courseId);
        logService.log(null, principal != null ? principal.getName() : "admin", "ASSIGN_COURSE_CAREER", "Career", id.toString(), "Assigned course " + courseId, request);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @DeleteMapping("/{id}/remove-course/{courseId}")
    public ResponseEntity<Map<String, Object>> removeCourse(
        @PathVariable Long id,
        @PathVariable Long courseId,
        Principal principal,
        HttpServletRequest request
    ) {
        careerService.removeCourseFromCareer(id, courseId);
        logService.log(null, principal != null ? principal.getName() : "admin", "REMOVE_COURSE_CAREER", "Career", id.toString(), "Removed course " + courseId, request);
        return ResponseEntity.ok(Map.of("success", true));
    }
}
