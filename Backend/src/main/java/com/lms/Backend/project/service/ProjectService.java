package com.lms.Backend.project.service;

import com.lms.Backend.common.exception.ResourceNotFoundException;
import com.lms.Backend.project.dto.ProjectDto;
import com.lms.Backend.project.entity.Project;
import com.lms.Backend.project.repository.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Transactional(readOnly = true)
    public List<ProjectDto> getActiveProjects(String industry) {
        List<Project> list;
        if (industry != null && !industry.trim().isEmpty() && !industry.equalsIgnoreCase("All")) {
            list = projectRepository.findByIndustryIgnoreCaseAndActiveTrueAndPublishedTrue(industry.trim());
        } else {
            list = projectRepository.findByActiveTrueAndPublishedTrueOrderByDisplayOrderAscTitleAsc();
        }
        return list.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProjectDto getProjectBySlug(String slug) {
        Project project = projectRepository.findBySlug(slug)
            .filter(p -> p.isActive() && p.isPublished())
            .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + slug));
        return toDto(project);
    }

    @Transactional(readOnly = true)
    public List<ProjectDto> getAllProjectsAdmin() {
        return projectRepository.findAllByOrderByDisplayOrderAscTitleAsc()
            .stream()
            .map(this::toDto)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProjectDto getProjectByIdAdmin(Long id) {
        Project project = projectRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));
        return toDto(project);
    }

    public ProjectDto toDto(Project p) {
        ProjectDto dto = new ProjectDto();
        dto.setId(p.getId());
        dto.setTitle(p.getTitle());
        dto.setSlug(p.getSlug());
        dto.setIndustry(p.getIndustry());
        dto.setCategory(p.getCategory());
        dto.setDescription(p.getDescription());
        dto.setDifficulty(p.getDifficulty());
        dto.setDuration(p.getDuration());
        dto.setSkillsCount(p.getSkillsCount());
        dto.setLearnersCount(p.getLearnersCount());
        dto.setImageUrl(p.getImageUrl());
        dto.setPrerequisites(p.getPrerequisites());
        dto.setTechStack(p.getTechStack() != null ? new ArrayList<>(p.getTechStack()) : new ArrayList<>());
        dto.setWhatYouWillBuild(p.getWhatYouWillBuild() != null ? new ArrayList<>(p.getWhatYouWillBuild()) : new ArrayList<>());
        dto.setLearningOutcomes(p.getLearningOutcomes() != null ? new ArrayList<>(p.getLearningOutcomes()) : new ArrayList<>());
        dto.setSkillsLearned(p.getSkillsLearned() != null ? new ArrayList<>(p.getSkillsLearned()) : new ArrayList<>());
        dto.setActive(p.isActive());
        dto.setPublished(p.isPublished());
        dto.setDisplayOrder(p.getDisplayOrder());
        dto.setCreatedAt(p.getCreatedAt());
        dto.setUpdatedAt(p.getUpdatedAt());
        return dto;
    }
}
