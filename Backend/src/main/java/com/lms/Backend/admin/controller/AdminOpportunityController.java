package com.lms.Backend.admin.controller;

import com.lms.Backend.admin.service.AdminActivityLogService;
import com.lms.Backend.opportunity.dto.OpportunityDto;
import com.lms.Backend.opportunity.service.OpportunityService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/opportunities")
@PreAuthorize("hasRole('ADMIN')")
public class AdminOpportunityController {

    private final OpportunityService opportunityService;
    private final AdminActivityLogService logService;

    public AdminOpportunityController(OpportunityService opportunityService, AdminActivityLogService logService) {
        this.opportunityService = opportunityService;
        this.logService = logService;
    }

    @GetMapping
    public ResponseEntity<List<OpportunityDto>> getAll() {
        return ResponseEntity.ok(opportunityService.getAllAdminOpportunities());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OpportunityDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(opportunityService.getOpportunityById(id));
    }

    @PostMapping
    public ResponseEntity<OpportunityDto> create(
        @RequestBody Map<String, Object> payload,
        Principal principal,
        HttpServletRequest request
    ) {
        OpportunityDto created = opportunityService.createOpportunity(payload);
        logService.log(null, principal != null ? principal.getName() : "admin",
            "CREATE_OPPORTUNITY", "Opportunity", created.getId(),
            "Created opportunity: " + created.getTitle() + " at " + created.getCompany(), request);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OpportunityDto> update(
        @PathVariable Long id,
        @RequestBody Map<String, Object> payload,
        Principal principal,
        HttpServletRequest request
    ) {
        OpportunityDto updated = opportunityService.updateOpportunity(id, payload);
        logService.log(null, principal != null ? principal.getName() : "admin",
            "UPDATE_OPPORTUNITY", "Opportunity", id.toString(),
            "Updated opportunity: " + updated.getTitle(), request);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{id}/publish")
    public ResponseEntity<OpportunityDto> togglePublish(
        @PathVariable Long id,
        @RequestBody(required = false) Map<String, Object> payload,
        Principal principal,
        HttpServletRequest request
    ) {
        Boolean published = (payload != null && payload.containsKey("published"))
            ? Boolean.parseBoolean(String.valueOf(payload.get("published")))
            : null;
        OpportunityDto updated = opportunityService.togglePublish(id, published);
        logService.log(null, principal != null ? principal.getName() : "admin",
            updated.isPublished() ? "PUBLISH_OPPORTUNITY" : "UNPUBLISH_OPPORTUNITY", "Opportunity", id.toString(),
            "Set published=" + updated.isPublished() + " for " + updated.getTitle(), request);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<OpportunityDto> toggleStatus(
        @PathVariable Long id,
        @RequestBody(required = false) Map<String, Object> payload,
        Principal principal,
        HttpServletRequest request
    ) {
        Boolean active = (payload != null && payload.containsKey("active"))
            ? Boolean.parseBoolean(String.valueOf(payload.get("active")))
            : null;
        OpportunityDto updated = opportunityService.toggleStatus(id, active);
        logService.log(null, principal != null ? principal.getName() : "admin",
            updated.isActive() ? "ACTIVATE_OPPORTUNITY" : "DEACTIVATE_OPPORTUNITY", "Opportunity", id.toString(),
            "Set active=" + updated.isActive() + " for " + updated.getTitle(), request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(
        @PathVariable Long id,
        Principal principal,
        HttpServletRequest request
    ) {
        opportunityService.deleteOpportunity(id);
        logService.log(null, principal != null ? principal.getName() : "admin",
            "DELETE_OPPORTUNITY", "Opportunity", id.toString(),
            "Deleted opportunity with ID: " + id, request);
        return ResponseEntity.ok(Map.of("success", true, "message", "Opportunity deleted successfully"));
    }
}
