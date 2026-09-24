package com.lms.Backend.opportunity.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lms.Backend.user.entity.User;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(
    name = "saved_opportunities",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_saved_user_opportunity", columnNames = {"user_id", "opportunity_id"})
    },
    indexes = {
        @Index(name = "idx_saved_user_id", columnList = "user_id"),
        @Index(name = "idx_saved_opportunity_id", columnList = "opportunity_id")
    }
)
public class SavedOpportunity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "opportunity_id", nullable = false)
    private Opportunity opportunity;

    @Column(name = "saved_at", nullable = false, updatable = false)
    private Instant savedAt;

    @PrePersist
    protected void onCreate() {
        this.savedAt = Instant.now();
    }

    public SavedOpportunity() {}

    public SavedOpportunity(User user, Opportunity opportunity) {
        this.user = user;
        this.opportunity = opportunity;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Opportunity getOpportunity() { return opportunity; }
    public void setOpportunity(Opportunity opportunity) { this.opportunity = opportunity; }

    public Instant getSavedAt() { return savedAt; }
    public void setSavedAt(Instant savedAt) { this.savedAt = savedAt; }
}
