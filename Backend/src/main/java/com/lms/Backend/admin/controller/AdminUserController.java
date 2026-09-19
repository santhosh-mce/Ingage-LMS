package com.lms.Backend.admin.controller;

import com.lms.Backend.admin.service.AdminActivityLogService;
import com.lms.Backend.admin.service.AdminUserService;
import com.lms.Backend.user.entity.UserRole;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final AdminUserService adminUserService;
    private final AdminActivityLogService logService;

    public AdminUserController(AdminUserService adminUserService, AdminActivityLogService logService) {
        this.adminUserService = adminUserService;
        this.logService = logService;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getUsers(@RequestParam(value = "search", required = false) String search) {
        return ResponseEntity.ok(adminUserService.getAllUsers(search));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getUserDetails(@PathVariable UUID id) {
        return ResponseEntity.ok(adminUserService.getUserDetails(id));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> updateUserStatus(
        @PathVariable UUID id,
        @RequestBody Map<String, Object> body,
        Principal principal,
        HttpServletRequest request
    ) {
        boolean active = Boolean.parseBoolean(body.get("active").toString());
        adminUserService.toggleUserStatus(id, active);
        logService.log(null, principal != null ? principal.getName() : "admin", active ? "ACTIVATE_USER" : "DEACTIVATE_USER", "User", id.toString(), "Updated active=" + active, request);
        return ResponseEntity.ok(Map.of("success", true, "active", active));
    }

    @PutMapping("/{id}/role")
    public ResponseEntity<Map<String, Object>> updateUserRole(
        @PathVariable UUID id,
        @RequestBody Map<String, Object> body,
        Principal principal,
        HttpServletRequest request
    ) {
        String roleStr = (String) body.get("role");
        UserRole role = UserRole.valueOf(roleStr.toUpperCase());
        adminUserService.updateUserRole(id, role);
        logService.log(null, principal != null ? principal.getName() : "admin", "UPDATE_USER_ROLE", "User", id.toString(), "Updated role to " + role, request);
        return ResponseEntity.ok(Map.of("success", true, "role", role.name()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteUser(
        @PathVariable UUID id,
        Principal principal,
        HttpServletRequest request
    ) {
        Map<String, Object> res = adminUserService.deleteOrDeactivateUser(id);
        logService.log(null, principal != null ? principal.getName() : "admin", "DELETE_OR_DEACTIVATE_USER", "User", id.toString(), res.toString(), request);
        return ResponseEntity.ok(res);
    }
}
