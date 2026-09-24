package com.lms.Backend.career.dto;

public record CareerSkillDto(
    Long id,
    String skillName,
    String skillType,
    Integer displayOrder
) {}
