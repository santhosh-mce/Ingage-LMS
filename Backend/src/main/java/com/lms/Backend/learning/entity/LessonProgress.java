package com.lms.Backend.learning.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lms.Backend.course.entity.CourseLesson;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.Instant;

@Entity
@Table(
    name = "lesson_progress",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"enrollment_id", "lesson_id"})
    }
)
public class LessonProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enrollment_id", nullable = false)
    @JsonIgnore
    private Enrollment enrollment;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "lesson_id", nullable = false)
    private CourseLesson lesson;

    @Column(nullable = false)
    private boolean completed = false;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "last_accessed_at")
    private Instant lastAccessedAt;

    @Column(name = "watch_duration_seconds")
    private Integer watchDurationSeconds = 0;

    public LessonProgress() {}

    public LessonProgress(Enrollment enrollment, CourseLesson lesson, boolean completed) {
        this.enrollment = enrollment;
        this.lesson = lesson;
        this.completed = completed;
        if (completed) {
            this.completedAt = Instant.now();
        }
        this.lastAccessedAt = Instant.now();
    }

    @PrePersist
    protected void onCreate() {
        this.lastAccessedAt = Instant.now();
        if (completed && completedAt == null) {
            this.completedAt = Instant.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.lastAccessedAt = Instant.now();
        if (completed && completedAt == null) {
            this.completedAt = Instant.now();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Enrollment getEnrollment() { return enrollment; }
    public void setEnrollment(Enrollment enrollment) { this.enrollment = enrollment; }

    public CourseLesson getLesson() { return lesson; }
    public void setLesson(CourseLesson lesson) { this.lesson = lesson; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) {
        this.completed = completed;
        if (completed && this.completedAt == null) {
            this.completedAt = Instant.now();
        }
    }

    public Instant getCompletedAt() { return completedAt; }
    public void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }

    public Instant getLastAccessedAt() { return lastAccessedAt; }
    public void setLastAccessedAt(Instant lastAccessedAt) { this.lastAccessedAt = lastAccessedAt; }

    public Integer getWatchDurationSeconds() { return watchDurationSeconds; }
    public void setWatchDurationSeconds(Integer watchDurationSeconds) { this.watchDurationSeconds = watchDurationSeconds; }
}
