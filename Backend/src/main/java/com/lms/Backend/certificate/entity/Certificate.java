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
    @JoinColumn(name = "course_id", nullable = true)
    private Course course;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "career_id", nullable = true)
    private com.lms.Backend.career.entity.Career career;

    @Column(name = "certificate_type", length = 30)
    private String certificateType = "COURSE";

    @Column(name = "issued_at", nullable = false)
    private Instant issuedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CertificateStatus status = CertificateStatus.ACTIVE;

    @Column(name = "certificate_url", length = 1000)
    private String certificateUrl;

    @Column(name = "pdf_path", length = 1000)
    private String pdfPath;

    @Column(name = "completion_date")
    private Instant completionDate;

    @Column(name = "student_name", length = 255)
    private String studentName;

    @Column(name = "course_name", length = 255)
    private String courseName;

    @Column(name = "instructor_name", length = 255)
    private String instructorName;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    public Certificate() {}

    public Certificate(String certificateNumber, String verificationCode, User user, Course course) {
        this.certificateNumber = certificateNumber;
        this.verificationCode = verificationCode != null ? verificationCode : UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
        this.user = user;
        this.course = course;
        this.certificateType = "COURSE";
        this.issuedAt = Instant.now();
        this.status = CertificateStatus.ACTIVE;
        if (user != null) {
            this.studentName = (user.getName() != null && !user.getName().isBlank()) ? user.getName().trim() : user.getEmail();
        }
        if (course != null) {
            this.courseName = course.getTitle();
            this.instructorName = course.getInstructor();
        }
    }

    public Certificate(String certificateNumber, String verificationCode, User user, com.lms.Backend.career.entity.Career career) {
        this.certificateNumber = certificateNumber;
        this.verificationCode = verificationCode != null ? verificationCode : UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
        this.user = user;
        this.career = career;
        this.certificateType = "CAREER_PATH";
        this.issuedAt = Instant.now();
        this.status = CertificateStatus.ACTIVE;
        if (user != null) {
            this.studentName = (user.getName() != null && !user.getName().isBlank()) ? user.getName().trim() : user.getEmail();
        }
        if (career != null) {
            this.courseName = career.getTitle();
            this.instructorName = "InGage Mentorship Board";
        }
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        if (this.issuedAt == null) {
            this.issuedAt = Instant.now();
        }
        if (this.completionDate == null) {
            this.completionDate = this.issuedAt;
        }
        if (this.verificationCode == null || this.verificationCode.isBlank()) {
            this.verificationCode = UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
        }
        if ((this.studentName == null || this.studentName.isBlank()) && this.user != null) {
            this.studentName = (this.user.getName() != null && !this.user.getName().isBlank())
                    ? this.user.getName().trim()
                    : this.user.getEmail();
        }
        if ((this.courseName == null || this.courseName.isBlank()) && this.course != null) {
            this.courseName = this.course.getTitle();
        }
        if ((this.instructorName == null || this.instructorName.isBlank()) && this.course != null) {
            this.instructorName = this.course.getInstructor();
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

    public Instant getCompletionDate() { return completionDate != null ? completionDate : issuedAt; }
    public void setCompletionDate(Instant completionDate) { this.completionDate = completionDate; }

    public String getStudentName() {
        if (studentName != null && !studentName.isBlank()) return studentName;
        return user != null && user.getName() != null && !user.getName().isBlank() ? user.getName() : (user != null ? user.getEmail() : "Student");
    }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getCourseName() {
        if (courseName != null && !courseName.isBlank()) return courseName;
        return course != null ? course.getTitle() : "Course";
    }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public String getInstructorName() {
        if (instructorName != null && !instructorName.isBlank()) return instructorName;
        return course != null ? course.getInstructor() : null;
    }
    public void setInstructorName(String instructorName) { this.instructorName = instructorName; }

    public CertificateStatus getStatus() { return status; }
    public void setStatus(CertificateStatus status) { this.status = status; }

    public String getCertificateUrl() { return certificateUrl; }
    public void setCertificateUrl(String certificateUrl) { this.certificateUrl = certificateUrl; }

    public String getPdfPath() { return pdfPath; }
    public void setPdfPath(String pdfPath) { this.pdfPath = pdfPath; }
    public com.lms.Backend.career.entity.Career getCareer() { return career; }
    public void setCareer(com.lms.Backend.career.entity.Career career) { this.career = career; }

    public String getCertificateType() { return certificateType != null ? certificateType : "COURSE"; }
    public void setCertificateType(String certificateType) { this.certificateType = certificateType; }

    public Instant getCreatedAt() { return createdAt; }
}
