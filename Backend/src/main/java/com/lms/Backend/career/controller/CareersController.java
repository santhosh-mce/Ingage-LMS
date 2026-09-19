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

    public CareersController(CareerService careerService, UserRepository userRepository) {
        this.careerService = careerService;
        this.userRepository = userRepository;
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
     * GET /api/careers/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<CareerDetailResponse> getCareerById(
        @PathVariable Long id,
        Principal principal
    ) {
        User user = resolveUser(principal);
        return ResponseEntity.ok(careerService.getCareerById(id, user));
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
}
