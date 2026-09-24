package com.lms.Backend.notification.entity;

import com.lms.Backend.user.entity.User;
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
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(length = 50)
    private String type = "GENERAL";

    @Column(name = "is_read", nullable = false)
    private boolean read = false;

    @Column(name = "action_url", length = 500)
    private String actionUrl;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    public Notification() {}

    public Notification(User user, String title, String message, String type, String actionUrl) {
        this.user = user;
        this.title = title;
        this.message = message;
        this.type = type != null ? type : "GENERAL";
        this.actionUrl = actionUrl;
        this.read = false;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }

    public String getActionUrl() { return actionUrl; }
    public void setActionUrl(String actionUrl) { this.actionUrl = actionUrl; }

    public Instant getCreatedAt() { return createdAt; }
}
