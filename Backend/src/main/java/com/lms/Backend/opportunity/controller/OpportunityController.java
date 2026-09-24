package com.lms.Backend.opportunity.controller;

import com.lms.Backend.opportunity.dto.OpportunityApplicationDto;
import com.lms.Backend.opportunity.dto.OpportunityDto;
import com.lms.Backend.opportunity.dto.OpportunityStatsDto;
import com.lms.Backend.opportunity.service.OpportunityService;
import com.lms.Backend.user.entity.User;
import com.lms.Backend.user.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/opportunities")
public class OpportunityController {

    private final OpportunityService opportunityService;
    private final UserRepository userRepository;

    public OpportunityController(OpportunityService opportunityService, UserRepository userRepository) {
        this.opportunityService = opportunityService;
        this.userRepository = userRepository;
    }

    private User resolveUser(Principal principal) {
        if (principal == null || principal.getName() == null) {
            return null;
        }
        return userRepository.findByEmailIgnoreCase(principal.getName()).orElse(null);
    }

    @GetMapping
    public ResponseEntity<List<OpportunityDto>> list(
        @RequestParam(required = false) String type,
        @RequestParam(required = false) String workMode,
        @RequestParam(required = false) String search,
        @RequestParam(required = false) String query
    ) {
        String searchQuery = search != null ? search : query;
        return ResponseEntity.ok(opportunityService.getPublicOpportunities(type, workMode, searchQuery));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OpportunityDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(opportunityService.getOpportunityById(id));
    }

    @GetMapping("/stats")
    public ResponseEntity<OpportunityStatsDto> getStats(Principal principal) {
        User user = resolveUser(principal);
        return ResponseEntity.ok(opportunityService.getStats(user));
    }

    @GetMapping("/my-applications")
    public ResponseEntity<List<OpportunityApplicationDto>> getMyApplications(Principal principal) {
        User user = resolveUser(principal);
        if (user == null) {
            return ResponseEntity.ok(List.of());
        }
        return ResponseEntity.ok(opportunityService.getUserApplications(user));
    }

    @GetMapping("/saved")
    public ResponseEntity<List<String>> getSaved(Principal principal) {
        User user = resolveUser(principal);
        if (user == null) {
            return ResponseEntity.ok(List.of());
        }
        return ResponseEntity.ok(opportunityService.getUserSavedIds(user));
    }

    @PostMapping("/{id}/apply")
    public ResponseEntity<OpportunityApplicationDto> apply(
        @PathVariable Long id,
        @RequestBody(required = false) Map<String, String> body,
        Principal principal
    ) {
        User user = resolveUser(principal);
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        String notes = body != null ? body.get("notes") : "";
        OpportunityApplicationDto result = opportunityService.apply(user, id, notes);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{id}/save")
    public ResponseEntity<Map<String, Object>> toggleSave(@PathVariable Long id, Principal principal) {
        User user = resolveUser(principal);
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        boolean saved = opportunityService.toggleSave(user, id);
        return ResponseEntity.ok(Map.of("saved", saved, "opportunityId", id));
    }

    @DeleteMapping("/{id}/save")
    public ResponseEntity<Map<String, Object>> unsave(@PathVariable Long id, Principal principal) {
        User user = resolveUser(principal);
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        opportunityService.removeSaved(user, id);
        return ResponseEntity.ok(Map.of("saved", false, "opportunityId", id));
    }
}