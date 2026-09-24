package com.lms.Backend.career.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "career_opportunities")
public class CareerOpportunity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "career_id", nullable = false)
    @JsonIgnore
    private Career career;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(length = 255)
    private String company;

    @Column(length = 255)
    private String location;

    @Column(length = 50)
    private String type; // Full-time, Remote, Hybrid

    @Column(length = 100)
    private String salary;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }

    public CareerOpportunity() {}

    public CareerOpportunity(String title, String company, String location, String type, String salary, Integer displayOrder) {
        this.title = title;
        this.company = company;
        this.location = location;
        this.type = type;
        this.salary = salary;
        this.displayOrder = displayOrder != null ? displayOrder : 0;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Career getCareer() { return career; }
    public void setCareer(Career career) { this.career = career; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getSalary() { return salary; }
    public void setSalary(String salary) { this.salary = salary; }

    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
