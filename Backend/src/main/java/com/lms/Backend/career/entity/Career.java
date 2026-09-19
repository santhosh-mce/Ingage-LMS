package com.lms.Backend.career.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
    name = "careers",
    indexes = {
        @Index(name = "idx_careers_slug", columnList = "slug", unique = true),
        @Index(name = "idx_careers_category", columnList = "category"),
        @Index(name = "idx_careers_level", columnList = "level"),
        @Index(name = "idx_careers_active", columnList = "active"),
        @Index(name = "idx_careers_published", columnList = "published"),
        @Index(name = "idx_careers_featured", columnList = "featured")
    }
)
public class Career {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, unique = true, length = 255)
    private String slug;

    @Column(nullable = false, length = 100)
    private String category;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "short_description", length = 500)
    private String shortDescription;

    @Column(nullable = false, length = 50)
    private String level;

    @Column(nullable = false, length = 50)
    private String duration;

    @Column(name = "salary_min", nullable = false)
    private Long salaryMin;

    @Column(name = "salary_max", nullable = false)
    private Long salaryMax;

    @Column(name = "salary_currency", nullable = false, length = 10)
    private String salaryCurrency = "INR";

    @Column(name = "image_url", length = 1000)
    private String imageUrl;

    @Column(length = 100)
    private String icon;

    @Column(nullable = false)
    private boolean featured = false;

    @Column(nullable = false)
    private boolean popular = false;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private boolean published = true;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 0;

    @Column(name = "job_openings", length = 50)
    private String jobOpenings;

    @Column(name = "modules_count")
    private Integer modulesCount = 0;

    @Column(name = "certification_name", length = 255)
    private String certificationName;

    @com.fasterxml.jackson.annotation.JsonIgnore
    @OneToMany(mappedBy = "career", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("displayOrder ASC")
    private List<CareerSkill> skills = new ArrayList<>();

    @com.fasterxml.jackson.annotation.JsonIgnore
    @OneToMany(mappedBy = "career", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("displayOrder ASC")
    private List<CareerResponsibility> responsibilities = new ArrayList<>();

    @com.fasterxml.jackson.annotation.JsonIgnore
    @OneToMany(mappedBy = "career", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("displayOrder ASC")
    private List<CareerRoadmap> roadmaps = new ArrayList<>();

    @com.fasterxml.jackson.annotation.JsonIgnore
    @OneToMany(mappedBy = "career", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("displayOrder ASC")
    private List<CareerProject> projects = new ArrayList<>();

    @com.fasterxml.jackson.annotation.JsonIgnore
    @OneToMany(mappedBy = "career", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("displayOrder ASC")
    private List<CareerCourse> careerCourses = new ArrayList<>();

    @com.fasterxml.jackson.annotation.JsonIgnore
    @OneToMany(mappedBy = "career", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("displayOrder ASC")
    private List<CareerOpportunity> opportunities = new ArrayList<>();

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

    public Career() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getShortDescription() { return shortDescription; }
    public void setShortDescription(String shortDescription) { this.shortDescription = shortDescription; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public Long getSalaryMin() { return salaryMin; }
    public void setSalaryMin(Long salaryMin) { this.salaryMin = salaryMin; }

    public Long getSalaryMax() { return salaryMax; }
    public void setSalaryMax(Long salaryMax) { this.salaryMax = salaryMax; }

    public String getSalaryCurrency() { return salaryCurrency; }
    public void setSalaryCurrency(String salaryCurrency) { this.salaryCurrency = salaryCurrency; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public boolean isFeatured() { return featured; }
    public void setFeatured(boolean featured) { this.featured = featured; }

    public boolean isPopular() { return popular; }
    public void setPopular(boolean popular) { this.popular = popular; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public boolean isPublished() { return published; }
    public void setPublished(boolean published) { this.published = published; }

    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }

    public String getJobOpenings() { return jobOpenings; }
    public void setJobOpenings(String jobOpenings) { this.jobOpenings = jobOpenings; }

    public Integer getModulesCount() { return modulesCount; }
    public void setModulesCount(Integer modulesCount) { this.modulesCount = modulesCount; }

    public String getCertificationName() { return certificationName; }
    public void setCertificationName(String certificationName) { this.certificationName = certificationName; }

    public List<CareerSkill> getSkills() { return skills; }
    public void setSkills(List<CareerSkill> skills) { this.skills = skills; }

    public List<CareerResponsibility> getResponsibilities() { return responsibilities; }
    public void setResponsibilities(List<CareerResponsibility> responsibilities) { this.responsibilities = responsibilities; }

    public List<CareerRoadmap> getRoadmaps() { return roadmaps; }
    public void setRoadmaps(List<CareerRoadmap> roadmaps) { this.roadmaps = roadmaps; }

    public List<CareerProject> getProjects() { return projects; }
    public void setProjects(List<CareerProject> projects) { this.projects = projects; }

    public List<CareerCourse> getCareerCourses() { return careerCourses; }
    public void setCareerCourses(List<CareerCourse> careerCourses) { this.careerCourses = careerCourses; }

    public List<CareerOpportunity> getOpportunities() { return opportunities; }
    public void setOpportunities(List<CareerOpportunity> opportunities) { this.opportunities = opportunities; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    // Convenience helper methods for bi-directional relationship
    public void addSkill(CareerSkill skill) {
        skills.add(skill);
        skill.setCareer(this);
    }

    public void addResponsibility(CareerResponsibility responsibility) {
        responsibilities.add(responsibility);
        responsibility.setCareer(this);
    }

    public void addRoadmap(CareerRoadmap roadmap) {
        roadmaps.add(roadmap);
        roadmap.setCareer(this);
    }

    public void addProject(CareerProject project) {
        projects.add(project);
        project.setCareer(this);
    }

    public void addCourse(CareerCourse careerCourse) {
        careerCourses.add(careerCourse);
        careerCourse.setCareer(this);
    }

    public void addOpportunity(CareerOpportunity opportunity) {
        opportunities.add(opportunity);
        opportunity.setCareer(this);
    }
}
