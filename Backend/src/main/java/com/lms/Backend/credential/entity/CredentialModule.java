package com.lms.Backend.credential.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "credential_modules")
public class CredentialModule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 50)
    private String duration;

    @Column(name = "order_index", nullable = false)
    private Integer orderIndex = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "credential_course_id", nullable = false)
    @JsonIgnore
    private CredentialCourse course;

    public CredentialModule() {}

    public CredentialModule(String title, String description, String duration, Integer orderIndex, CredentialCourse course) {
        this.title = title;
        this.description = description;
        this.duration = duration;
        this.orderIndex = orderIndex != null ? orderIndex : 0;
        this.course = course;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public Integer getOrderIndex() { return orderIndex; }
    public void setOrderIndex(Integer orderIndex) { this.orderIndex = orderIndex; }

    public CredentialCourse getCourse() { return course; }
    public void setCourse(CredentialCourse course) { this.course = course; }
}
