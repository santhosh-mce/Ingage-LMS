package com.lms.Backend.career.entity;

import com.lms.Backend.learning.entity.EnrollmentStatus;
import com.lms.Backend.user.entity.User;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(
    name = "career_enrollments",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "career_id"})
    }
)
public class CareerEnrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "career_id", nullable = false)
    private Career career;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EnrollmentStatus status = EnrollmentStatus.ACTIVE;

    @Column(name = "progress_percentage", nullable = false)
    private Integer progressPercentage = 0;

    @Column(name = "enrolled_at", nullable = false, updatable = false)
    private Instant enrolledAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "last_accessed_at")
    private Instant lastAccessedAt;

    public CareerEnrollment() {}

    public CareerEnrollment(User user, Career career) {
        this.user = user;
        this.career = career;
        this.status = EnrollmentStatus.ACTIVE;
        this.progressPercentage = 0;
    }

    @PrePersist
    protected void onCreate() {
        this.enrolledAt = Instant.now();
        this.lastAccessedAt = Instant.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Career getCareer() { return career; }
    public void setCareer(Career career) { this.career = career; }

    public EnrollmentStatus getStatus() { return status; }
    public void setStatus(EnrollmentStatus status) { this.status = status; }

    public Integer getProgressPercentage() { return progressPercentage; }
    public void setProgressPercentage(Integer progressPercentage) { this.progressPercentage = progressPercentage; }

    public Instant getEnrolledAt() { return enrolledAt; }
    public void setEnrolledAt(Instant enrolledAt) { this.enrolledAt = enrolledAt; }

    public Instant getCompletedAt() { return completedAt; }
    public void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }

    public Instant getLastAccessedAt() { return lastAccessedAt; }
    public void setLastAccessedAt(Instant lastAccessedAt) { this.lastAccessedAt = lastAccessedAt; }
}
