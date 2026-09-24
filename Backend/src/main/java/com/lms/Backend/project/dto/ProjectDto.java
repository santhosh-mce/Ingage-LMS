package com.lms.Backend.project.dto;

import java.util.ArrayList;
import java.util.List;

public class ProjectDto {
    private Long id;
    private String title;
    private String slug;
    private String industry;
    private String category;
    private String description;
    private String difficulty;
    private String duration;
    private Integer skillsCount;
    private Integer learnersCount;
    private String imageUrl;
    private String prerequisites;
    private List<String> techStack = new ArrayList<>();
    private List<String> whatYouWillBuild = new ArrayList<>();
    private List<String> learningOutcomes = new ArrayList<>();
    private List<String> skillsLearned = new ArrayList<>();
    private boolean active;
    private boolean published;
    private Integer displayOrder;

    public ProjectDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getIndustry() { return industry; }
    public void setIndustry(String industry) { this.industry = industry; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public Integer getSkillsCount() { return skillsCount; }
    public void setSkillsCount(Integer skillsCount) { this.skillsCount = skillsCount; }

    public Integer getLearnersCount() { return learnersCount; }
    public void setLearnersCount(Integer learnersCount) { this.learnersCount = learnersCount; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getPrerequisites() { return prerequisites; }
    public void setPrerequisites(String prerequisites) { this.prerequisites = prerequisites; }

    public List<String> getTechStack() { return techStack; }
    public void setTechStack(List<String> techStack) { this.techStack = techStack; }

    public List<String> getWhatYouWillBuild() { return whatYouWillBuild; }
    public void setWhatYouWillBuild(List<String> whatYouWillBuild) { this.whatYouWillBuild = whatYouWillBuild; }

    public List<String> getLearningOutcomes() { return learningOutcomes; }
    public void setLearningOutcomes(List<String> learningOutcomes) { this.learningOutcomes = learningOutcomes; }

    public List<String> getSkillsLearned() { return skillsLearned; }
    public void setSkillsLearned(List<String> skillsLearned) { this.skillsLearned = skillsLearned; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public boolean isPublished() { return published; }
    public void setPublished(boolean published) { this.published = published; }

    private java.time.Instant createdAt;
    private java.time.Instant updatedAt;

    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }

    public java.time.Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(java.time.Instant createdAt) { this.createdAt = createdAt; }

    public java.time.Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(java.time.Instant updatedAt) { this.updatedAt = updatedAt; }
}
