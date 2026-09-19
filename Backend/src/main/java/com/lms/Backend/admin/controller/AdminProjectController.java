package com.lms.Backend.admin.controller;

import com.lms.Backend.admin.service.AdminActivityLogService;
import com.lms.Backend.common.exception.ResourceNotFoundException;
import com.lms.Backend.project.entity.Project;
import com.lms.Backend.project.repository.ProjectRepository;
import com.lms.Backend.project.service.ProjectService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/projects")
@PreAuthorize("hasRole('ADMIN')")
@org.springframework.transaction.annotation.Transactional
public class AdminProjectController {

    private final ProjectRepository projectRepository;
    private final ProjectService projectService;
    private final AdminActivityLogService logService;

    public AdminProjectController(ProjectRepository projectRepository, ProjectService projectService, AdminActivityLogService logService) {
        this.projectRepository = projectRepository;
        this.projectService = projectService;
        this.logService = logService;
    }

    @GetMapping
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public ResponseEntity<List<com.lms.Backend.project.dto.ProjectDto>> getAllProjects() {
        return ResponseEntity.ok(
            projectRepository.findAllByOrderByDisplayOrderAscTitleAsc()
                .stream()
                .map(projectService::toDto)
                .toList()
        );
    }

    @PostMapping
    public ResponseEntity<com.lms.Backend.project.dto.ProjectDto> createProject(
        @RequestBody Map<String, Object> payload,
        Principal principal,
        HttpServletRequest request
    ) {
        Project project = new Project();
        applyPayload(project, payload);
        Project saved = projectRepository.save(project);

        logService.log(null, principal != null ? principal.getName() : "admin",
            "CREATE_PROJECT", "Project", saved.getId().toString(), "Created project: " + saved.getTitle(), request);

        return ResponseEntity.ok(projectService.toDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Project> updateProject(
        @PathVariable Long id,
        @RequestBody Map<String, Object> payload,
        Principal principal,
        HttpServletRequest request
    ) {
        Project project = projectRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));

        applyPayload(project, payload);
        Project updated = projectRepository.save(project);

        logService.log(null, principal != null ? principal.getName() : "admin",
            "UPDATE_PROJECT", "Project", id.toString(), "Updated project: " + updated.getTitle(), request);

        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<com.lms.Backend.project.dto.ProjectDto> toggleStatus(
        @PathVariable Long id,
        @RequestBody(required = false) Map<String, Object> payload,
        Principal principal,
        HttpServletRequest request
    ) {
        Project project = projectRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));

        boolean active = (payload != null && payload.containsKey("active"))
            ? Boolean.parseBoolean(String.valueOf(payload.get("active")))
            : !project.isActive();
        project.setActive(active);
        Project updated = projectRepository.save(project);

        logService.log(null, principal != null ? principal.getName() : "admin",
            active ? "ACTIVATE_PROJECT" : "DEACTIVATE_PROJECT", "Project", id.toString(),
            "Set active=" + active + " for " + updated.getTitle(), request);

        return ResponseEntity.ok(projectService.toDto(updated));
    }

    @PutMapping("/{id}/publish")
    public ResponseEntity<com.lms.Backend.project.dto.ProjectDto> togglePublish(
        @PathVariable Long id,
        @RequestBody(required = false) Map<String, Object> payload,
        Principal principal,
        HttpServletRequest request
    ) {
        Project project = projectRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));

        boolean published = (payload != null && payload.containsKey("published"))
            ? Boolean.parseBoolean(String.valueOf(payload.get("published")))
            : !project.isPublished();
        project.setPublished(published);
        Project updated = projectRepository.save(project);

        logService.log(null, principal != null ? principal.getName() : "admin",
            published ? "PUBLISH_PROJECT" : "UNPUBLISH_PROJECT", "Project", id.toString(),
            "Set published=" + published + " for " + updated.getTitle(), request);

        return ResponseEntity.ok(projectService.toDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteProject(
        @PathVariable Long id,
        Principal principal,
        HttpServletRequest request
    ) {
        Project project = projectRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));

        String title = project.getTitle();
        projectRepository.delete(project);

        logService.log(null, principal != null ? principal.getName() : "admin",
            "DELETE_PROJECT", "Project", id.toString(), "Deleted project: " + title, request);

        return ResponseEntity.ok(Map.of("success", true, "message", "Project deleted successfully"));
    }

    private void applyPayload(Project project, Map<String, Object> payload) {
        if (payload.containsKey("title")) project.setTitle((String) payload.get("title"));
        if (payload.containsKey("slug")) project.setSlug((String) payload.get("slug"));
        if (payload.containsKey("category")) {
            project.setCategory((String) payload.get("category"));
            project.setIndustry((String) payload.get("category"));
        }
        if (payload.containsKey("industry")) project.setIndustry((String) payload.get("industry"));
        if (payload.containsKey("description")) project.setDescription((String) payload.get("description"));
        if (payload.containsKey("difficulty")) project.setDifficulty((String) payload.get("difficulty"));
        if (payload.containsKey("duration")) project.setDuration((String) payload.get("duration"));
        if (payload.containsKey("imageUrl")) project.setImageUrl((String) payload.get("imageUrl"));
        if (payload.containsKey("prerequisites")) project.setPrerequisites((String) payload.get("prerequisites"));
        if (payload.containsKey("active")) project.setActive(Boolean.parseBoolean(String.valueOf(payload.get("active"))));
        if (payload.containsKey("published")) project.setPublished(Boolean.parseBoolean(String.valueOf(payload.get("published"))));
        if (payload.containsKey("displayOrder")) project.setDisplayOrder(Integer.parseInt(String.valueOf(payload.get("displayOrder"))));
        if (payload.containsKey("skillsCount")) project.setSkillsCount(Integer.parseInt(String.valueOf(payload.get("skillsCount"))));
        if (payload.containsKey("learnersCount")) project.setLearnersCount(Integer.parseInt(String.valueOf(payload.get("learnersCount"))));

        if (payload.get("techStack") instanceof List) {
            project.setTechStack((List<String>) payload.get("techStack"));
        }
        if (payload.get("skillsLearned") instanceof List) {
            project.setSkillsLearned((List<String>) payload.get("skillsLearned"));
        }
        if (payload.get("whatYouWillBuild") instanceof List) {
            project.setWhatYouWillBuild((List<String>) payload.get("whatYouWillBuild"));
        }
        if (payload.get("learningOutcomes") instanceof List) {
            project.setLearningOutcomes((List<String>) payload.get("learningOutcomes"));
        }
    }
}
