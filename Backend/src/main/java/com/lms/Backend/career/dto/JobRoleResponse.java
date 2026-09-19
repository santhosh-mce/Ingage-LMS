package com.lms.Backend.career.dto;

public record JobRoleResponse(
    Long id,
    String title,
    String slug,
    String description,
    String imageUrl,
    String iconName,
    String difficultyLevel,
    Integer durationMonths,
    Integer minimumSalary,
    Integer maximumSalary,
    Integer jobOpenings,
    Integer moduleCount,
    Boolean trending
) {
}
