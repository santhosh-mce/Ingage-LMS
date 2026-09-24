package com.lms.Backend.career.controller;

import com.lms.Backend.career.dto.CareerDetailResponse;
import com.lms.Backend.career.dto.CareerResponse;
import com.lms.Backend.career.dto.CareerStatsResponse;
import com.lms.Backend.career.dto.PageResponse;
import com.lms.Backend.career.service.CareerService;
import com.lms.Backend.user.entity.User;
import com.lms.Backend.user.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/careers")
public class CareersController {

    private final CareerService careerService;
    private final UserRepository userRepository;
    private final com.lms.Backend.career.service.CareerProgressService careerProgressService;

    public CareersController(
        CareerService careerService,
        UserRepository userRepository,
        com.lms.Backend.career.service.CareerProgressService careerProgressService
    ) {
        this.careerService = careerService;
        this.userRepository = userRepository;
        this.careerProgressService = careerProgressService;
    }

    private User resolveUser(Principal principal) {
        if (principal == null || principal.getName() == null) {
            return null;
        }
        return userRepository.findByEmailIgnoreCase(principal.getName()).orElse(null);
    }

    /**
     * GET /api/careers
     * Search, filter by category/level/featured/popular, with pagination & sorting.
     */
    @GetMapping
    public ResponseEntity<PageResponse<CareerResponse>> getCareers(
        @RequestParam(required = false) String search,
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) String category,
        @RequestParam(required = false) String level,
        @RequestParam(required = false) Boolean featured,
        @RequestParam(required = false) Boolean popular,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(defaultValue = "displayOrder") String sortBy,
        @RequestParam(defaultValue = "asc") String sortDir
    ) {
        String effectiveKeyword = (search != null && !search.isBlank()) ? search : keyword;
        Sort sort = sortDir.equalsIgnoreCase("desc")
            ? Sort.by(sortBy).descending()
            : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        return ResponseEntity.ok(careerService.getFilteredCareers(
            effectiveKeyword, category, level, featured, popular, pageable
        ));
    }

    /**
     * GET /api/careers/search?keyword=python
     */
    @GetMapping("/search")
    public ResponseEntity<PageResponse<CareerResponse>> searchCareers(
        @RequestParam String keyword,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("displayOrder").ascending());
        return ResponseEntity.ok(careerService.searchCareers(keyword, pageable));
    }

    /**
     * GET /api/careers/slug/{slug}
     */
    @GetMapping("/slug/{slug}")
    public ResponseEntity<CareerDetailResponse> getCareerBySlug(
        @PathVariable String slug,
        Principal principal
    ) {
        User user = resolveUser(principal);
        return ResponseEntity.ok(careerService.getCareerBySlug(slug, user));
    }

    /**
     * GET /api/careers/{idOrSlug}
     * Supports lookup by numeric ID or string slug (e.g. /api/careers/data-analyst)
     */
    @GetMapping("/{idOrSlug}")
    public ResponseEntity<CareerDetailResponse> getCareerByIdOrSlug(
        @PathVariable String idOrSlug,
        Principal principal
    ) {
        User user = resolveUser(principal);
        try {
            Long id = Long.parseLong(idOrSlug);
            return ResponseEntity.ok(careerService.getCareerById(id, user));
        } catch (NumberFormatException e) {
            return ResponseEntity.ok(careerService.getCareerBySlug(idOrSlug, user));
        }
    }

    /**
     * GET /api/careers/categories
     */
    @GetMapping("/categories")
    public ResponseEntity<List<String>> getCategories() {
        return ResponseEntity.ok(careerService.getCategories());
    }

    /**
     * GET /api/careers/{id}/stats
     */
    @GetMapping("/{id}/stats")
    public ResponseEntity<CareerStatsResponse> getCareerStats(@PathVariable Long id) {
        return ResponseEntity.ok(careerService.getCareerStats(id));
    }

    /**
     * GET /api/careers/{slugOrId}/curriculum/download
     * Protected download endpoint:
     * - 401 Unauthorized if unauthenticated
     * - 403 Forbidden if user has not purchased/enrolled in the course
     * - 200 OK with curriculum file if user has purchased the course
     */
    @GetMapping("/{slugOrId}/curriculum/download")
    public ResponseEntity<?> downloadCurriculum(
        @PathVariable String slugOrId,
        Principal principal
    ) {
        // 1. Unauthenticated -> 401 Unauthorized
        if (principal == null || principal.getName() == null) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.UNAUTHORIZED)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .body(java.util.Map.of(
                    "status", 401,
                    "error", "Unauthorized",
                    "message", "Sign up or login to continue"
                ));
        }

        User user = resolveUser(principal);
        if (user == null) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.UNAUTHORIZED)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .body(java.util.Map.of(
                    "status", 401,
                    "error", "Unauthorized",
                    "message", "User account not found"
                ));
        }

        // Find career
        com.lms.Backend.career.entity.Career career = careerService.findCareerEntity(slugOrId);
        if (career == null) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.NOT_FOUND)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .body(java.util.Map.of(
                    "status", 404,
                    "error", "Not Found",
                    "message", "Career path not found: " + slugOrId
                ));
        }

        // 2. User has NOT purchased -> 403 Forbidden
        boolean isPurchased = careerService.hasUserPurchasedCareer(career, user);
        if (!isPurchased) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.FORBIDDEN)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .body(java.util.Map.of(
                    "status", 403,
                    "error", "Forbidden",
                    "message", "Purchase the course to download the curriculum.",
                    "requiresPurchase", true
                ));
        }

        // 3. User has purchased -> 200 OK with file attachment
        String content = careerService.generateCurriculumContent(career);
        byte[] bytes = content.getBytes(java.nio.charset.StandardCharsets.UTF_8);

        return ResponseEntity.ok()
            .contentType(org.springframework.http.MediaType.TEXT_PLAIN)
            .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + career.getSlug() + "-curriculum.txt\"")
            .contentLength(bytes.length)
            .body(bytes);
    }

    /**
     * GET /api/careers/{slugOrId}/access
     * Returns learner enrollment, completion status, and certificate readiness
     */
    @GetMapping("/{slugOrId}/access")
    public ResponseEntity<?> getCareerAccess(
        @PathVariable String slugOrId,
        Principal principal
    ) {
        com.lms.Backend.career.entity.Career career = careerService.findCareerEntity(slugOrId);
        if (career == null) {
            return ResponseEntity.notFound().build();
        }

        User user = resolveUser(principal);
        if (user == null) {
            return ResponseEntity.ok(java.util.Map.of(
                "careerId", career.getId(),
                "careerTitle", career.getTitle(),
                "careerSlug", career.getSlug(),
                "enrolled", false,
                "status", "NOT_ENROLLED",
                "progressPercentage", 0,
                "completed", false,
                "certificateAvailable", false
            ));
        }

        return ResponseEntity.ok(careerProgressService.getCareerProgressDetail(user.getId(), career.getId()));
    }

    /**
     * POST /api/careers/{id}/claim-certificate
     * Issues verified Certificate for 100% completed career path
     */
    @PostMapping("/{id}/claim-certificate")
    public ResponseEntity<?> claimCertificate(
        @PathVariable Long id,
        Principal principal
    ) {
        User user = resolveUser(principal);
        if (user == null) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.UNAUTHORIZED).body(
                java.util.Map.of("error", "Sign in required to claim certificate")
            );
        }

        try {
            com.lms.Backend.certificate.entity.Certificate cert = careerProgressService.claimCareerCertificate(user.getId(), id);
            return ResponseEntity.ok(java.util.Map.of(
                "success", true,
                "certificateNumber", cert.getCertificateNumber(),
                "verificationCode", cert.getVerificationCode(),
                "courseName", cert.getCourseName(),
                "issueDate", cert.getIssuedAt() != null ? cert.getIssuedAt().toString() : ""
            ));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/careers/my-enrollments
     * Returns list of Career Paths the logged-in user is currently enrolled in
     */
    @GetMapping("/my-enrollments")
    public ResponseEntity<?> getMyCareerEnrollments(Principal principal) {
        User user = resolveUser(principal);
        if (user == null) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.UNAUTHORIZED).body(
                java.util.Map.of("error", "Sign in required")
            );
        }
        return ResponseEntity.ok(careerProgressService.getUserCareerEnrollments(user.getId()));
    }
}

