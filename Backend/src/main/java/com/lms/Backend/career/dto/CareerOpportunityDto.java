package com.lms.Backend.career.dto;

public record CareerOpportunityDto(
    Long id,
    String title,
    String company,
    String location,
    String type,
    String salary,
    Integer displayOrder
) {}
