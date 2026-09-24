package com.lms.Backend.user.dto;

public class DashboardSummaryDto {
    private int coursesEnrolled;
    private int coursesCompleted;
    private int projectsCompleted;
    private int credentialsEarned;
    private int careerReadinessPercentage;

    public DashboardSummaryDto() {}

    public DashboardSummaryDto(int coursesEnrolled, int coursesCompleted, int projectsCompleted, int credentialsEarned, int careerReadinessPercentage) {
        this.coursesEnrolled = coursesEnrolled;
        this.coursesCompleted = coursesCompleted;
        this.projectsCompleted = projectsCompleted;
        this.credentialsEarned = credentialsEarned;
        this.careerReadinessPercentage = careerReadinessPercentage;
    }

    public int getCoursesEnrolled() { return coursesEnrolled; }
    public void setCoursesEnrolled(int coursesEnrolled) { this.coursesEnrolled = coursesEnrolled; }
    public int getCoursesCompleted() { return coursesCompleted; }
    public void setCoursesCompleted(int coursesCompleted) { this.coursesCompleted = coursesCompleted; }
    public int getProjectsCompleted() { return projectsCompleted; }
    public void setProjectsCompleted(int projectsCompleted) { this.projectsCompleted = projectsCompleted; }
    public int getCredentialsEarned() { return credentialsEarned; }
    public void setCredentialsEarned(int credentialsEarned) { this.credentialsEarned = credentialsEarned; }
    public int getCareerReadinessPercentage() { return careerReadinessPercentage; }
    public void setCareerReadinessPercentage(int careerReadinessPercentage) { this.careerReadinessPercentage = careerReadinessPercentage; }
}
