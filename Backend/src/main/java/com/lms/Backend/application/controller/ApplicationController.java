package com.lms.Backend.application.controller;

import com.lms.Backend.opportunity.dto.OpportunityApplicationDto;
import com.lms.Backend.opportunity.service.OpportunityService;
import com.lms.Backend.user.entity.User;
import com.lms.Backend.user.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/applications")
public class ApplicationController {

    private final OpportunityService opportunityService;
    private final UserRepository userRepository;

    public ApplicationController(OpportunityService opportunityService, UserRepository userRepository) {
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
    public ResponseEntity<List<OpportunityApplicationDto>> getApplications(Principal principal) {
        User user = resolveUser(principal);
        if (user == null) {
            return ResponseEntity.ok(List.of());
        }
        return ResponseEntity.ok(opportunityService.getUserApplications(user));
    }

    @PostMapping
    public ResponseEntity<OpportunityApplicationDto> createApplication(
        @RequestBody Map<String, Object> body,
        Principal principal
    ) {
        User user = resolveUser(principal);
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        Object oppIdObj = body.get("opportunityId");
        if (oppIdObj == null) {
            return ResponseEntity.badRequest().build();
        }
        Long oppId;
        try {
            oppId = Long.parseLong(String.valueOf(oppIdObj));
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().build();
        }
        String notes = body.containsKey("notes") ? String.valueOf(body.get("notes")) : "";
        return ResponseEntity.ok(opportunityService.apply(user, oppId, notes));
    }
}