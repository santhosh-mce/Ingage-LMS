package com.lms.Backend.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @com.fasterxml.jackson.annotation.JsonIgnore
    @Column(name = "password_hash", length = 255)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role = UserRole.STUDENT;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AuthProvider provider = AuthProvider.LOCAL;

    @Column(name = "provider_id", length = 255)
    private String providerId;

    @Column(name = "profile_image", length = 1000)
    private String profileImage;

    @Column(nullable = false)
    private boolean emailVerified;

    @Column(length = 20)
    private String phone;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "last_login")
    private Instant lastLogin;

    @Column(name = "welcome_email_sent", nullable = false)
    private boolean welcomeEmailSent = false;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "first_name", length = 50)
    private String firstName;

    @Column(name = "last_name", length = 50)
    private String lastName;

    @Column(length = 100)
    private String location;

    @Column(name = "date_of_birth", length = 50)
    private String dateOfBirth;

    @Column(length = 20)
    private String gender;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(name = "highest_qualification", length = 100)
    private String highestQualification;

    @Column(name = "target_job_role", length = 100)
    private String targetJobRole;

    @Column(name = "preferred_industry", length = 100)
    private String preferredIndustry;

    @Column(name = "experience_level", length = 50)
    private String experienceLevel;

    @Column(name = "preferred_location", length = 100)
    private String preferredLocation;

    @Column(name = "career_goal", columnDefinition = "TEXT")
    private String careerGoal;

    @Column(name = "open_to_work", nullable = false)
    private boolean openToWork = false;

    @Column(name = "linkedin_url", length = 255)
    private String linkedinUrl;

    @Column(name = "github_url", length = 255)
    private String githubUrl;

    @Column(name = "portfolio_url", length = 255)
    private String portfolioUrl;

    @Column(name = "other_website_url", length = 255)
    private String otherWebsiteUrl;

    @Column(name = "resume_url", length = 500)
    private String resumeUrl;

    @Column(name = "resume_filename", length = 255)
    private String resumeFilename;

    @Column(name = "resume_file_size")
    private Long resumeFileSize;

    @Column(name = "resume_uploaded_at")
    private Instant resumeUploadedAt;

    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public UserRole getRole() { return role; }
    public AuthProvider getProvider() { return provider; }
    public String getProviderId() { return providerId; }
    public String getProfileImage() { return profileImage; }
    public boolean isEmailVerified() { return emailVerified; }
    public String getPhone() { return phone; }
    public boolean isActive() { return active; }
    public Instant getLastLogin() { return lastLogin; }
    public boolean isWelcomeEmailSent() { return welcomeEmailSent; }
    public Instant getCreatedAt() { return createdAt; }

    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getLocation() { return location; }
    public String getDateOfBirth() { return dateOfBirth; }
    public String getGender() { return gender; }
    public String getBio() { return bio; }
    public String getHighestQualification() { return highestQualification; }
    public String getTargetJobRole() { return targetJobRole; }
    public String getPreferredIndustry() { return preferredIndustry; }
    public String getExperienceLevel() { return experienceLevel; }
    public String getPreferredLocation() { return preferredLocation; }
    public String getCareerGoal() { return careerGoal; }
    public boolean isOpenToWork() { return openToWork; }
    public String getLinkedinUrl() { return linkedinUrl; }
    public String getGithubUrl() { return githubUrl; }
    public String getPortfolioUrl() { return portfolioUrl; }
    public String getOtherWebsiteUrl() { return otherWebsiteUrl; }
    public String getResumeUrl() { return resumeUrl; }
    public String getResumeFilename() { return resumeFilename; }
    public Long getResumeFileSize() { return resumeFileSize; }
    public Instant getResumeUploadedAt() { return resumeUploadedAt; }

    public void setId(UUID id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public void setRole(UserRole role) { this.role = role; }
    public void setProvider(AuthProvider provider) { this.provider = provider; }
    public void setProviderId(String providerId) { this.providerId = providerId; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }
    public void setEmailVerified(boolean emailVerified) { this.emailVerified = emailVerified; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setActive(boolean active) { this.active = active; }
    public void setLastLogin(Instant lastLogin) { this.lastLogin = lastLogin; }
    public void setWelcomeEmailSent(boolean welcomeEmailSent) { this.welcomeEmailSent = welcomeEmailSent; }

    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setLocation(String location) { this.location = location; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public void setGender(String gender) { this.gender = gender; }
    public void setBio(String bio) { this.bio = bio; }
    public void setHighestQualification(String highestQualification) { this.highestQualification = highestQualification; }
    public void setTargetJobRole(String targetJobRole) { this.targetJobRole = targetJobRole; }
    public void setPreferredIndustry(String preferredIndustry) { this.preferredIndustry = preferredIndustry; }
    public void setExperienceLevel(String experienceLevel) { this.experienceLevel = experienceLevel; }
    public void setPreferredLocation(String preferredLocation) { this.preferredLocation = preferredLocation; }
    public void setCareerGoal(String careerGoal) { this.careerGoal = careerGoal; }
    public void setOpenToWork(boolean openToWork) { this.openToWork = openToWork; }
    public void setLinkedinUrl(String linkedinUrl) { this.linkedinUrl = linkedinUrl; }
    public void setGithubUrl(String githubUrl) { this.githubUrl = githubUrl; }
    public void setPortfolioUrl(String portfolioUrl) { this.portfolioUrl = portfolioUrl; }
    public void setOtherWebsiteUrl(String otherWebsiteUrl) { this.otherWebsiteUrl = otherWebsiteUrl; }
    public void setResumeUrl(String resumeUrl) { this.resumeUrl = resumeUrl; }
    public void setResumeFilename(String resumeFilename) { this.resumeFilename = resumeFilename; }
    public void setResumeFileSize(Long resumeFileSize) { this.resumeFileSize = resumeFileSize; }
    public void setResumeUploadedAt(Instant resumeUploadedAt) { this.resumeUploadedAt = resumeUploadedAt; }
}