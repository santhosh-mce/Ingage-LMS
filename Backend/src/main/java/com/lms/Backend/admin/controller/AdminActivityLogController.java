package com.lms.Backend.admin.controller;

import com.lms.Backend.admin.entity.AdminActivityLog;
import com.lms.Backend.admin.service.AdminActivityLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/activity")
@PreAuthorize("hasRole('ADMIN')")
public class AdminActivityLogController {

    private final AdminActivityLogService logService;

    public AdminActivityLogController(AdminActivityLogService logService) {
        this.logService = logService;
    }

    @GetMapping
    public ResponseEntity<List<AdminActivityLog>> getActivityLogs() {
        return ResponseEntity.ok(logService.getRecentLogs());
    }
}
