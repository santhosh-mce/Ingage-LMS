package com.lms.Backend.credential.dto;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class CredentialCourseDto {

    private Long id;
    private String title;
    private String slug;
    private String provider = "Google";
    private String category;
    private String level;
    private String duration;
    private String description;
    private String shortDescription;
    private String thumbnail;
    private String credentialName;
    private String credentialType;
    private String credentialUrl;
    private Double price;
    private Double discount;
    private boolean free;
    private boolean published;
    private boolean featured;
    private Double rating;
    private Integer learnersCount;
    private String careerSlug;
    private List<String> learningOutcomes = new ArrayList<>();
    private List<String> prerequisites = new ArrayList<>();
    private List<CredentialModuleDto> modules = new ArrayList<>();
    private Instant createdAt;
    private Instant updatedAt;

    // User-specific contextual fields
    private boolean enrolled = false;
    private Integer progressPercentage = 0;
    private String userStatus = "NOT_ENROLLED";
    private Integer completedModulesCount = 0;
    private String credentialId;

    public CredentialCourseDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getShortDescription() { return shortDescription; }
    public void setShortDescription(String shortDescription) { this.shortDescription = shortDescription; }

    public String getThumbnail() { return thumbnail; }
    public void setThumbnail(String thumbnail) { this.thumbnail = thumbnail; }

    public String getCredentialName() { return credentialName; }
    public void setCredentialName(String credentialName) { this.credentialName = credentialName; }

    public String getCredentialType() { return credentialType; }
    public void setCredentialType(String credentialType) { this.credentialType = credentialType; }

    public String getCredentialUrl() { return credentialUrl; }
    public void setCredentialUrl(String credentialUrl) { this.credentialUrl = credentialUrl; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Double getDiscount() { return discount; }
    public void setDiscount(Double discount) { this.discount = discount; }

    public boolean isFree() { return free; }
    public void setFree(boolean free) { this.free = free; }

    public boolean isPublished() { return published; }
    public void setPublished(boolean published) { this.published = published; }

    public boolean isFeatured() { return featured; }
    public void setFeatured(boolean featured) { this.featured = featured; }

    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }

    public Integer getLearnersCount() { return learnersCount; }
    public void setLearnersCount(Integer learnersCount) { this.learnersCount = learnersCount; }

    public String getCareerSlug() { return careerSlug; }
    public void setCareerSlug(String careerSlug) { this.careerSlug = careerSlug; }

    public List<String> getLearningOutcomes() { return learningOutcomes; }
    public void setLearningOutcomes(List<String> learningOutcomes) { this.learningOutcomes = learningOutcomes; }

    public List<String> getPrerequisites() { return prerequisites; }
    public void setPrerequisites(List<String> prerequisites) { this.prerequisites = prerequisites; }

    public List<CredentialModuleDto> getModules() { return modules; }
    public void setModules(List<CredentialModuleDto> modules) { this.modules = modules; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public boolean isEnrolled() { return enrolled; }
    public void setEnrolled(boolean enrolled) { this.enrolled = enrolled; }

    public Integer getProgressPercentage() { return progressPercentage; }
    public void setProgressPercentage(Integer progressPercentage) { this.progressPercentage = progressPercentage; }

    public String getUserStatus() { return userStatus; }
    public void setUserStatus(String userStatus) { this.userStatus = userStatus; }

    public Integer getCompletedModulesCount() { return completedModulesCount; }
    public void setCompletedModulesCount(Integer completedModulesCount) { this.completedModulesCount = completedModulesCount; }

    public String getCredentialId() { return credentialId; }
    public void setCredentialId(String credentialId) { this.credentialId = credentialId; }
}
