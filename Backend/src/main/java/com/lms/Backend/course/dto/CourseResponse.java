package com.lms.Backend.course.dto;

import java.time.Instant;

public record CourseResponse(
    Long id,
    String title,
    String description,
    String thumbnail,
    String category,
    String level,
    String duration,
    String instructor,
    Integer price,
    Boolean published,
    Instant createdAt,
    Instant updatedAt
) {
}
