package com.lms.Backend.course.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 1000)
    private String thumbnail;

    @Column(length = 100)
    private String category;

    @Column(length = 50)
    private String level;

    @Column(length = 50)
    private String duration;

    @Column(length = 150)
    private String instructor;

    @Column(nullable = false)
    private Integer price;

    @Column(length = 255)
    private String slug;

    @Column(name = "short_description", columnDefinition = "TEXT")
    private String shortDescription;

    @Column(length = 1000)
    private String banner;

    @Column(length = 50)
    private String language = "English";

    @Enumerated(jakarta.persistence.EnumType.STRING)
    @Column(length = 30)
    private CourseStatus status = CourseStatus.PUBLISHED;

    @Enumerated(jakarta.persistence.EnumType.STRING)
    @Column(name = "discount_type", length = 30)
    private DiscountType discountType;

    @Column(name = "discount_value")
    private Double discountValue = 0.0;

    @Column(name = "final_price")
    private Double finalPrice;

    @Column(length = 10)
    private String currency = "INR";

    @Column(nullable = false)
    private boolean published = true;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column
    private Instant updatedAt;

    public Course() {
    }

    public Course(String title, String description, String thumbnail, String category, String level, String duration, String instructor, Integer price, boolean published) {
        this.title = title;
        this.description = description;
        this.thumbnail = thumbnail;
        this.category = category;
        this.level = level;
        this.duration = duration;
        this.instructor = instructor;
        this.price = price;
        this.published = published;
        this.status = published ? CourseStatus.PUBLISHED : CourseStatus.DRAFT;
        calculateFinalPrice();
    }

    public void calculateFinalPrice() {
        if (price == null || price <= 0) {
            this.finalPrice = 0.0;
            return;
        }
        if (discountType == null || discountValue == null || discountValue <= 0) {
            this.finalPrice = (double) price;
            return;
        }
        if (discountType == DiscountType.PERCENTAGE) {
            double discounted = price - (price * (discountValue / 100.0));
            this.finalPrice = Math.max(0.0, Math.round(discounted * 100.0) / 100.0);
        } else if (discountType == DiscountType.FIXED_AMOUNT) {
            this.finalPrice = Math.max(0.0, price - discountValue);
        } else {
            this.finalPrice = (double) price;
        }
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getInstructor() {
        return instructor;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getShortDescription() { return shortDescription; }
    public void setShortDescription(String shortDescription) { this.shortDescription = shortDescription; }

    public String getBanner() { return banner; }
    public void setBanner(String banner) { this.banner = banner; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public CourseStatus getStatus() { return status; }
    public void setStatus(CourseStatus status) {
        this.status = status;
        this.published = (status == CourseStatus.PUBLISHED);
    }

    public DiscountType getDiscountType() { return discountType; }
    public void setDiscountType(DiscountType discountType) {
        this.discountType = discountType;
        calculateFinalPrice();
    }

    public Double getDiscountValue() { return discountValue; }
    public void setDiscountValue(Double discountValue) {
        this.discountValue = discountValue;
        calculateFinalPrice();
    }

    public Double getFinalPrice() {
        if (finalPrice == null) {
            calculateFinalPrice();
        }
        return finalPrice;
    }
    public void setFinalPrice(Double finalPrice) { this.finalPrice = finalPrice; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
