package com.lms.Backend.course.dto;

import java.time.Instant;

public record CourseResponse(
    Long id,
    String title,
    String description,
    String shortDescription,
    String thumbnail,
    String category,
    String level,
    String duration,
    String instructor,
    Integer price,
    Double discountPrice,
    String status,
    Boolean published,
    Instant createdAt,
    Instant updatedAt
) {
    public CourseResponse(
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
        this(id, title, description, null, thumbnail, category, level, duration, instructor, price, price != null ? (double) price : 0.0, published ? "PUBLISHED" : "DRAFT", published, createdAt, updatedAt);
    }
}
