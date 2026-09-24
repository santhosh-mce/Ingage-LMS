package com.lms.Backend.credential.entity;

import com.lms.Backend.user.entity.User;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(
    name = "credential_enrollments",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "credential_course_id"})
    }
)
public class CredentialEnrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "credential_course_id", nullable = false)
    private CredentialCourse course;

    @Column(nullable = false, length = 50)
    private String status = "IN_PROGRESS"; // IN_PROGRESS, COMPLETED, CREDENTIAL_EARNED, CREDENTIAL_PENDING

    @Column(name = "progress_percentage", nullable = false)
    private Integer progressPercentage = 0;

    @Column(name = "completed_modules_count", nullable = false)
    private Integer completedModulesCount = 0;

    @Column(name = "credential_id", length = 100)
    private String credentialId;

    @Column(name = "credential_url", length = 1000)
    private String credentialUrl;

    @Column(name = "enrolled_at", nullable = false, updatable = false)
    private Instant enrolledAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "last_accessed_at")
    private Instant lastAccessedAt;

    @PrePersist
    protected void onCreate() {
        this.enrolledAt = Instant.now();
        this.lastAccessedAt = Instant.now();
    }

    public CredentialEnrollment() {}

    public CredentialEnrollment(User user, CredentialCourse course) {
        this.user = user;
        this.course = course;
        this.status = "IN_PROGRESS";
        this.progressPercentage = 0;
        this.completedModulesCount = 0;
        this.enrolledAt = Instant.now();
        this.lastAccessedAt = Instant.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public CredentialCourse getCourse() { return course; }
    public void setCourse(CredentialCourse course) { this.course = course; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getProgressPercentage() { return progressPercentage; }
    public void setProgressPercentage(Integer progressPercentage) { this.progressPercentage = progressPercentage; }

    public Integer getCompletedModulesCount() { return completedModulesCount; }
    public void setCompletedModulesCount(Integer completedModulesCount) { this.completedModulesCount = completedModulesCount; }

    public String getCredentialId() { return credentialId; }
    public void setCredentialId(String credentialId) { this.credentialId = credentialId; }

    public String getCredentialUrl() { return credentialUrl; }
    public void setCredentialUrl(String credentialUrl) { this.credentialUrl = credentialUrl; }

    public Instant getEnrolledAt() { return enrolledAt; }
    public void setEnrolledAt(Instant enrolledAt) { this.enrolledAt = enrolledAt; }

    public Instant getCompletedAt() { return completedAt; }
    public void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }

    public Instant getLastAccessedAt() { return lastAccessedAt; }
    public void setLastAccessedAt(Instant lastAccessedAt) { this.lastAccessedAt = lastAccessedAt; }
}
