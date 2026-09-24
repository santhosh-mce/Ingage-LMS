package com.lms.Backend.course.dto;

public class CourseAccessDetailDto {

    private Long courseId;
    private boolean hasAccess;
    private String accessType; // NONE, DIRECT_COURSE, CAREER_PATH_INCLUDED, BOTH
    private Long careerPathId;
    private String careerPathName;
    private String careerPathSlug;
    private Long directEnrollmentId;
    private String directStatus;
    private Integer progressPercentage;
    private boolean completed;
    private boolean certificateAvailable;

    public CourseAccessDetailDto() {}

    public CourseAccessDetailDto(
        Long courseId,
        boolean hasAccess,
        String accessType,
        Long careerPathId,
        String careerPathName,
        String careerPathSlug,
        Long directEnrollmentId,
        String directStatus,
        Integer progressPercentage,
        boolean completed,
        boolean certificateAvailable
    ) {
        this.courseId = courseId;
        this.hasAccess = hasAccess;
        this.accessType = accessType;
        this.careerPathId = careerPathId;
        this.careerPathName = careerPathName;
        this.careerPathSlug = careerPathSlug;
        this.directEnrollmentId = directEnrollmentId;
        this.directStatus = directStatus;
        this.progressPercentage = progressPercentage;
        this.completed = completed;
        this.certificateAvailable = certificateAvailable;
    }

    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }

    public boolean isHasAccess() { return hasAccess; }
    public void setHasAccess(boolean hasAccess) { this.hasAccess = hasAccess; }

    public String getAccessType() { return accessType; }
    public void setAccessType(String accessType) { this.accessType = accessType; }

    public Long getCareerPathId() { return careerPathId; }
    public void setCareerPathId(Long careerPathId) { this.careerPathId = careerPathId; }

    public String getCareerPathName() { return careerPathName; }
    public void setCareerPathName(String careerPathName) { this.careerPathName = careerPathName; }

    public String getCareerPathSlug() { return careerPathSlug; }
    public void setCareerPathSlug(String careerPathSlug) { this.careerPathSlug = careerPathSlug; }

    public Long getDirectEnrollmentId() { return directEnrollmentId; }
    public void setDirectEnrollmentId(Long directEnrollmentId) { this.directEnrollmentId = directEnrollmentId; }

    public String getDirectStatus() { return directStatus; }
    public void setDirectStatus(String directStatus) { this.directStatus = directStatus; }

    public Integer getProgressPercentage() { return progressPercentage; }
    public void setProgressPercentage(Integer progressPercentage) { this.progressPercentage = progressPercentage; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public boolean isCertificateAvailable() { return certificateAvailable; }
    public void setCertificateAvailable(boolean certificateAvailable) { this.certificateAvailable = certificateAvailable; }
}
