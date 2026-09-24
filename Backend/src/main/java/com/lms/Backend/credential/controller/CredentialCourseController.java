package com.lms.Backend.credential.controller;

import com.lms.Backend.credential.dto.CredentialCourseDto;
import com.lms.Backend.credential.dto.UserCredentialDto;
import com.lms.Backend.credential.service.CredentialCourseService;
import com.lms.Backend.user.entity.User;
import com.lms.Backend.user.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping
public class CredentialCourseController {

    private final CredentialCourseService courseService;
    private final UserRepository userRepository;

    public CredentialCourseController(CredentialCourseService courseService, UserRepository userRepository) {
        this.courseService = courseService;
        this.userRepository = userRepository;
    }

    private User getOptionalUser(Principal principal) {
        if (principal == null || principal.getName() == null) return null;
        return userRepository.findByEmailIgnoreCase(principal.getName()).orElse(null);
    }

    private User getRequiredUser(Principal principal) {
        if (principal == null || principal.getName() == null) {
            throw new IllegalArgumentException("Authentication required");
        }
        return userRepository.findByEmailIgnoreCase(principal.getName())
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    @GetMapping({"/credential-courses", "/api/credential-courses"})
    public ResponseEntity<List<CredentialCourseDto>> getAllCourses(
        @RequestParam(required = false) String category,
        @RequestParam(required = false) String level,
        @RequestParam(required = false) String search,
        @RequestParam(required = false) String sort,
        Principal principal
    ) {
        User user = getOptionalUser(principal);
        return ResponseEntity.ok(courseService.getAllPublishedCourses(category, level, search, sort, user));
    }

    @GetMapping({"/credential-courses/{slug}", "/api/credential-courses/{slug}"})
    public ResponseEntity<CredentialCourseDto> getCourseBySlug(
        @PathVariable String slug,
        Principal principal
    ) {
        User user = getOptionalUser(principal);
        return ResponseEntity.ok(courseService.getCourseBySlug(slug, user));
    }

    @GetMapping({"/credential-courses/categories", "/api/credential-courses/categories"})
    public ResponseEntity<List<Map<String, Object>>> getCategories() {
        return ResponseEntity.ok(courseService.getCategoriesWithCounts());
    }

    @GetMapping({"/credential-courses/featured", "/api/credential-courses/featured"})
    public ResponseEntity<List<CredentialCourseDto>> getFeatured(Principal principal) {
        User user = getOptionalUser(principal);
        return ResponseEntity.ok(courseService.getFeaturedCourses(user));
    }

    @GetMapping({"/credential-courses/search", "/api/credential-courses/search"})
    public ResponseEntity<List<CredentialCourseDto>> search(
        @RequestParam String q,
        Principal principal
    ) {
        User user = getOptionalUser(principal);
        return ResponseEntity.ok(courseService.searchCourses(q, user));
    }

    @GetMapping({"/credential-courses/career/{careerSlug}", "/api/credential-courses/career/{careerSlug}"})
    public ResponseEntity<List<CredentialCourseDto>> getByCareerSlug(
        @PathVariable String careerSlug,
        Principal principal
    ) {
        User user = getOptionalUser(principal);
        return ResponseEntity.ok(courseService.getCoursesByCareerSlug(careerSlug, user));
    }

    @PostMapping({"/credential-courses/{slug}/enroll", "/api/credential-courses/{slug}/enroll"})
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CredentialCourseDto> enroll(
        @PathVariable String slug,
        Principal principal
    ) {
        User user = getRequiredUser(principal);
        return ResponseEntity.ok(courseService.enrollInCourse(slug, user));
    }

    @PostMapping({"/credential-courses/{slug}/progress", "/api/credential-courses/{slug}/progress"})
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CredentialCourseDto> updateProgress(
        @PathVariable String slug,
        @RequestBody(required = false) Map<String, Object> payload,
        Principal principal
    ) {
        User user = getRequiredUser(principal);
        Integer completedModules = null;
        if (payload != null && payload.containsKey("completedModules")) {
            completedModules = Integer.parseInt(payload.get("completedModules").toString());
        }
        return ResponseEntity.ok(courseService.updateProgress(slug, completedModules, user));
    }

    @GetMapping({"/my/credentials", "/api/my/credentials"})
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<UserCredentialDto>> getMyCredentials(Principal principal) {
        User user = getRequiredUser(principal);
        return ResponseEntity.ok(courseService.getUserCredentials(user));
    }
}
