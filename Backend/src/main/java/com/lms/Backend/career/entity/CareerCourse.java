package com.lms.Backend.career.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lms.Backend.course.entity.Course;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "career_courses")
public class CareerCourse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "career_id", nullable = false)
    @JsonIgnore
    private Career career;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 0;

    @Column(name = "included", columnDefinition = "boolean default true")
    private Boolean included = true;

    @Column(name = "required_for_completion", columnDefinition = "boolean default true")
    private Boolean requiredForCompletion = true;

    public CareerCourse() {}

    public CareerCourse(Career career, Course course, Integer displayOrder) {
        this.career = career;
        this.course = course;
        this.displayOrder = displayOrder != null ? displayOrder : 0;
        this.included = true;
        this.requiredForCompletion = true;
    }

    public CareerCourse(Career career, Course course, Integer displayOrder, Boolean included, Boolean requiredForCompletion) {
        this.career = career;
        this.course = course;
        this.displayOrder = displayOrder != null ? displayOrder : 0;
        this.included = included != null ? included : true;
        this.requiredForCompletion = requiredForCompletion != null ? requiredForCompletion : true;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Career getCareer() { return career; }
    public void setCareer(Career career) { this.career = career; }

    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }

    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }

    public boolean isIncluded() { return included != null && included; }
    public Boolean getIncluded() { return included != null ? included : true; }
    public void setIncluded(Boolean included) { this.included = included != null ? included : true; }

    public boolean isRequiredForCompletion() { return requiredForCompletion != null && requiredForCompletion; }
    public Boolean getRequiredForCompletion() { return requiredForCompletion != null ? requiredForCompletion : true; }
    public void setRequiredForCompletion(Boolean requiredForCompletion) {
        this.requiredForCompletion = requiredForCompletion != null ? requiredForCompletion : true;
    }
}
