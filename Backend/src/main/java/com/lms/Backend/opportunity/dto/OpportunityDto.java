package com.lms.Backend.opportunity.dto;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class OpportunityDto {
    private String id;
    private String title;
    private String company;
    private String companyLogo;
    private String location;
    private String type; // Job, Internship, Freelance, Apprenticeship
    private String workMode; // Remote, Hybrid, On-site
    private String salary;
    private Double salaryMin;
    private Double salaryMax;
    private String salaryPeriod;
    private String currency;
    private String experienceLevel;
    private String category;
    private Integer matchScore;
    private String description;
    private String aboutCompany;
    private List<SkillItem> requiredSkills = new ArrayList<>();
    private List<String> responsibilities = new ArrayList<>();
    private List<String> qualifications = new ArrayList<>();
    private List<String> benefits = new ArrayList<>();
    private String deadline;
    private String postedDate;
    private String roleTrackId;
    private boolean active;
    private boolean published;
    private boolean featured;
    private Integer displayOrder;
    private Instant createdAt;
    private Instant updatedAt;

    public static class SkillItem {
        private String name;
        private boolean matched;

        public SkillItem() {}
        public SkillItem(String name, boolean matched) {
            this.name = name;
            this.matched = matched;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public boolean isMatched() { return matched; }
        public void setMatched(boolean matched) { this.matched = matched; }
    }

    public OpportunityDto() {}

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getCompanyLogo() { return companyLogo; }
    public void setCompanyLogo(String companyLogo) { this.companyLogo = companyLogo; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getWorkMode() { return workMode; }
    public void setWorkMode(String workMode) { this.workMode = workMode; }

    public String getSalary() { return salary; }
    public void setSalary(String salary) { this.salary = salary; }

    public Double getSalaryMin() { return salaryMin; }
    public void setSalaryMin(Double salaryMin) { this.salaryMin = salaryMin; }

    public Double getSalaryMax() { return salaryMax; }
    public void setSalaryMax(Double salaryMax) { this.salaryMax = salaryMax; }

    public String getSalaryPeriod() { return salaryPeriod; }
    public void setSalaryPeriod(String salaryPeriod) { this.salaryPeriod = salaryPeriod; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getExperienceLevel() { return experienceLevel; }
    public void setExperienceLevel(String experienceLevel) { this.experienceLevel = experienceLevel; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Integer getMatchScore() { return matchScore; }
    public void setMatchScore(Integer matchScore) { this.matchScore = matchScore; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getAboutCompany() { return aboutCompany; }
    public void setAboutCompany(String aboutCompany) { this.aboutCompany = aboutCompany; }

    public List<SkillItem> getRequiredSkills() { return requiredSkills; }
    public void setRequiredSkills(List<SkillItem> requiredSkills) { this.requiredSkills = requiredSkills; }

    public List<String> getResponsibilities() { return responsibilities; }
    public void setResponsibilities(List<String> responsibilities) { this.responsibilities = responsibilities; }

    public List<String> getQualifications() { return qualifications; }
    public void setQualifications(List<String> qualifications) { this.qualifications = qualifications; }

    public List<String> getBenefits() { return benefits; }
    public void setBenefits(List<String> benefits) { this.benefits = benefits; }

    public String getDeadline() { return deadline; }
    public void setDeadline(String deadline) { this.deadline = deadline; }

    public String getPostedDate() { return postedDate; }
    public void setPostedDate(String postedDate) { this.postedDate = postedDate; }

    public String getRoleTrackId() { return roleTrackId; }
    public void setRoleTrackId(String roleTrackId) { this.roleTrackId = roleTrackId; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public boolean isPublished() { return published; }
    public void setPublished(boolean published) { this.published = published; }

    public boolean isFeatured() { return featured; }
    public void setFeatured(boolean featured) { this.featured = featured; }

    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
