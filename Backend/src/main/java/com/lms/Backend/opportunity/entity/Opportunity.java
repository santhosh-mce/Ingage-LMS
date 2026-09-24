package com.lms.Backend.opportunity.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
    name = "opportunities",
    indexes = {
        @Index(name = "idx_opportunities_type", columnList = "type"),
        @Index(name = "idx_opportunities_work_mode", columnList = "work_mode"),
        @Index(name = "idx_opportunities_active", columnList = "active"),
        @Index(name = "idx_opportunities_published", columnList = "published")
    }
)
public class Opportunity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, length = 255)
    private String company;

    @Column(name = "company_logo", length = 1000)
    private String companyLogo;

    @Column(nullable = false, length = 255)
    private String location;

    @Column(nullable = false, length = 50)
    private String type = "Job"; // Job, Internship, Freelance, Apprenticeship

    @Column(name = "work_mode", nullable = false, length = 50)
    private String workMode = "Remote"; // Remote, Hybrid, On-site

    @Column(length = 100)
    private String salary;

    @Column(name = "salary_min")
    private Double salaryMin;

    @Column(name = "salary_max")
    private Double salaryMax;

    @Column(name = "salary_period", length = 50)
    private String salaryPeriod;

    @Column(length = 20)
    private String currency = "INR";

    @Column(name = "experience_level", length = 100)
    private String experienceLevel;

    @Column(length = 100)
    private String category;

    @Column(name = "match_score")
    private Integer matchScore = 85;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "about_company", columnDefinition = "TEXT")
    private String aboutCompany;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "opportunity_skills", joinColumns = @JoinColumn(name = "opportunity_id"))
    @Column(name = "skill")
    private List<String> requiredSkills = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "opportunity_responsibilities", joinColumns = @JoinColumn(name = "opportunity_id"))
    @Column(name = "responsibility", columnDefinition = "TEXT")
    private List<String> responsibilities = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "opportunity_qualifications", joinColumns = @JoinColumn(name = "opportunity_id"))
    @Column(name = "qualification", columnDefinition = "TEXT")
    private List<String> qualifications = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "opportunity_benefits", joinColumns = @JoinColumn(name = "opportunity_id"))
    @Column(name = "benefit", columnDefinition = "TEXT")
    private List<String> benefits = new ArrayList<>();

    @Column(length = 100)
    private String deadline;

    @Column(name = "role_track_id", length = 100)
    private String roleTrackId;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private boolean published = true;

    @Column(nullable = false)
    private boolean featured = false;

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
        if (this.type == null) this.type = "Job";
        if (this.workMode == null) this.workMode = "Remote";
        if (this.matchScore == null) this.matchScore = 85;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public Opportunity() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public List<String> getRequiredSkills() { return requiredSkills; }
    public void setRequiredSkills(List<String> requiredSkills) { this.requiredSkills = requiredSkills; }

    public List<String> getResponsibilities() { return responsibilities; }
    public void setResponsibilities(List<String> responsibilities) { this.responsibilities = responsibilities; }

    public List<String> getQualifications() { return qualifications; }
    public void setQualifications(List<String> qualifications) { this.qualifications = qualifications; }

    public List<String> getBenefits() { return benefits; }
    public void setBenefits(List<String> benefits) { this.benefits = benefits; }

    public String getDeadline() { return deadline; }
    public void setDeadline(String deadline) { this.deadline = deadline; }

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
