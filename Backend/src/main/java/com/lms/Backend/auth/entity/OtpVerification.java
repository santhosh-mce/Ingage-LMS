package com.lms.Backend.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "otp_verifications", indexes = {
    @Index(name = "idx_otp_email_purpose", columnList = "email, purpose")
})
public class OtpVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 255)
    private String email;

    @Column(name = "otp_hash", nullable = false, length = 255)
    private String otpHash;

    @Column(nullable = false, length = 50)
    private String purpose = "LOGIN";

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "attempt_count", nullable = false)
    private int attemptCount = 0;

    @Column(nullable = false)
    private boolean used = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "last_resent_at")
    private Instant lastResentAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    public UUID getId() { return id; }
    public String getEmail() { return email; }
    public String getOtpHash() { return otpHash; }
    public String getPurpose() { return purpose; }
    public Instant getExpiresAt() { return expiresAt; }
    public int getAttemptCount() { return attemptCount; }
    public boolean isUsed() { return used; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getLastResentAt() { return lastResentAt; }

    public void setId(UUID id) { this.id = id; }
    public void setEmail(String email) { this.email = email; }
    public void setOtpHash(String otpHash) { this.otpHash = otpHash; }
    public void setPurpose(String purpose) { this.purpose = purpose; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }
    public void setAttemptCount(int attemptCount) { this.attemptCount = attemptCount; }
    public void setUsed(boolean used) { this.used = used; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public void setLastResentAt(Instant lastResentAt) { this.lastResentAt = lastResentAt; }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }
}
