package com.lms.Backend.career.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CareerCourseDto(
    Long id,
    Long courseId,
    String courseTitle,
    String courseThumbnail,
    String courseCategory,
    String courseLevel,
    Integer coursePrice,
    Integer displayOrder,
    String enrollmentStatus,
    String paymentStatus,
    Integer progress,
    boolean courseAccess,
    boolean completed,
    boolean included,
    boolean requiredForCompletion
) {
    public CareerCourseDto(
        Long id,
        Long courseId,
        String courseTitle,
        String courseThumbnail,
        String courseCategory,
        String courseLevel,
        Integer coursePrice,
        Integer displayOrder,
        String enrollmentStatus,
        String paymentStatus,
        Integer progress,
        boolean courseAccess,
        boolean completed
    ) {
        this(id, courseId, courseTitle, courseThumbnail, courseCategory, courseLevel, coursePrice, displayOrder, enrollmentStatus, paymentStatus, progress, courseAccess, completed, true, true);
    }

    public CareerCourseDto(
        Long id,
        Long courseId,
        String courseTitle,
        String courseThumbnail,
        String courseCategory,
        String courseLevel,
        Integer coursePrice,
        Integer displayOrder
    ) {
        this(id, courseId, courseTitle, courseThumbnail, courseCategory, courseLevel, coursePrice, displayOrder, "NOT_ENROLLED", null, 0, false, false, true, true);
    }

    @JsonProperty("title")
    public String title() {
        return courseTitle;
    }

    @JsonProperty("imageUrl")
    public String imageUrl() {
        return courseThumbnail;
    }

    @JsonProperty("category")
    public String category() {
        return courseCategory;
    }

    @JsonProperty("level")
    public String level() {
        return courseLevel;
    }

    @JsonProperty("price")
    public Integer price() {
        return coursePrice;
    }
}
