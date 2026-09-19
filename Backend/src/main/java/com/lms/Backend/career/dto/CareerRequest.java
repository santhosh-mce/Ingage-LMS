package com.lms.Backend.career.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.List;

public record CareerRequest(
    @NotBlank(message = "Title is required")
    String title,

    @NotBlank(message = "Slug is required")
    String slug,

    @NotBlank(message = "Category is required")
    String category,

    @NotBlank(message = "Description is required")
    String description,

    String shortDescription,

    @NotBlank(message = "Level is required")
    String level,

    @NotBlank(message = "Duration is required")
    String duration,

    @NotNull(message = "Minimum salary is required")
    @PositiveOrZero(message = "Minimum salary cannot be negative")
    Long salaryMin,

    @NotNull(message = "Maximum salary is required")
    @PositiveOrZero(message = "Maximum salary cannot be negative")
    Long salaryMax,

    String salaryCurrency,

    String imageUrl,

    String icon,

    Boolean featured,

    Boolean popular,

    Boolean active,

    Integer displayOrder,

    String jobOpenings,

    Integer modulesCount,

    String certificationName,

    List<SkillItem> skills,

    List<String> responsibilities,

    List<RoadmapItem> roadmaps,

    List<ProjectItem> projects,

    List<Long> courseIds,

    List<OpportunityItem> opportunities
) {
    public record SkillItem(String name, String type, Integer order) {}
    public record RoadmapItem(String title, String description, String duration, Integer order) {}
    public record ProjectItem(String title, String description, String difficulty, String technologies, Integer order) {}
    public record OpportunityItem(String title, String company, String location, String type, String salary, Integer order) {}
}
