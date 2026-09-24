package com.lms.Backend.project.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
    name = "projects",
    indexes = {
        @Index(name = "idx_projects_slug", columnList = "slug", unique = true),
        @Index(name = "idx_projects_industry", columnList = "industry"),
        @Index(name = "idx_projects_category", columnList = "category"),
        @Index(name = "idx_projects_active", columnList = "active"),
        @Index(name = "idx_projects_published", columnList = "published")
    }
)
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, unique = true, length = 255)
    private String slug;

    @Column(nullable = false, length = 100)
    private String industry;

    @Column(nullable = false, length = 100)
    private String category;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 50)
    private String difficulty = "Beginner";

    @Column(length = 50)
    private String duration = "25h";

    @Column(name = "skills_count")
    private Integer skillsCount = 5;

    @Column(name = "learners_count")
    private Integer learnersCount = 1000;

    @Column(name = "image_url", length = 1000)
    private String imageUrl;

    @Column(columnDefinition = "TEXT")
    private String prerequisites;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "project_tech_stacks", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "tech")
    private List<String> techStack = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @org.hibernate.annotations.BatchSize(size = 25)
    @CollectionTable(name = "project_what_you_will_build", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "item", columnDefinition = "TEXT")
    private List<String> whatYouWillBuild = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @org.hibernate.annotations.BatchSize(size = 25)
    @CollectionTable(name = "project_learning_outcomes", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "outcome", columnDefinition = "TEXT")
    private List<String> learningOutcomes = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @org.hibernate.annotations.BatchSize(size = 25)
    @CollectionTable(name = "project_skills_learned", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "skill")
    private List<String> skillsLearned = new ArrayList<>();

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private boolean published = true;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        if (this.difficulty == null) this.difficulty = "Beginner";
        if (this.duration == null) this.duration = "25h";
        if (this.industry == null && this.category != null) this.industry = this.category;
        if (this.category == null && this.industry != null) this.category = this.industry;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    // Getters and Setters
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

    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
