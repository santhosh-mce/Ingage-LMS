package com.lms.Backend.admin.service;

import com.lms.Backend.admin.entity.AdminActivityLog;
import com.lms.Backend.admin.repository.AdminActivityLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class AdminActivityLogService {

    private final AdminActivityLogRepository activityLogRepository;

    public AdminActivityLogService(AdminActivityLogRepository activityLogRepository) {
        this.activityLogRepository = activityLogRepository;
    }

    @Transactional
    public void log(UUID adminId, String adminEmail, String action, String entityType, String entityId, String details, HttpServletRequest request) {
        String ipAddress = "127.0.0.1";
        if (request != null) {
            String forwarded = request.getHeader("X-Forwarded-For");
            ipAddress = (forwarded != null && !forwarded.isBlank()) ? forwarded.split(",")[0].trim() : request.getRemoteAddr();
        }
        AdminActivityLog log = new AdminActivityLog(adminId, adminEmail, action, entityType, entityId, details, ipAddress);
        activityLogRepository.save(log);
    }

    public List<AdminActivityLog> getRecentLogs() {
        return activityLogRepository.findTop100ByOrderByCreatedAtDesc();
    }
}
