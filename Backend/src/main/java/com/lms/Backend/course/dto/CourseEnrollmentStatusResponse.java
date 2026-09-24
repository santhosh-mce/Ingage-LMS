package com.lms.Backend.course.dto;

public record CourseEnrollmentStatusResponse(
    Long courseId,
    boolean enrolled,
    String enrollmentStatus,
    String paymentStatus,
    Integer progress,
    boolean courseAccess,
    boolean completed
) {}
