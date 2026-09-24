package com.lms.Backend.admin.controller;

import com.lms.Backend.admin.service.AdminDashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminDashboardController {

    private final AdminDashboardService dashboardService;

    public AdminDashboardController(AdminDashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardOverview() {
        return ResponseEntity.ok(dashboardService.getOverviewMetrics());
    }

    @GetMapping("/analytics")
    public ResponseEntity<Map<String, Object>> getDashboardAnalytics() {
        return ResponseEntity.ok(dashboardService.getAnalytics());
    }

    @GetMapping("/latest-courses")
    public ResponseEntity<List<Map<String, Object>>> getLatestCourses() {
        return ResponseEntity.ok(dashboardService.getLatestCourses());
    }

    @GetMapping("/recent-activity")
    public ResponseEntity<List<Map<String, Object>>> getRecentActivity() {
        return ResponseEntity.ok(dashboardService.getRecentActivity());
    }
}
