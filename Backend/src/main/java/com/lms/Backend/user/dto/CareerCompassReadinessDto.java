package com.lms.Backend.user.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CareerCompassReadinessDto {
    private String targetJobRole;
    private String careerTitle;
    private String careerSlug;
    private String careerCategory;
    private String careerLevel;
    private int readinessPercentage;
    private List<String> matchedSkills = new ArrayList<>();
    private List<String> missingSkills = new ArrayList<>();
    private List<String> allRequiredSkills = new ArrayList<>();
    private List<Map<String, Object>> recommendedCourses = new ArrayList<>();
    private List<Map<String, Object>> recommendedProjects = new ArrayList<>();
    private List<Map<String, Object>> recommendedCredentials = new ArrayList<>();

    public CareerCompassReadinessDto() {}

    public String getTargetJobRole() { return targetJobRole; }
    public void setTargetJobRole(String targetJobRole) { this.targetJobRole = targetJobRole; }
    public String getCareerTitle() { return careerTitle; }
    public void setCareerTitle(String careerTitle) { this.careerTitle = careerTitle; }
    public String getCareerSlug() { return careerSlug; }
    public void setCareerSlug(String careerSlug) { this.careerSlug = careerSlug; }
    public String getCareerCategory() { return careerCategory; }
    public void setCareerCategory(String careerCategory) { this.careerCategory = careerCategory; }
    public String getCareerLevel() { return careerLevel; }
    public void setCareerLevel(String careerLevel) { this.careerLevel = careerLevel; }
    public int getReadinessPercentage() { return readinessPercentage; }
    public void setReadinessPercentage(int readinessPercentage) { this.readinessPercentage = readinessPercentage; }
    public List<String> getMatchedSkills() { return matchedSkills; }
    public void setMatchedSkills(List<String> matchedSkills) { this.matchedSkills = matchedSkills; }
    public List<String> getMissingSkills() { return missingSkills; }
    public void setMissingSkills(List<String> missingSkills) { this.missingSkills = missingSkills; }
    public List<String> getAllRequiredSkills() { return allRequiredSkills; }
    public void setAllRequiredSkills(List<String> allRequiredSkills) { this.allRequiredSkills = allRequiredSkills; }
    public List<Map<String, Object>> getRecommendedCourses() { return recommendedCourses; }
    public void setRecommendedCourses(List<Map<String, Object>> recommendedCourses) { this.recommendedCourses = recommendedCourses; }
    public List<Map<String, Object>> getRecommendedProjects() { return recommendedProjects; }
    public void setRecommendedProjects(List<Map<String, Object>> recommendedProjects) { this.recommendedProjects = recommendedProjects; }
    public List<Map<String, Object>> getRecommendedCredentials() { return recommendedCredentials; }
    public void setRecommendedCredentials(List<Map<String, Object>> recommendedCredentials) { this.recommendedCredentials = recommendedCredentials; }
}
