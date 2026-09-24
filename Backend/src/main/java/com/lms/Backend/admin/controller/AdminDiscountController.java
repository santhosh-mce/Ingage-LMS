package com.lms.Backend.admin.controller;

import com.lms.Backend.admin.service.AdminActivityLogService;
import com.lms.Backend.admin.service.AdminDiscountService;
import com.lms.Backend.discount.entity.Discount;
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
@RequestMapping("/admin/discounts")
@PreAuthorize("hasRole('ADMIN')")
public class AdminDiscountController {

    private final AdminDiscountService discountService;
    private final AdminActivityLogService logService;

    public AdminDiscountController(AdminDiscountService discountService, AdminActivityLogService logService) {
        this.discountService = discountService;
        this.logService = logService;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getDiscounts() {
        return ResponseEntity.ok(discountService.getAllDiscounts());
    }

    @PostMapping
    public ResponseEntity<Discount> createDiscount(
        @RequestBody Map<String, Object> payload,
        Principal principal,
        HttpServletRequest request
    ) {
        Discount created = discountService.createDiscount(payload);
        logService.log(null, principal != null ? principal.getName() : "admin", "CREATE_DISCOUNT", "Discount", created.getId().toString(), "Created: " + created.getCouponCode(), request);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Discount> updateDiscount(
        @PathVariable Long id,
        @RequestBody Map<String, Object> payload,
        Principal principal,
        HttpServletRequest request
    ) {
        Discount updated = discountService.updateDiscount(id, payload);
        logService.log(null, principal != null ? principal.getName() : "admin", "UPDATE_DISCOUNT", "Discount", id.toString(), "Updated: " + updated.getCouponCode(), request);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> toggleStatus(
        @PathVariable Long id,
        @RequestBody Map<String, Object> body,
        Principal principal,
        HttpServletRequest request
    ) {
        boolean active = Boolean.parseBoolean(body.get("active").toString());
        discountService.toggleDiscountStatus(id, active);
        logService.log(null, principal != null ? principal.getName() : "admin", "TOGGLE_DISCOUNT", "Discount", id.toString(), "Set active=" + active, request);
        return ResponseEntity.ok(Map.of("success", true, "active", active));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteDiscount(
        @PathVariable Long id,
        Principal principal,
        HttpServletRequest request
    ) {
        discountService.deleteDiscount(id);
        logService.log(null, principal != null ? principal.getName() : "admin", "DELETE_DISCOUNT", "Discount", id.toString(), "Deleted", request);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @GetMapping("/analytics")
    public ResponseEntity<Map<String, Object>> getAnalytics() {
        return ResponseEntity.ok(discountService.getDiscountAnalytics());
    }
}
