package com.lms.Backend.career.dto;

public record CareerRoadmapDto(
    Long id,
    String title,
    String description,
    String duration,
    Integer displayOrder
) {}
