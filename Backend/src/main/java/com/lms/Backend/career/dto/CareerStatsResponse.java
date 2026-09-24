package com.lms.Backend.career.dto;

public record CareerStatsResponse(
    long courseCount,
    long projectCount,
    long jobOpportunityCount,
    long skillCount
) {}
