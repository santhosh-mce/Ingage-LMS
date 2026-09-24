package com.lms.Backend.career.dto;

import java.util.List;

public record CareerResponse(
    Long id,
    String title,
    String slug,
    String category,
    String description,
    String shortDescription,
    String level,
    String duration,
    SalaryInfo salary,
    String imageUrl,
    String icon,
    Boolean featured,
    Boolean popular,
    Boolean active,
    Integer displayOrder,
    String jobOpenings,
    Integer modulesCount,
    String certificationName,
    List<String> skills
) {
    public record SalaryInfo(
        Long min,
        Long max,
        String currency,
        String formatted
    ) {}
}
