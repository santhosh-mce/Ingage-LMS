package com.lms.Backend.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(
    name = "user_education",
    indexes = {
        @Index(name = "idx_user_edu_user_id", columnList = "user_id")
    }
)
public class UserEducation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @Column(length = 100)
    private String qualification;

    @Column(nullable = false, length = 150)
    private String degree;

    @Column(nullable = false, length = 200)
    private String institution;

    @Column(length = 150)
    private String department;

    @Column(name = "graduation_year", length = 10)
    private String graduationYear;

    @Column(length = 50)
    private String cgpa;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        this.createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getQualification() { return qualification; }
    public void setQualification(String qualification) { this.qualification = qualification; }

    public String getDegree() { return degree; }
    public void setDegree(String degree) { this.degree = degree; }

    public String getInstitution() { return institution; }
    public void setInstitution(String institution) { this.institution = institution; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getGraduationYear() { return graduationYear; }
    public void setGraduationYear(String graduationYear) { this.graduationYear = graduationYear; }

    public String getCgpa() { return cgpa; }
    public void setCgpa(String cgpa) { this.cgpa = cgpa; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
