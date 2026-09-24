package com.lms.Backend.user.service;

import com.lms.Backend.admin.service.FileUploadService;
import com.lms.Backend.career.entity.Career;
import com.lms.Backend.career.entity.CareerCourse;
import com.lms.Backend.career.entity.CareerProject;
import com.lms.Backend.career.entity.CareerSkill;
import com.lms.Backend.career.repository.CareerRepository;
import com.lms.Backend.credential.dto.CredentialCourseDto;
import com.lms.Backend.credential.dto.UserCredentialDto;
import com.lms.Backend.credential.service.CredentialCourseService;
import com.lms.Backend.learning.entity.EnrollmentStatus;
import com.lms.Backend.learning.repository.EnrollmentRepository;
import com.lms.Backend.user.dto.*;
import com.lms.Backend.user.entity.User;
import com.lms.Backend.user.entity.UserEducation;
import com.lms.Backend.user.entity.UserProject;
import com.lms.Backend.user.entity.UserSkill;
import com.lms.Backend.user.repository.UserEducationRepository;
import com.lms.Backend.user.repository.UserProjectRepository;
import com.lms.Backend.user.repository.UserRepository;
import com.lms.Backend.user.repository.UserSkillRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProfileService {

    private static final Logger log = LoggerFactory.getLogger(ProfileService.class);
    private static final long MAX_RESUME_SIZE = 5 * 1024 * 1024L; // 5 MB

    private final UserRepository userRepository;
    private final UserEducationRepository educationRepository;
    private final UserSkillRepository skillRepository;
    private final UserProjectRepository projectRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CareerRepository careerRepository;
    private final CredentialCourseService credentialCourseService;
    private final FileUploadService fileUploadService;

    public ProfileService(
        UserRepository userRepository,
        UserEducationRepository educationRepository,
        UserSkillRepository skillRepository,
        UserProjectRepository projectRepository,
        EnrollmentRepository enrollmentRepository,
        CareerRepository careerRepository,
        CredentialCourseService credentialCourseService,
        FileUploadService fileUploadService
    ) {
        this.userRepository = userRepository;
        this.educationRepository = educationRepository;
        this.skillRepository = skillRepository;
        this.projectRepository = projectRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.careerRepository = careerRepository;
        this.credentialCourseService = credentialCourseService;
        this.fileUploadService = fileUploadService;
    }

    @Transactional(readOnly = true)
    public ProfileDto getProfile(User user) {
        ProfileDto profile = new ProfileDto();

        // 1. Personal Info
        PersonalInfoDto personal = new PersonalInfoDto();
        personal.setFirstName(user.getFirstName());
        personal.setLastName(user.getLastName());
        personal.setFullName(user.getName());
        personal.setEmail(user.getEmail());
        personal.setPhone(user.getPhone());
        String img = user.getProfileImage();
        if (img != null && img.startsWith("/uploads/")) {
            img = "/api" + img;
        }
        personal.setProfileImage(img);
        personal.setLocation(user.getLocation());
        personal.setDateOfBirth(user.getDateOfBirth());
        personal.setGender(user.getGender());
        personal.setBio(user.getBio());
        personal.setEmailVerified(user.isEmailVerified());
        profile.setPersonalInfo(personal);

        // 2. Career Goal
        CareerGoalDto careerGoal = new CareerGoalDto();
        careerGoal.setTargetJobRole(user.getTargetJobRole());
        careerGoal.setPreferredIndustry(user.getPreferredIndustry());
        careerGoal.setExperienceLevel(user.getExperienceLevel());
        careerGoal.setPreferredLocation(user.getPreferredLocation());
        careerGoal.setCareerGoal(user.getCareerGoal());
        careerGoal.setOpenToWork(user.isOpenToWork());
        profile.setCareerGoal(careerGoal);

        // 3. Links
        ProfessionalLinksDto links = new ProfessionalLinksDto();
        links.setLinkedinUrl(user.getLinkedinUrl());
        links.setGithubUrl(user.getGithubUrl());
        links.setPortfolioUrl(user.getPortfolioUrl());
        links.setOtherWebsiteUrl(user.getOtherWebsiteUrl());
        profile.setLinks(links);

        // 4. Resume
        String resumeUrl = user.getResumeUrl();
        if (resumeUrl != null && resumeUrl.startsWith("/uploads/")) {
            resumeUrl = "/api" + resumeUrl;
        }
        ResumeDto resume = new ResumeDto(
            user.getResumeFilename(),
            resumeUrl,
            user.getResumeFileSize(),
            user.getResumeUploadedAt()
        );
        profile.setResume(resume);

        // 5. Education
        List<UserEducation> edus = educationRepository.findByUserIdOrderByGraduationYearDesc(user.getId());
        List<UserEducationDto> eduDtos = edus.stream()
            .map(e -> new UserEducationDto(e.getId(), e.getQualification(), e.getDegree(), e.getInstitution(), e.getDepartment(), e.getGraduationYear(), e.getCgpa()))
            .collect(Collectors.toList());
        profile.setEducation(eduDtos);

        // 6. Skills
        List<UserSkill> skills = skillRepository.findByUserIdOrderByNameAsc(user.getId());
        List<UserSkillDto> skillDtos = skills.stream()
            .map(s -> new UserSkillDto(s.getId(), s.getName(), s.getCategory(), s.getLevel()))
            .collect(Collectors.toList());
        profile.setSkills(skillDtos);

        // 7. Projects
        List<UserProject> projs = projectRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
        List<UserProjectDto> projDtos = projs.stream()
            .map(p -> new UserProjectDto(p.getId(), p.getTitle(), p.getDescription(), p.getCategory(), p.getTechnologies(), p.getGithubUrl(), p.getLiveUrl(), p.getImageUrl(), p.getStatus()))
            .collect(Collectors.toList());
        profile.setProjects(projDtos);

        // 8. Career Compass Readiness
        CareerCompassReadinessDto compass = calculateCareerCompass(user, skills);
        profile.setCareerCompass(compass);

        // 9. Dashboard Summary
        int enrolledCount = (int) enrollmentRepository.countByUserId(user.getId());
        int completedCoursesCount = (int) enrollmentRepository.countByUserIdAndStatus(user.getId(), EnrollmentStatus.COMPLETED);
        int completedProjectsCount = (int) projectRepository.countByUserIdAndStatusIgnoreCase(user.getId(), "Completed");
        if (completedProjectsCount == 0 && !projs.isEmpty()) {
            completedProjectsCount = projs.size();
        }
        int credentialsCount = 0;
        try {
            List<UserCredentialDto> credentials = credentialCourseService.getUserCredentials(user);
            if (credentials != null) {
                credentialsCount = credentials.size();
            }
        } catch (Exception e) {
            log.debug("Error fetching credentials count for user {}: {}", user.getEmail(), e.getMessage());
        }

        DashboardSummaryDto summary = new DashboardSummaryDto(
            enrolledCount,
            completedCoursesCount,
            completedProjectsCount,
            credentialsCount,
            compass != null ? compass.getReadinessPercentage() : 0
        );
        profile.setSummary(summary);

        // 10. Profile Completion Percentage
        calculateProfileCompletion(profile, user, edus, skills, projs);

        return profile;
    }

    private void calculateProfileCompletion(
        ProfileDto profile,
        User user,
        List<UserEducation> edus,
        List<UserSkill> skills,
        List<UserProject> projs
    ) {
        int score = 0;
        List<String> completed = new ArrayList<>();
        List<String> missing = new ArrayList<>();

        // Personal Info (20%)
        boolean hasPersonal = (user.getName() != null && !user.getName().isBlank())
            && ((user.getLocation() != null && !user.getLocation().isBlank()) || (user.getPhone() != null && !user.getPhone().isBlank()));
        if (hasPersonal) {
            score += 20;
            completed.add("Personal Information");
        } else {
            missing.add("Personal Information");
        }

        // Education (20%)
        if (!edus.isEmpty()) {
            score += 20;
            completed.add("Education");
        } else {
            missing.add("Education");
        }

        // Career Goal (15%)
        if (user.getTargetJobRole() != null && !user.getTargetJobRole().isBlank()) {
            score += 15;
            completed.add("Career Goal");
        } else {
            missing.add("Career Goal");
        }

        // Skills (15%)
        if (skills.size() >= 3) {
            score += 15;
            completed.add("Skills (3+ added)");
        } else if (!skills.isEmpty()) {
            score += 8;
            missing.add("Add at least 3 skills");
        } else {
            missing.add("Skills");
        }

        // Projects (10%)
        if (!projs.isEmpty()) {
            score += 10;
            completed.add("Projects");
        } else {
            missing.add("Projects");
        }

        // Resume (10%)
        if (user.getResumeUrl() != null && !user.getResumeUrl().isBlank()) {
            score += 10;
            completed.add("Resume");
        } else {
            missing.add("Resume");
        }

        // Professional Links (10%)
        boolean hasLinks = (user.getLinkedinUrl() != null && !user.getLinkedinUrl().isBlank())
            || (user.getGithubUrl() != null && !user.getGithubUrl().isBlank())
            || (user.getPortfolioUrl() != null && !user.getPortfolioUrl().isBlank());
        if (hasLinks) {
            score += 10;
            completed.add("Professional Links");
        } else {
            missing.add("Professional Links");
        }

        profile.setProfileCompletionPercentage(Math.min(100, score));
        profile.setCompletedSections(completed);
        profile.setMissingSections(missing);
    }

    public CareerCompassReadinessDto calculateCareerCompass(User user, List<UserSkill> userSkills) {
        CareerCompassReadinessDto dto = new CareerCompassReadinessDto();
        String targetRole = user.getTargetJobRole();
        if (targetRole == null || targetRole.isBlank()) {
            dto.setTargetJobRole(null);
            dto.setReadinessPercentage(0);
            return dto;
        }

        dto.setTargetJobRole(targetRole);

        // Find career by title or slug
        Optional<Career> careerOpt = careerRepository.findFirstByTitleIgnoreCase(targetRole.trim());
        if (careerOpt.isEmpty()) {
            careerOpt = careerRepository.findBySlug(targetRole.trim().toLowerCase().replace(" ", "-"));
        }
        if (careerOpt.isEmpty()) {
            careerOpt = careerRepository.findFirstByTitleContainingIgnoreCase(targetRole.trim());
        }

        if (careerOpt.isPresent()) {
            Career career = careerOpt.get();
            dto.setCareerTitle(career.getTitle());
            dto.setCareerSlug(career.getSlug());
            dto.setCareerCategory(career.getCategory());
            dto.setCareerLevel(career.getLevel());

            Set<String> studentSkillSet = userSkills.stream()
                .map(s -> s.getName().trim().toLowerCase())
                .collect(Collectors.toSet());

            List<String> requiredSkillNames = new ArrayList<>();
            List<String> matched = new ArrayList<>();
            List<String> missing = new ArrayList<>();

            List<CareerSkill> roleSkills = career.getSkills();
            if (roleSkills != null && !roleSkills.isEmpty()) {
                for (CareerSkill cs : roleSkills) {
                    String sName = cs.getSkillName();
                    requiredSkillNames.add(sName);
                    if (studentSkillSet.contains(sName.trim().toLowerCase())) {
                        matched.add(sName);
                    } else {
                        missing.add(sName);
                    }
                }
            }

            dto.setAllRequiredSkills(requiredSkillNames);
            dto.setMatchedSkills(matched);
            dto.setMissingSkills(missing);

            int total = requiredSkillNames.size();
            int readiness = (total > 0) ? (int) Math.round(((double) matched.size() / total) * 100) : 0;
            dto.setReadinessPercentage(readiness);

            // Recommended Courses from Career
            List<Map<String, Object>> recCourses = new ArrayList<>();
            if (career.getCareerCourses() != null) {
                for (CareerCourse cc : career.getCareerCourses()) {
                    if (cc.getCourse() != null) {
                        Map<String, Object> cMap = new LinkedHashMap<>();
                        cMap.put("id", cc.getCourse().getId());
                        cMap.put("title", cc.getCourse().getTitle());
                        cMap.put("slug", cc.getCourse().getSlug());
                        cMap.put("thumbnail", cc.getCourse().getThumbnail());
                        cMap.put("category", cc.getCourse().getCategory());
                        cMap.put("level", cc.getCourse().getLevel());
                        recCourses.add(cMap);
                        if (recCourses.size() >= 4) break;
                    }
                }
            }
            dto.setRecommendedCourses(recCourses);

            // Recommended Projects from Career
            List<Map<String, Object>> recProjects = new ArrayList<>();
            if (career.getProjects() != null) {
                for (CareerProject cp : career.getProjects()) {
                    Map<String, Object> pMap = new LinkedHashMap<>();
                    pMap.put("id", cp.getId());
                    pMap.put("title", cp.getTitle());
                    pMap.put("description", cp.getDescription());
                    pMap.put("difficulty", cp.getDifficulty());
                    pMap.put("technologies", cp.getTechnologies());
                    recProjects.add(pMap);
                    if (recProjects.size() >= 4) break;
                }
            }
            dto.setRecommendedProjects(recProjects);

            // Recommended Credentials for this career
            try {
                List<CredentialCourseDto> credCourses = credentialCourseService.getCoursesByCareerSlug(career.getSlug(), user);
                if (credCourses != null) {
                    List<Map<String, Object>> recCredentials = new ArrayList<>();
                    for (CredentialCourseDto cd : credCourses) {
                        Map<String, Object> crMap = new LinkedHashMap<>();
                        crMap.put("id", cd.getId());
                        crMap.put("title", cd.getTitle());
                        crMap.put("slug", cd.getSlug());
                        crMap.put("credentialName", cd.getCredentialName());
                        crMap.put("provider", cd.getProvider());
                        crMap.put("thumbnail", cd.getThumbnail());
                        recCredentials.add(crMap);
                        if (recCredentials.size() >= 4) break;
                    }
                    dto.setRecommendedCredentials(recCredentials);
                }
            } catch (Exception e) {
                log.debug("No credentials found for career {}: {}", career.getSlug(), e.getMessage());
            }

        } else {
            dto.setCareerTitle(targetRole);
            dto.setCareerSlug(targetRole.toLowerCase().replace(" ", "-"));
            dto.setReadinessPercentage(0);
        }

        return dto;
    }

    @Transactional
    public ProfileDto updatePersonalInfo(User user, PersonalInfoDto dto) {
        if (dto.getFirstName() != null) user.setFirstName(dto.getFirstName().trim());
        if (dto.getLastName() != null) user.setLastName(dto.getLastName().trim());

        if (dto.getFullName() != null && !dto.getFullName().isBlank()) {
            user.setName(dto.getFullName().trim());
        } else if (user.getFirstName() != null && !user.getFirstName().isBlank()) {
            String combined = (user.getFirstName() + " " + (user.getLastName() != null ? user.getLastName() : "")).trim();
            user.setName(combined);
        }

        if (dto.getPhone() != null) user.setPhone(dto.getPhone().trim());
        if (dto.getLocation() != null) user.setLocation(dto.getLocation().trim());
        if (dto.getDateOfBirth() != null) user.setDateOfBirth(dto.getDateOfBirth().trim());
        if (dto.getGender() != null) user.setGender(dto.getGender().trim());
        if (dto.getBio() != null) user.setBio(dto.getBio().trim());

        user = userRepository.save(user);
        return getProfile(user);
    }

    @Transactional
    public ProfileDto updateCareerGoal(User user, CareerGoalDto dto) {
        if (dto.getTargetJobRole() != null) user.setTargetJobRole(dto.getTargetJobRole().trim());
        if (dto.getPreferredIndustry() != null) user.setPreferredIndustry(dto.getPreferredIndustry().trim());
        if (dto.getExperienceLevel() != null) user.setExperienceLevel(dto.getExperienceLevel().trim());
        if (dto.getPreferredLocation() != null) user.setPreferredLocation(dto.getPreferredLocation().trim());
        if (dto.getCareerGoal() != null) user.setCareerGoal(dto.getCareerGoal().trim());
        user.setOpenToWork(dto.isOpenToWork());

        user = userRepository.save(user);
        return getProfile(user);
    }

    @Transactional
    public ProfileDto updateLinks(User user, ProfessionalLinksDto dto) {
        if (dto.getLinkedinUrl() != null) user.setLinkedinUrl(dto.getLinkedinUrl().trim());
        if (dto.getGithubUrl() != null) user.setGithubUrl(dto.getGithubUrl().trim());
        if (dto.getPortfolioUrl() != null) user.setPortfolioUrl(dto.getPortfolioUrl().trim());
        if (dto.getOtherWebsiteUrl() != null) user.setOtherWebsiteUrl(dto.getOtherWebsiteUrl().trim());

        user = userRepository.save(user);
        return getProfile(user);
    }

    @Transactional
    public ProfileDto updateFullProfile(User user, FullProfileUpdateRequest request) {
        if (request.getPersonalInfo() != null) {
            updatePersonalInfo(user, request.getPersonalInfo());
        }
        if (request.getCareerGoal() != null) {
            updateCareerGoal(user, request.getCareerGoal());
        }
        if (request.getLinks() != null) {
            updateLinks(user, request.getLinks());
        }
        if (request.getEducation() != null) {
            educationRepository.deleteByUserId(user.getId());
            for (UserEducationDto dto : request.getEducation()) {
                if (dto.getDegree() != null && !dto.getDegree().isBlank() &&
                    dto.getInstitution() != null && !dto.getInstitution().isBlank()) {
                    UserEducation edu = new UserEducation();
                    edu.setUser(user);
                    edu.setQualification(dto.getQualification());
                    edu.setDegree(dto.getDegree().trim());
                    edu.setInstitution(dto.getInstitution().trim());
                    edu.setDepartment(dto.getDepartment());
                    edu.setGraduationYear(dto.getGraduationYear());
                    edu.setCgpa(dto.getCgpa());
                    educationRepository.save(edu);
                }
            }
        }
        if (request.getSkills() != null) {
            skillRepository.deleteByUserId(user.getId());
            for (UserSkillDto dto : request.getSkills()) {
                if (dto.getName() != null && !dto.getName().isBlank()) {
                    UserSkill skill = new UserSkill();
                    skill.setUser(user);
                    skill.setName(dto.getName().trim());
                    skill.setCategory(dto.getCategory() != null ? dto.getCategory() : "TECHNOLOGIES");
                    skill.setLevel(dto.getLevel() != null ? dto.getLevel() : "INTERMEDIATE");
                    skillRepository.save(skill);
                }
            }
        }
        return getProfile(user);
    }

    // Education
    @Transactional
    public UserEducationDto addEducation(User user, UserEducationDto dto) {
        if (dto.getDegree() == null || dto.getDegree().isBlank()) {
            throw new IllegalArgumentException("Degree is required.");
        }
        if (dto.getInstitution() == null || dto.getInstitution().isBlank()) {
            throw new IllegalArgumentException("Institution is required.");
        }

        UserEducation edu = new UserEducation();
        edu.setUser(user);
        edu.setQualification(dto.getQualification());
        edu.setDegree(dto.getDegree().trim());
        edu.setInstitution(dto.getInstitution().trim());
        edu.setDepartment(dto.getDepartment());
        edu.setGraduationYear(dto.getGraduationYear());
        edu.setCgpa(dto.getCgpa());

        edu = educationRepository.save(edu);

        // Update highest qualification on User if empty or newer
        if (user.getHighestQualification() == null || user.getHighestQualification().isBlank()) {
            user.setHighestQualification(edu.getDegree());
            userRepository.save(user);
        }

        return new UserEducationDto(edu.getId(), edu.getQualification(), edu.getDegree(), edu.getInstitution(), edu.getDepartment(), edu.getGraduationYear(), edu.getCgpa());
    }

    @Transactional
    public UserEducationDto updateEducation(User user, Long id, UserEducationDto dto) {
        UserEducation edu = educationRepository.findByIdAndUserId(id, user.getId())
            .orElseThrow(() -> new IllegalArgumentException("Education record not found."));

        if (dto.getDegree() != null && !dto.getDegree().isBlank()) edu.setDegree(dto.getDegree().trim());
        if (dto.getInstitution() != null && !dto.getInstitution().isBlank()) edu.setInstitution(dto.getInstitution().trim());
        if (dto.getQualification() != null) edu.setQualification(dto.getQualification().trim());
        if (dto.getDepartment() != null) edu.setDepartment(dto.getDepartment().trim());
        if (dto.getGraduationYear() != null) edu.setGraduationYear(dto.getGraduationYear().trim());
        if (dto.getCgpa() != null) edu.setCgpa(dto.getCgpa().trim());

        edu = educationRepository.save(edu);
        return new UserEducationDto(edu.getId(), edu.getQualification(), edu.getDegree(), edu.getInstitution(), edu.getDepartment(), edu.getGraduationYear(), edu.getCgpa());
    }

    @Transactional
    public void deleteEducation(User user, Long id) {
        UserEducation edu = educationRepository.findByIdAndUserId(id, user.getId())
            .orElseThrow(() -> new IllegalArgumentException("Education record not found."));
        educationRepository.delete(edu);
    }

    // Skills
    @Transactional
    public UserSkillDto addSkill(User user, UserSkillDto dto) {
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new IllegalArgumentException("Skill name is required.");
        }
        String trimmedName = dto.getName().trim();

        if (skillRepository.existsByUserIdAndNameIgnoreCase(user.getId(), trimmedName)) {
            throw new IllegalArgumentException("Skill already exists in your profile.");
        }

        UserSkill skill = new UserSkill();
        skill.setUser(user);
        skill.setName(trimmedName);
        skill.setCategory(dto.getCategory() != null && !dto.getCategory().isBlank() ? dto.getCategory().trim() : "PROGRAMMING_LANGUAGES");
        skill.setLevel(dto.getLevel() != null && !dto.getLevel().isBlank() ? dto.getLevel().trim() : "INTERMEDIATE");

        skill = skillRepository.save(skill);
        return new UserSkillDto(skill.getId(), skill.getName(), skill.getCategory(), skill.getLevel());
    }

    @Transactional
    public void deleteSkill(User user, Long id) {
        UserSkill skill = skillRepository.findByIdAndUserId(id, user.getId())
            .orElseThrow(() -> new IllegalArgumentException("Skill record not found."));
        skillRepository.delete(skill);
    }

    // Projects
    @Transactional
    public UserProjectDto addProject(User user, UserProjectDto dto) {
        if (dto.getTitle() == null || dto.getTitle().isBlank()) {
            throw new IllegalArgumentException("Project title is required.");
        }

        UserProject p = new UserProject();
        p.setUser(user);
        p.setTitle(dto.getTitle().trim());
        p.setDescription(dto.getDescription());
        p.setCategory(dto.getCategory());
        p.setTechnologies(dto.getTechnologies());
        p.setGithubUrl(dto.getGithubUrl());
        p.setLiveUrl(dto.getLiveUrl());
        p.setImageUrl(dto.getImageUrl());
        p.setStatus(dto.getStatus() != null && !dto.getStatus().isBlank() ? dto.getStatus() : "Completed");

        p = projectRepository.save(p);
        return new UserProjectDto(p.getId(), p.getTitle(), p.getDescription(), p.getCategory(), p.getTechnologies(), p.getGithubUrl(), p.getLiveUrl(), p.getImageUrl(), p.getStatus());
    }

    @Transactional
    public UserProjectDto updateProject(User user, Long id, UserProjectDto dto) {
        UserProject p = projectRepository.findByIdAndUserId(id, user.getId())
            .orElseThrow(() -> new IllegalArgumentException("Project record not found."));

        if (dto.getTitle() != null && !dto.getTitle().isBlank()) p.setTitle(dto.getTitle().trim());
        if (dto.getDescription() != null) p.setDescription(dto.getDescription());
        if (dto.getCategory() != null) p.setCategory(dto.getCategory());
        if (dto.getTechnologies() != null) p.setTechnologies(dto.getTechnologies());
        if (dto.getGithubUrl() != null) p.setGithubUrl(dto.getGithubUrl());
        if (dto.getLiveUrl() != null) p.setLiveUrl(dto.getLiveUrl());
        if (dto.getImageUrl() != null) p.setImageUrl(dto.getImageUrl());
        if (dto.getStatus() != null) p.setStatus(dto.getStatus());

        p = projectRepository.save(p);
        return new UserProjectDto(p.getId(), p.getTitle(), p.getDescription(), p.getCategory(), p.getTechnologies(), p.getGithubUrl(), p.getLiveUrl(), p.getImageUrl(), p.getStatus());
    }

    @Transactional
    public void deleteProject(User user, Long id) {
        UserProject p = projectRepository.findByIdAndUserId(id, user.getId())
            .orElseThrow(() -> new IllegalArgumentException("Project record not found."));
        projectRepository.delete(p);
    }

    // Resume
    @Transactional
    public ResumeDto uploadResume(User user, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Please select a resume file to upload.");
        }

        if (file.getSize() > MAX_RESUME_SIZE) {
            throw new IllegalArgumentException("Resume file size exceeds the 5 MB limit.");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.toLowerCase().endsWith(".pdf")) {
            throw new IllegalArgumentException("Only PDF format (.pdf) is supported for resume uploads.");
        }

        String userPrefix = "user-" + (user.getId() != null ? user.getId().toString().substring(0, Math.min(8, user.getId().toString().length())) : "default");

        // Delete old resume if present
        String oldResumeUrl = user.getResumeUrl();
        if (oldResumeUrl != null && !oldResumeUrl.isBlank()) {
            fileUploadService.deleteOldDocument(oldResumeUrl, userPrefix);
        }

        String savedFileUrl = fileUploadService.storeFile(file, "documents", userPrefix);
        user.setResumeUrl(savedFileUrl);
        user.setResumeFilename(originalFilename);
        user.setResumeFileSize(file.getSize());
        user.setResumeUploadedAt(Instant.now());

        user = userRepository.save(user);

        String accessibleUrl = savedFileUrl;
        if (accessibleUrl.startsWith("/uploads/")) {
            accessibleUrl = "/api" + accessibleUrl;
        }

        return new ResumeDto(
            user.getResumeFilename(),
            accessibleUrl,
            user.getResumeFileSize(),
            user.getResumeUploadedAt()
        );
    }

    @Transactional
    public void deleteResume(User user) {
        String oldResumeUrl = user.getResumeUrl();
        String userPrefix = "user-" + (user.getId() != null ? user.getId().toString().substring(0, Math.min(8, user.getId().toString().length())) : "default");
        if (oldResumeUrl != null && !oldResumeUrl.isBlank()) {
            fileUploadService.deleteOldDocument(oldResumeUrl, userPrefix);
        }

        user.setResumeUrl(null);
        user.setResumeFilename(null);
        user.setResumeFileSize(null);
        user.setResumeUploadedAt(null);
        userRepository.save(user);
    }
}
