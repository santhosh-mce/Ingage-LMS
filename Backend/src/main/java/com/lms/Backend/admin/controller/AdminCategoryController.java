package com.lms.Backend.admin.controller;

import com.lms.Backend.admin.service.AdminActivityLogService;
import com.lms.Backend.admin.service.AdminCategoryService;
import com.lms.Backend.course.entity.CourseCategory;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
public class AdminCategoryController {

    private final AdminCategoryService categoryService;
    private final AdminActivityLogService logService;

    public AdminCategoryController(
        AdminCategoryService categoryService,
        AdminActivityLogService logService
    ) {
        this.categoryService = categoryService;
        this.logService = logService;
    }

    /**
     * Public endpoint for active course categories (used in course filters, add-course dropdowns, etc.)
     */
    @GetMapping({"/categories", "/api/categories", "/categories/active", "/api/categories/active"})
    public ResponseEntity<List<Map<String, Object>>> getActiveCategories() {
        return ResponseEntity.ok(categoryService.getActiveCategories());
    }

    /**
     * Admin endpoints: Get all categories with course count, search and status filters
     */
    @GetMapping({"/admin/categories", "/api/admin/categories"})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Map<String, Object>>> getAllCategories(
        @RequestParam(value = "search", required = false) String search,
        @RequestParam(value = "status", defaultValue = "ALL") String status
    ) {
        return ResponseEntity.ok(categoryService.getAllCategories(search, status));
    }

    @GetMapping({"/admin/categories/{id}", "/api/admin/categories/{id}"})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getCategoryById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(categoryService.getCategoryById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping({"/admin/categories", "/api/admin/categories"})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createCategory(
        @RequestBody Map<String, Object> payload,
        Principal principal,
        HttpServletRequest request
    ) {
        try {
            CourseCategory created = categoryService.createCategory(payload);
            logService.log(
                null,
                principal != null ? principal.getName() : "admin",
                "CREATE_CATEGORY",
                "Category",
                created.getId().toString(),
                "Created category: " + created.getName(),
                request
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Failed to create category: " + e.getMessage()));
        }
    }

    @PutMapping({"/admin/categories/{id}", "/api/admin/categories/{id}"})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateCategory(
        @PathVariable Long id,
        @RequestBody Map<String, Object> payload,
        Principal principal,
        HttpServletRequest request
    ) {
        try {
            CourseCategory updated = categoryService.updateCategory(id, payload);
            logService.log(
                null,
                principal != null ? principal.getName() : "admin",
                "UPDATE_CATEGORY",
                "Category",
                id.toString(),
                "Updated category: " + updated.getName(),
                request
            );
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Failed to update category: " + e.getMessage()));
        }
    }

    @PatchMapping({"/admin/categories/{id}/status", "/api/admin/categories/{id}/status"})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> toggleCategoryStatus(
        @PathVariable Long id,
        @RequestBody Map<String, Object> payload,
        Principal principal,
        HttpServletRequest request
    ) {
        try {
            boolean active = true;
            if (payload.containsKey("active")) {
                active = Boolean.parseBoolean(String.valueOf(payload.get("active")));
            }
            CourseCategory updated = categoryService.toggleStatus(id, active);
            logService.log(
                null,
                principal != null ? principal.getName() : "admin",
                "TOGGLE_CATEGORY_STATUS",
                "Category",
                id.toString(),
                "Changed status of category " + updated.getName() + " to " + (active ? "Active" : "Inactive"),
                request
            );
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Failed to toggle category status: " + e.getMessage()));
        }
    }

    @DeleteMapping({"/admin/categories/{id}", "/api/admin/categories/{id}"})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteCategory(
        @PathVariable Long id,
        Principal principal,
        HttpServletRequest request
    ) {
        try {
            categoryService.deleteCategory(id);
            logService.log(
                null,
                principal != null ? principal.getName() : "admin",
                "DELETE_CATEGORY",
                "Category",
                id.toString(),
                "Deleted category ID: " + id,
                request
            );
            return ResponseEntity.ok(Map.of("message", "Category deleted successfully"));
        } catch (IllegalStateException e) {
            // Cannot delete category because courses are assigned
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Failed to delete category: " + e.getMessage()));
        }
    }
}
