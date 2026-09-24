package com.lms.Backend.project.controller;

import com.lms.Backend.project.dto.ProjectDto;
import com.lms.Backend.project.service.ProjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public ResponseEntity<List<ProjectDto>> listProjects(@RequestParam(required = false) String industry) {
        return ResponseEntity.ok(projectService.getActiveProjects(industry));
    }

    @GetMapping("/{slug}")
    public ResponseEntity<ProjectDto> getProjectBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(projectService.getProjectBySlug(slug));
    }
}