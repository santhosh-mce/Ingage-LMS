package com.lms.Backend.credential.dto;

import java.time.Instant;

public class UserCredentialDto {

    private Long enrollmentId;
    private Long courseId;
    private String courseTitle;
    private String courseSlug;
    private String courseThumbnail;
    private String category;
    private String level;
    private String provider = "Google";
    private String credentialName;
    private String credentialType;
    private String credentialUrl;
    private String status; // IN_PROGRESS, COMPLETED, CREDENTIAL_EARNED, CREDENTIAL_PENDING
    private Integer progressPercentage;
    private Integer completedModulesCount;
    private Integer totalModulesCount;
    private String credentialId;
    private Instant enrolledAt;
    private Instant completedAt;

    public UserCredentialDto() {}

    public Long getEnrollmentId() { return enrollmentId; }
    public void setEnrollmentId(Long enrollmentId) { this.enrollmentId = enrollmentId; }

    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }

    public String getCourseTitle() { return courseTitle; }
    public void setCourseTitle(String courseTitle) { this.courseTitle = courseTitle; }

    public String getCourseSlug() { return courseSlug; }
    public void setCourseSlug(String courseSlug) { this.courseSlug = courseSlug; }

    public String getCourseThumbnail() { return courseThumbnail; }
    public void setCourseThumbnail(String courseThumbnail) { this.courseThumbnail = courseThumbnail; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }

    public String getCredentialName() { return credentialName; }
    public void setCredentialName(String credentialName) { this.credentialName = credentialName; }

    public String getCredentialType() { return credentialType; }
    public void setCredentialType(String credentialType) { this.credentialType = credentialType; }

    public String getCredentialUrl() { return credentialUrl; }
    public void setCredentialUrl(String credentialUrl) { this.credentialUrl = credentialUrl; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getProgressPercentage() { return progressPercentage; }
    public void setProgressPercentage(Integer progressPercentage) { this.progressPercentage = progressPercentage; }

    public Integer getCompletedModulesCount() { return completedModulesCount; }
    public void setCompletedModulesCount(Integer completedModulesCount) { this.completedModulesCount = completedModulesCount; }

    public Integer getTotalModulesCount() { return totalModulesCount; }
    public void setTotalModulesCount(Integer totalModulesCount) { this.totalModulesCount = totalModulesCount; }

    public String getCredentialId() { return credentialId; }
    public void setCredentialId(String credentialId) { this.credentialId = credentialId; }

    public Instant getEnrolledAt() { return enrolledAt; }
    public void setEnrolledAt(Instant enrolledAt) { this.enrolledAt = enrolledAt; }

    public Instant getCompletedAt() { return completedAt; }
    public void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }
}
