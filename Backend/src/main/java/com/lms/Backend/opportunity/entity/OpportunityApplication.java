package com.lms.Backend.opportunity.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lms.Backend.user.entity.User;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(
    name = "opportunity_applications",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_opportunity", columnNames = {"user_id", "opportunity_id"})
    },
    indexes = {
        @Index(name = "idx_app_user_id", columnList = "user_id"),
        @Index(name = "idx_app_opportunity_id", columnList = "opportunity_id")
    }
)
public class OpportunityApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "opportunity_id", nullable = false)
    private Opportunity opportunity;

    @Column(nullable = false, length = 50)
    private String status = "Under Review";

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "applied_at", nullable = false, updatable = false)
    private Instant appliedAt;

    @PrePersist
    protected void onCreate() {
        this.appliedAt = Instant.now();
        if (this.status == null) this.status = "Under Review";
    }

    public OpportunityApplication() {}

    public OpportunityApplication(User user, Opportunity opportunity, String notes) {
        this.user = user;
        this.opportunity = opportunity;
        this.notes = notes;
        this.status = "Under Review";
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Opportunity getOpportunity() { return opportunity; }
    public void setOpportunity(Opportunity opportunity) { this.opportunity = opportunity; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Instant getAppliedAt() { return appliedAt; }
    public void setAppliedAt(Instant appliedAt) { this.appliedAt = appliedAt; }
}
