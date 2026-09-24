package com.lms.Backend.admin.controller;

import com.lms.Backend.admin.service.AdminActivityLogService;
import com.lms.Backend.admin.service.AdminSettingsService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/admin/settings")
@PreAuthorize("hasRole('ADMIN')")
public class AdminSettingsController {

    private final AdminSettingsService settingsService;
    private final AdminActivityLogService logService;

    public AdminSettingsController(
        AdminSettingsService settingsService,
        AdminActivityLogService logService
    ) {
        this.settingsService = settingsService;
        this.logService = logService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getSettings() {
        return ResponseEntity.ok(settingsService.getAllSettings());
    }

    @PutMapping
    public ResponseEntity<Map<String, Object>> updateSettings(
        @RequestBody Map<String, Object> payload,
        Principal principal,
        HttpServletRequest request
    ) {
        String adminEmail = principal != null ? principal.getName() : "admin";
        Map<String, Object> updated = settingsService.updateSettings(payload, adminEmail);
        logService.log(null, adminEmail, "UPDATE_SETTINGS", "Settings", "all", "Updated LMS platform settings", request);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/general")
    public ResponseEntity<Map<String, Object>> updateGeneralSettings(
        @RequestBody Map<String, Object> payload,
        Principal principal,
        HttpServletRequest request
    ) {
        String adminEmail = principal != null ? principal.getName() : "admin";
        Map<String, Object> updated = settingsService.updateSettings(payload, adminEmail);
        logService.log(null, adminEmail, "UPDATE_GENERAL_SETTINGS", "Settings", "general", "Updated general settings", request);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/security/password")
    public ResponseEntity<?> updatePassword(
        @RequestBody Map<String, String> body,
        Principal principal,
        HttpServletRequest request
    ) {
        if (principal == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Unauthorized session"));
        }

        String currentPassword = body.get("currentPassword");
        String newPassword = body.get("newPassword");
        String confirmPassword = body.get("confirmPassword");

        try {
            Map<String, Object> result = settingsService.updatePassword(
                principal.getName(),
                currentPassword,
                newPassword,
                confirmPassword
            );
            logService.log(null, principal.getName(), "CHANGE_PASSWORD", "Security", "admin", "Admin changed security password", request);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/email/test")
    public ResponseEntity<?> sendTestEmail(
        @RequestBody Map<String, String> body,
        Principal principal,
        HttpServletRequest request
    ) {
        String recipient = body.get("recipientEmail");
        if (recipient == null || recipient.isBlank()) {
            recipient = body.get("email");
        }

        String adminEmail = principal != null ? principal.getName() : "admin";

        try {
            Map<String, Object> result = settingsService.sendTestEmail(recipient, adminEmail);
            logService.log(null, adminEmail, "TEST_EMAIL", "Email", recipient, "Sent test email to " + recipient, request);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/system")
    public ResponseEntity<Map<String, Object>> getSystemTelemetry() {
        return ResponseEntity.ok(settingsService.getSystemInfo());
    }
}
