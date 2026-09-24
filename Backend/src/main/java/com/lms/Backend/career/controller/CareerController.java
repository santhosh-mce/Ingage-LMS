package com.lms.Backend.career.controller;

import com.lms.Backend.career.dto.JobRoleResponse;
import com.lms.Backend.career.service.JobRoleService;
import com.lms.Backend.common.exception.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/career")
public class CareerController {

    private final JobRoleService jobRoleService;

    public CareerController(JobRoleService jobRoleService) {
        this.jobRoleService = jobRoleService;
    }

    @GetMapping("/job-roles")
    public ResponseEntity<List<JobRoleResponse>> getJobRoles() {
        return ResponseEntity.ok(jobRoleService.getActiveJobRoles());
    }

    @GetMapping("/job-roles/{slug}")
    public ResponseEntity<JobRoleResponse> getJobRoleBySlug(@PathVariable String slug) {
        JobRoleResponse role = jobRoleService.getJobRoleBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Job role not found with slug: " + slug));
        return ResponseEntity.ok(role);
    }
}