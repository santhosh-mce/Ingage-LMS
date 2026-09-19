package com.lms.Backend.certificate.entity;

import com.lms.Backend.course.entity.Course;
import com.lms.Backend.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
    name = "certificates",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"certificate_number"}),
        @UniqueConstraint(columnNames = {"verification_code"}),
        @UniqueConstraint(columnNames = {"user_id", "course_id"})
    }
)
public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "certificate_number", nullable = false, length = 100)
    private String certificateNumber;

    @Column(name = "verification_code", nullable = false, length = 100)
    private String verificationCode;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "issued_at", nullable = false)
    private Instant issuedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CertificateStatus status = CertificateStatus.ACTIVE;

    @Column(name = "certificate_url", length = 1000)
    private String certificateUrl;

    @Column(name = "pdf_path", length = 1000)
    private String pdfPath;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    public Certificate() {}

    public Certificate(String certificateNumber, String verificationCode, User user, Course course) {
        this.certificateNumber = certificateNumber;
        this.verificationCode = verificationCode != null ? verificationCode : UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
        this.user = user;
        this.course = course;
        this.issuedAt = Instant.now();
        this.status = CertificateStatus.ACTIVE;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        if (this.issuedAt == null) {
            this.issuedAt = Instant.now();
        }
        if (this.verificationCode == null || this.verificationCode.isBlank()) {
            this.verificationCode = UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCertificateNumber() { return certificateNumber; }
    public void setCertificateNumber(String certificateNumber) { this.certificateNumber = certificateNumber; }

    public String getVerificationCode() { return verificationCode; }
    public void setVerificationCode(String verificationCode) { this.verificationCode = verificationCode; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }

    public Instant getIssuedAt() { return issuedAt; }
    public void setIssuedAt(Instant issuedAt) { this.issuedAt = issuedAt; }

    public CertificateStatus getStatus() { return status; }
    public void setStatus(CertificateStatus status) { this.status = status; }

    public String getCertificateUrl() { return certificateUrl; }
    public void setCertificateUrl(String certificateUrl) { this.certificateUrl = certificateUrl; }

    public String getPdfPath() { return pdfPath; }
    public void setPdfPath(String pdfPath) { this.pdfPath = pdfPath; }

    public Instant getCreatedAt() { return createdAt; }
}
