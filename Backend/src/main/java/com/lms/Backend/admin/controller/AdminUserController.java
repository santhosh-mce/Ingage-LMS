package com.lms.Backend.admin.controller;

import com.lms.Backend.admin.service.AdminActivityLogService;
import com.lms.Backend.admin.service.AdminUserService;
import com.lms.Backend.user.entity.User;
import com.lms.Backend.user.entity.UserRole;
import com.lms.Backend.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private final UserRepository userRepository;

    public AdminUserController(
        AdminUserService adminUserService,
        AdminActivityLogService logService,
        UserRepository userRepository
    ) {
        this.adminUserService = adminUserService;
        this.logService = logService;
        this.userRepository = userRepository;
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getUserStats() {
        return ResponseEntity.ok(adminUserService.getUserStats());
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getUsers(@RequestParam(value = "search", required = false) String search) {
        return ResponseEntity.ok(adminUserService.getAllUsers(search));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getUserDetails(@PathVariable UUID id) {
        return ResponseEntity.ok(adminUserService.getUserDetails(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateUser(
        @PathVariable UUID id,
        @RequestBody Map<String, Object> body,
        Principal principal,
        HttpServletRequest request
    ) {
        // Security check: If modifying own account, cannot demote self from ADMIN or deactivate self
        if (principal != null) {
            User target = userRepository.findById(id).orElse(null);
            if (target != null && target.getEmail().equalsIgnoreCase(principal.getName())) {
                if (body.containsKey("active") && !Boolean.parseBoolean(body.get("active").toString())) {
                    return ResponseEntity.badRequest().body(Map.of("error", "Security restriction: You cannot deactivate your own logged-in admin account."));
                }
                if (body.containsKey("role") && !"ADMIN".equalsIgnoreCase(body.get("role").toString())) {
                    return ResponseEntity.badRequest().body(Map.of("error", "Security restriction: You cannot remove admin role from your own logged-in account."));
                }
            }
        }

        Map<String, Object> res = adminUserService.updateUser(id, body);
        logService.log(null, principal != null ? principal.getName() : "admin", "UPDATE_USER", "User", id.toString(), "Updated profile details for " + id, request);
        return ResponseEntity.ok(res);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> updateUserStatus(
        @PathVariable UUID id,
        @RequestBody Map<String, Object> body,
        Principal principal,
        HttpServletRequest request
    ) {
        boolean active = Boolean.parseBoolean(body.get("active").toString());

        // Security check: Protect self-deactivation
        if (!active && principal != null) {
            User target = userRepository.findById(id).orElse(null);
            if (target != null && target.getEmail().equalsIgnoreCase(principal.getName())) {
                return ResponseEntity.badRequest().body(Map.of("error", "Security restriction: You cannot deactivate your own logged-in admin account."));
            }
        }

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

        // Security check: Prevent removing admin role from oneself
        if (principal != null && role != UserRole.ADMIN) {
            User target = userRepository.findById(id).orElse(null);
            if (target != null && target.getEmail().equalsIgnoreCase(principal.getName())) {
                return ResponseEntity.badRequest().body(Map.of("error", "Security restriction: You cannot remove admin privileges from your own logged-in account."));
            }
        }

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
        // Security check: Protect self-deletion
        if (principal != null) {
            User target = userRepository.findById(id).orElse(null);
            if (target != null && target.getEmail().equalsIgnoreCase(principal.getName())) {
                return ResponseEntity.badRequest().body(Map.of("error", "Security restriction: You cannot delete your own logged-in admin account."));
            }
        }

        Map<String, Object> res = adminUserService.deleteOrDeactivateUser(id);
        logService.log(null, principal != null ? principal.getName() : "admin", "DELETE_OR_DEACTIVATE_USER", "User", id.toString(), res.toString(), request);
        return ResponseEntity.ok(res);
    }
}
