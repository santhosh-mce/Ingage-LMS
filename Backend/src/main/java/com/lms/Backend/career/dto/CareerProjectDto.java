package com.lms.Backend.career.dto;

public record CareerProjectDto(
    Long id,
    String title,
    String description,
    String difficulty,
    String technologies,
    Integer displayOrder
) {}
