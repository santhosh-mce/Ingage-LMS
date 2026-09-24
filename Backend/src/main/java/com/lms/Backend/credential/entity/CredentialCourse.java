package com.lms.Backend.credential.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "credential_courses")
public class CredentialCourse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, unique = true, length = 255)
    private String slug;

    @Column(nullable = false, length = 100)
    private String provider = "Google";

    @Column(nullable = false, length = 100)
    private String category;

    @Column(length = 50)
    private String level = "Beginner";

    @Column(length = 100)
    private String duration;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "short_description", columnDefinition = "TEXT")
    private String shortDescription;

    @Column(length = 1000)
    private String thumbnail;

    @Column(name = "credential_name", length = 255)
    private String credentialName;

    @Column(name = "credential_type", length = 100)
    private String credentialType = "Professional Certificate";

    @Column(name = "credential_url", length = 1000)
    private String credentialUrl;

    @Column(nullable = false)
    private Double price = 0.0;

    @Column
    private Double discount = 0.0;

    @Column(name = "is_free", nullable = false)
    private boolean free = true;

    @Column(nullable = false)
    private boolean published = true;

    @Column(nullable = false)
    private boolean featured = true;

    @Column
    private Double rating = 4.8;

    @Column(name = "learners_count")
    private Integer learnersCount = 1200;

    @Column(name = "career_slug", length = 100)
    private String careerSlug;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "credential_course_outcomes", joinColumns = @JoinColumn(name = "course_id"))
    @Column(name = "outcome", columnDefinition = "TEXT")
    @org.hibernate.annotations.BatchSize(size = 25)
    private List<String> learningOutcomes = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "credential_course_prerequisites", joinColumns = @JoinColumn(name = "course_id"))
    @Column(name = "prerequisite", length = 500)
    @org.hibernate.annotations.BatchSize(size = 25)
    private List<String> prerequisites = new ArrayList<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("orderIndex ASC")
    private List<CredentialModule> modules = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public CredentialCourse() {}

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

    public List<CredentialModule> getModules() { return modules; }
    public void setModules(List<CredentialModule> modules) { this.modules = modules; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
