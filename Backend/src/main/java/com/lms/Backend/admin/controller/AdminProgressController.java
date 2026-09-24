package com.lms.Backend.admin.controller;

import com.lms.Backend.admin.service.AdminProgressService;
import com.lms.Backend.user.entity.User;
import com.lms.Backend.user.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping
public class AdminProgressController {

    private final AdminProgressService progressService;
    private final UserRepository userRepository;

    public AdminProgressController(AdminProgressService progressService, UserRepository userRepository) {
        this.progressService = progressService;
        this.userRepository = userRepository;
    }

    @GetMapping("/admin/progress")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Map<String, Object>>> getAdminProgress(
        @RequestParam(value = "search", required = false) String search,
        @RequestParam(value = "courseId", required = false) Long courseId,
        @RequestParam(value = "status", defaultValue = "ALL") String status
    ) {
        return ResponseEntity.ok(progressService.getProgressList(search, courseId, status));
    }

    @PostMapping("/progress/complete-lesson")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> completeLesson(
        @RequestBody Map<String, Object> payload,
        Principal principal
    ) {
        User user = userRepository.findByEmailIgnoreCase(principal.getName())
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Long lessonId = Long.parseLong(payload.get("lessonId").toString());
        Map<String, Object> res = progressService.markLessonComplete(user.getId(), lessonId, payload);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/progress/save-progress")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> saveProgress(
        @RequestBody Map<String, Object> payload,
        Principal principal
    ) {
        User user = userRepository.findByEmailIgnoreCase(principal.getName())
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Long lessonId = Long.parseLong(payload.get("lessonId").toString());
        Map<String, Object> res = progressService.saveProgress(user.getId(), lessonId, payload);
        return ResponseEntity.ok(res);
    }
}
