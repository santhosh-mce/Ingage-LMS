package com.lms.Backend.user.dto;

public class CareerGoalDto {
    private String targetJobRole;
    private String preferredIndustry;
    private String experienceLevel;
    private String preferredLocation;
    private String careerGoal;
    private boolean openToWork;

    public CareerGoalDto() {}

    public String getTargetJobRole() { return targetJobRole; }
    public void setTargetJobRole(String targetJobRole) { this.targetJobRole = targetJobRole; }
    public String getPreferredIndustry() { return preferredIndustry; }
    public void setPreferredIndustry(String preferredIndustry) { this.preferredIndustry = preferredIndustry; }
    public String getExperienceLevel() { return experienceLevel; }
    public void setExperienceLevel(String experienceLevel) { this.experienceLevel = experienceLevel; }
    public String getPreferredLocation() { return preferredLocation; }
    public void setPreferredLocation(String preferredLocation) { this.preferredLocation = preferredLocation; }
    public String getCareerGoal() { return careerGoal; }
    public void setCareerGoal(String careerGoal) { this.careerGoal = careerGoal; }
    public boolean isOpenToWork() { return openToWork; }
    public void setOpenToWork(boolean openToWork) { this.openToWork = openToWork; }
}
