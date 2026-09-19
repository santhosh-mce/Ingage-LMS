package com.lms.Backend.career.dto;

import java.time.Instant;
import java.util.List;

public record CareerDetailResponse(
    Long id,
    String title,
    String slug,
    String category,
    String description,
    String shortDescription,
    String level,
    String duration,
    CareerResponse.SalaryInfo salary,
    String imageUrl,
    String icon,
    Boolean featured,
    Boolean popular,
    Boolean active,
    Integer displayOrder,
    String jobOpenings,
    Integer modulesCount,
    String certificationName,
    List<CareerSkillDto> skills,
    List<CareerResponsibilityDto> responsibilities,
    List<CareerRoadmapDto> roadmap,
    List<CareerProjectDto> projects,
    List<CareerCourseDto> courses,
    List<CareerOpportunityDto> jobOpportunities,
    CareerStatsResponse stats,
    Instant createdAt,
    Instant updatedAt
) {}
