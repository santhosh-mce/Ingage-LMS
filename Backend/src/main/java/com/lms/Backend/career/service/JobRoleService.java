package com.lms.Backend.career.service;

import com.lms.Backend.career.dto.JobRoleResponse;
import com.lms.Backend.career.entity.JobRole;
import com.lms.Backend.career.repository.JobRoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class JobRoleService {

    private final JobRoleRepository jobRoleRepository;

    public JobRoleService(JobRoleRepository jobRoleRepository) {
        this.jobRoleRepository = jobRoleRepository;
    }

    public List<JobRoleResponse> getActiveJobRoles() {
        return jobRoleRepository.findByActiveTrueOrderByIdAsc().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public Optional<JobRoleResponse> getJobRoleBySlug(String slug) {
        return jobRoleRepository.findBySlugAndActiveTrue(slug)
                .map(this::mapToResponse);
    }

    private JobRoleResponse mapToResponse(JobRole role) {
        return new JobRoleResponse(
                role.getId(),
                role.getTitle(),
                role.getSlug(),
                role.getDescription(),
                role.getImageUrl(),
                role.getIconName(),
                role.getDifficultyLevel(),
                role.getDurationMonths(),
                role.getMinimumSalary(),
                role.getMaximumSalary(),
                role.getJobOpenings(),
                role.getModuleCount(),
                role.isTrending()
        );
    }
}
