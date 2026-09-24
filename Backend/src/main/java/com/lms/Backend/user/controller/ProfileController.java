package com.lms.Backend.user.controller;

import com.lms.Backend.user.dto.*;
import com.lms.Backend.user.entity.User;
import com.lms.Backend.user.repository.UserRepository;
import com.lms.Backend.user.service.ProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping
@PreAuthorize("isAuthenticated()")
public class ProfileController {

    private static final Logger log = LoggerFactory.getLogger(ProfileController.class);

    private final ProfileService profileService;
    private final UserRepository userRepository;

    public ProfileController(ProfileService profileService, UserRepository userRepository) {
        this.profileService = profileService;
        this.userRepository = userRepository;
    }

    private User getAuthenticatedUser(Principal principal) {
        if (principal == null || principal.getName() == null) {
            throw new IllegalArgumentException("Unauthorized: Valid session required.");
        }
        return userRepository.findByEmailIgnoreCase(principal.getName())
            .orElseThrow(() -> new IllegalArgumentException("User not found."));
    }

    /**
     * Get the authenticated student's full career profile.
     */
    @GetMapping({"/profile/me", "/api/profile/me"})
    public ResponseEntity<ProfileDto> getMyProfile(Principal principal) {
        User user = getAuthenticatedUser(principal);
        return ResponseEntity.ok(profileService.getProfile(user));
    }

    /**
     * Update personal information.
     */
    @PutMapping({"/profile/me", "/api/profile/me"})
    public ResponseEntity<ProfileDto> updatePersonalInfo(
        @RequestBody PersonalInfoDto dto,
        Principal principal
    ) {
        User user = getAuthenticatedUser(principal);
        return ResponseEntity.ok(profileService.updatePersonalInfo(user, dto));
    }

    /**
     * Update career goal information.
     */
    @PutMapping({"/profile/me/career", "/api/profile/me/career"})
    public ResponseEntity<ProfileDto> updateCareerGoal(
        @RequestBody CareerGoalDto dto,
        Principal principal
    ) {
        User user = getAuthenticatedUser(principal);
        return ResponseEntity.ok(profileService.updateCareerGoal(user, dto));
    }

    /**
     * Update professional links.
     */
    @PutMapping({"/profile/me/links", "/api/profile/me/links"})
    public ResponseEntity<ProfileDto> updateLinks(
        @RequestBody ProfessionalLinksDto dto,
        Principal principal
    ) {
        User user = getAuthenticatedUser(principal);
        return ResponseEntity.ok(profileService.updateLinks(user, dto));
    }

    /**
     * Batch update complete profile (Personal info, career goal, links, education, skills) in one call.
     */
    @PutMapping({"/profile/me/full", "/api/profile/me/full"})
    public ResponseEntity<ProfileDto> updateFullProfile(
        @RequestBody FullProfileUpdateRequest request,
        Principal principal
    ) {
        User user = getAuthenticatedUser(principal);
        return ResponseEntity.ok(profileService.updateFullProfile(user, request));
    }

    // Education
    @GetMapping({"/profile/me/education", "/api/profile/me/education"})
    public ResponseEntity<List<UserEducationDto>> getEducation(Principal principal) {
        User user = getAuthenticatedUser(principal);
        ProfileDto p = profileService.getProfile(user);
        return ResponseEntity.ok(p.getEducation());
    }

    @PostMapping({"/profile/me/education", "/api/profile/me/education"})
    public ResponseEntity<UserEducationDto> addEducation(
        @RequestBody UserEducationDto dto,
        Principal principal
    ) {
        User user = getAuthenticatedUser(principal);
        return ResponseEntity.ok(profileService.addEducation(user, dto));
    }

    @PutMapping({"/profile/me/education/{id}", "/api/profile/me/education/{id}"})
    public ResponseEntity<UserEducationDto> updateEducation(
        @PathVariable Long id,
        @RequestBody UserEducationDto dto,
        Principal principal
    ) {
        User user = getAuthenticatedUser(principal);
        return ResponseEntity.ok(profileService.updateEducation(user, id, dto));
    }

    @DeleteMapping({"/profile/me/education/{id}", "/api/profile/me/education/{id}"})
    public ResponseEntity<Map<String, Object>> deleteEducation(
        @PathVariable Long id,
        Principal principal
    ) {
        User user = getAuthenticatedUser(principal);
        profileService.deleteEducation(user, id);
        return ResponseEntity.ok(Map.of("success", true, "message", "Education record deleted"));
    }

    // Skills
    @GetMapping({"/profile/me/skills", "/api/profile/me/skills"})
    public ResponseEntity<List<UserSkillDto>> getSkills(Principal principal) {
        User user = getAuthenticatedUser(principal);
        ProfileDto p = profileService.getProfile(user);
        return ResponseEntity.ok(p.getSkills());
    }

    @PostMapping({"/profile/me/skills", "/api/profile/me/skills"})
    public ResponseEntity<UserSkillDto> addSkill(
        @RequestBody UserSkillDto dto,
        Principal principal
    ) {
        User user = getAuthenticatedUser(principal);
        return ResponseEntity.ok(profileService.addSkill(user, dto));
    }

    @DeleteMapping({"/profile/me/skills/{id}", "/api/profile/me/skills/{id}"})
    public ResponseEntity<Map<String, Object>> deleteSkill(
        @PathVariable Long id,
        Principal principal
    ) {
        User user = getAuthenticatedUser(principal);
        profileService.deleteSkill(user, id);
        return ResponseEntity.ok(Map.of("success", true, "message", "Skill removed"));
    }

    // Projects
    @GetMapping({"/profile/me/projects", "/api/profile/me/projects"})
    public ResponseEntity<List<UserProjectDto>> getProjects(Principal principal) {
        User user = getAuthenticatedUser(principal);
        ProfileDto p = profileService.getProfile(user);
        return ResponseEntity.ok(p.getProjects());
    }

    @PostMapping({"/profile/me/projects", "/api/profile/me/projects"})
    public ResponseEntity<UserProjectDto> addProject(
        @RequestBody UserProjectDto dto,
        Principal principal
    ) {
        User user = getAuthenticatedUser(principal);
        return ResponseEntity.ok(profileService.addProject(user, dto));
    }

    @PutMapping({"/profile/me/projects/{id}", "/api/profile/me/projects/{id}"})
    public ResponseEntity<UserProjectDto> updateProject(
        @PathVariable Long id,
        @RequestBody UserProjectDto dto,
        Principal principal
    ) {
        User user = getAuthenticatedUser(principal);
        return ResponseEntity.ok(profileService.updateProject(user, id, dto));
    }

    @DeleteMapping({"/profile/me/projects/{id}", "/api/profile/me/projects/{id}"})
    public ResponseEntity<Map<String, Object>> deleteProject(
        @PathVariable Long id,
        Principal principal
    ) {
        User user = getAuthenticatedUser(principal);
        profileService.deleteProject(user, id);
        return ResponseEntity.ok(Map.of("success", true, "message", "Project deleted"));
    }

    // Resume
    @PostMapping(
        value = {"/profile/me/resume", "/api/profile/me/resume"},
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> uploadResume(
        @RequestParam("file") MultipartFile file,
        Principal principal
    ) {
        User user = getAuthenticatedUser(principal);
        try {
            ResumeDto resumeDto = profileService.uploadResume(user, file);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Resume uploaded successfully",
                "resume", resumeDto
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to upload resume for user {}: {}", user.getEmail(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to upload resume: " + e.getMessage()));
        }
    }

    @DeleteMapping({"/profile/me/resume", "/api/profile/me/resume"})
    public ResponseEntity<Map<String, Object>> deleteResume(Principal principal) {
        User user = getAuthenticatedUser(principal);
        profileService.deleteResume(user);
        return ResponseEntity.ok(Map.of("success", true, "message", "Resume deleted successfully"));
    }

    // Career Compass
    @GetMapping({"/profile/me/career-compass", "/api/profile/me/career-compass"})
    public ResponseEntity<CareerCompassReadinessDto> getCareerCompass(Principal principal) {
        User user = getAuthenticatedUser(principal);
        ProfileDto p = profileService.getProfile(user);
        return ResponseEntity.ok(p.getCareerCompass());
    }
}
