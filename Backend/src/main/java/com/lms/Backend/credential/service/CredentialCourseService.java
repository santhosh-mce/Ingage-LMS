package com.lms.Backend.credential.service;

import com.lms.Backend.common.exception.ResourceNotFoundException;
import com.lms.Backend.credential.dto.CredentialCourseDto;
import com.lms.Backend.credential.dto.CredentialModuleDto;
import com.lms.Backend.credential.dto.UserCredentialDto;
import com.lms.Backend.credential.entity.CredentialCourse;
import com.lms.Backend.credential.entity.CredentialEnrollment;
import com.lms.Backend.credential.entity.CredentialModule;
import com.lms.Backend.credential.repository.CredentialCourseRepository;
import com.lms.Backend.credential.repository.CredentialEnrollmentRepository;
import com.lms.Backend.credential.repository.CredentialModuleRepository;
import com.lms.Backend.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CredentialCourseService {

    private final CredentialCourseRepository courseRepository;
    private final CredentialModuleRepository moduleRepository;
    private final CredentialEnrollmentRepository enrollmentRepository;

    public CredentialCourseService(
        CredentialCourseRepository courseRepository,
        CredentialModuleRepository moduleRepository,
        CredentialEnrollmentRepository enrollmentRepository
    ) {
        this.courseRepository = courseRepository;
        this.moduleRepository = moduleRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    @Transactional(readOnly = true)
    public List<CredentialCourseDto> getAllPublishedCourses(String category, String level, String search, String sort, User user) {
        List<CredentialCourse> list;

        if (search != null && !search.isBlank()) {
            list = courseRepository.searchCourses(search.trim());
        } else if (category != null && !category.isBlank() && !category.equalsIgnoreCase("All")) {
            list = courseRepository.findByPublishedTrueAndCategoryIgnoreCase(category.trim());
        } else {
            list = courseRepository.findByPublishedTrueOrderByFeaturedDescCreatedAtDesc();
        }

        if (level != null && !level.isBlank() && !level.equalsIgnoreCase("All")) {
            list = list.stream()
                .filter(c -> c.getLevel() != null && c.getLevel().equalsIgnoreCase(level.trim()))
                .collect(Collectors.toList());
        }

        // Sorting
        if ("newest".equalsIgnoreCase(sort)) {
            list.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
        } else if ("popular".equalsIgnoreCase(sort)) {
            list.sort((a, b) -> Integer.compare(
                b.getLearnersCount() != null ? b.getLearnersCount() : 0,
                a.getLearnersCount() != null ? a.getLearnersCount() : 0
            ));
        } else if ("name_asc".equalsIgnoreCase(sort) || "a-z".equalsIgnoreCase(sort)) {
            list.sort(Comparator.comparing(CredentialCourse::getTitle, String.CASE_INSENSITIVE_ORDER));
        }

        return list.stream().map(c -> toDto(c, user)).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CredentialCourseDto getCourseBySlug(String slug, User user) {
        CredentialCourse course = courseRepository.findBySlug(slug)
            .orElseThrow(() -> new ResourceNotFoundException("Google course not found with slug: " + slug));
        return toDto(course, user);
    }

    @Transactional(readOnly = true)
    public List<CredentialCourseDto> getFeaturedCourses(User user) {
        return courseRepository.findByPublishedTrueAndFeaturedTrue().stream()
            .map(c -> toDto(c, user))
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CredentialCourseDto> getCoursesByCareerSlug(String careerSlug, User user) {
        return courseRepository.findByPublishedTrueAndCareerSlug(careerSlug).stream()
            .map(c -> toDto(c, user))
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getCategoriesWithCounts() {
        List<CredentialCourse> published = courseRepository.findByPublishedTrueOrderByFeaturedDescCreatedAtDesc();
        Map<String, Long> countMap = published.stream()
            .filter(c -> c.getCategory() != null)
            .collect(Collectors.groupingBy(CredentialCourse::getCategory, Collectors.counting()));

        List<Map<String, Object>> result = new ArrayList<>();
        // All tab count
        result.add(Map.of("category", "All", "count", published.size()));

        countMap.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .forEach(e -> result.add(Map.of("category", e.getKey(), "count", e.getValue())));

        return result;
    }

    @Transactional(readOnly = true)
    public List<CredentialCourseDto> searchCourses(String query, User user) {
        if (query == null || query.isBlank()) {
            return getAllPublishedCourses(null, null, null, null, user);
        }
        return courseRepository.searchCourses(query.trim()).stream()
            .map(c -> toDto(c, user))
            .collect(Collectors.toList());
    }

    @Transactional
    public CredentialCourseDto enrollInCourse(String slug, User user) {
        CredentialCourse course = courseRepository.findBySlug(slug)
            .orElseThrow(() -> new ResourceNotFoundException("Google course not found with slug: " + slug));

        CredentialEnrollment enrollment = enrollmentRepository.findByUserIdAndCourseId(user.getId(), course.getId())
            .orElseGet(() -> {
                CredentialEnrollment e = new CredentialEnrollment(user, course);
                e.setStatus("IN_PROGRESS");
                e.setProgressPercentage(0);
                e.setCredentialId("CRED-GOOG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
                e.setCredentialUrl(course.getCredentialUrl());
                return enrollmentRepository.save(e);
            });

        enrollment.setLastAccessedAt(Instant.now());
        enrollmentRepository.save(enrollment);

        // increment learners count
        course.setLearnersCount((course.getLearnersCount() != null ? course.getLearnersCount() : 0) + 1);
        courseRepository.save(course);

        return toDto(course, user);
    }

    @Transactional
    public CredentialCourseDto updateProgress(String slug, Integer completedModules, User user) {
        CredentialCourse course = courseRepository.findBySlug(slug)
            .orElseThrow(() -> new ResourceNotFoundException("Google course not found with slug: " + slug));

        CredentialEnrollment enrollment = enrollmentRepository.findByUserIdAndCourseId(user.getId(), course.getId())
            .orElseThrow(() -> new IllegalStateException("You are not enrolled in this course"));

        int totalModules = (course.getModules() != null && !course.getModules().isEmpty())
            ? course.getModules().size()
            : 6;

        int done = completedModules != null ? Math.min(completedModules, totalModules) : enrollment.getCompletedModulesCount() + 1;
        int percentage = Math.min(100, (int) Math.round(((double) done / totalModules) * 100));

        enrollment.setCompletedModulesCount(done);
        enrollment.setProgressPercentage(percentage);
        enrollment.setLastAccessedAt(Instant.now());

        if (percentage >= 100) {
            enrollment.setStatus("CREDENTIAL_EARNED");
            if (enrollment.getCompletedAt() == null) {
                enrollment.setCompletedAt(Instant.now());
            }
        } else {
            enrollment.setStatus("IN_PROGRESS");
        }

        enrollmentRepository.save(enrollment);
        return toDto(course, user);
    }

    @Transactional(readOnly = true)
    public List<UserCredentialDto> getUserCredentials(User user) {
        List<CredentialEnrollment> enrollments = enrollmentRepository.findByUserIdOrderByLastAccessedAtDesc(user.getId());
        List<UserCredentialDto> dtos = new ArrayList<>();

        for (CredentialEnrollment e : enrollments) {
            CredentialCourse c = e.getCourse();
            UserCredentialDto dto = new UserCredentialDto();
            dto.setEnrollmentId(e.getId());
            dto.setCourseId(c.getId());
            dto.setCourseTitle(c.getTitle());
            dto.setCourseSlug(c.getSlug());
            dto.setCourseThumbnail(c.getThumbnail());
            dto.setCategory(c.getCategory());
            dto.setLevel(c.getLevel());
            dto.setProvider(c.getProvider());
            dto.setCredentialName(c.getCredentialName());
            dto.setCredentialType(c.getCredentialType());
            dto.setCredentialUrl(c.getCredentialUrl());
            dto.setStatus(e.getStatus());
            dto.setProgressPercentage(e.getProgressPercentage());
            dto.setCompletedModulesCount(e.getCompletedModulesCount());
            dto.setTotalModulesCount(c.getModules() != null ? c.getModules().size() : 6);
            dto.setCredentialId(e.getCredentialId());
            dto.setEnrolledAt(e.getEnrolledAt());
            dto.setCompletedAt(e.getCompletedAt());
            dtos.add(dto);
        }

        return dtos;
    }

    // ==========================================
    // ADMIN ENDPOINTS
    // ==========================================

    @Transactional(readOnly = true)
    public List<CredentialCourseDto> getAllAdminCourses() {
        return courseRepository.findAll().stream()
            .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
            .map(c -> toDto(c, null))
            .collect(Collectors.toList());
    }

    @Transactional
    public CredentialCourseDto createAdminCourse(CredentialCourseDto dto) {
        CredentialCourse c = new CredentialCourse();
        copyDtoToEntity(dto, c);

        if (c.getSlug() == null || c.getSlug().isBlank()) {
            c.setSlug(c.getTitle().toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", ""));
        }

        CredentialCourse saved = courseRepository.save(c);

        if (dto.getModules() != null && !dto.getModules().isEmpty()) {
            for (int i = 0; i < dto.getModules().size(); i++) {
                CredentialModuleDto mDto = dto.getModules().get(i);
                CredentialModule m = new CredentialModule(
                    mDto.getTitle(),
                    mDto.getDescription(),
                    mDto.getDuration(),
                    mDto.getOrderIndex() != null ? mDto.getOrderIndex() : i,
                    saved
                );
                moduleRepository.save(m);
            }
        }

        return toDto(saved, null);
    }

    @Transactional
    public CredentialCourseDto updateAdminCourse(Long id, CredentialCourseDto dto) {
        CredentialCourse c = courseRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));

        copyDtoToEntity(dto, c);
        CredentialCourse saved = courseRepository.save(c);

        if (dto.getModules() != null) {
            // Replace modules
            moduleRepository.deleteAll(c.getModules());
            c.getModules().clear();
            for (int i = 0; i < dto.getModules().size(); i++) {
                CredentialModuleDto mDto = dto.getModules().get(i);
                CredentialModule m = new CredentialModule(
                    mDto.getTitle(),
                    mDto.getDescription(),
                    mDto.getDuration(),
                    mDto.getOrderIndex() != null ? mDto.getOrderIndex() : i,
                    saved
                );
                moduleRepository.save(m);
            }
        }

        return toDto(saved, null);
    }

    @Transactional
    public void deleteAdminCourse(Long id) {
        CredentialCourse c = courseRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
        courseRepository.delete(c);
    }

    @Transactional
    public CredentialCourseDto togglePublish(Long id) {
        CredentialCourse c = courseRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
        c.setPublished(!c.isPublished());
        return toDto(courseRepository.save(c), null);
    }

    // ==========================================
    // HELPERS
    // ==========================================

    public CredentialCourseDto toDto(CredentialCourse c, User user) {
        CredentialCourseDto dto = new CredentialCourseDto();
        dto.setId(c.getId());
        dto.setTitle(c.getTitle());
        dto.setSlug(c.getSlug());
        dto.setProvider(c.getProvider() != null ? c.getProvider() : "Google");
        dto.setCategory(c.getCategory());
        dto.setLevel(c.getLevel());
        dto.setDuration(c.getDuration());
        dto.setDescription(c.getDescription());
        dto.setShortDescription(c.getShortDescription());
        dto.setThumbnail(c.getThumbnail());
        dto.setCredentialName(c.getCredentialName());
        dto.setCredentialType(c.getCredentialType());
        dto.setCredentialUrl(c.getCredentialUrl());
        dto.setPrice(c.getPrice());
        dto.setDiscount(c.getDiscount());
        dto.setFree(c.isFree());
        dto.setPublished(c.isPublished());
        dto.setFeatured(c.isFeatured());
        dto.setRating(c.getRating());
        dto.setLearnersCount(c.getLearnersCount());
        dto.setCareerSlug(c.getCareerSlug());
        dto.setLearningOutcomes(c.getLearningOutcomes() != null ? new ArrayList<>(c.getLearningOutcomes()) : new ArrayList<>());
        dto.setPrerequisites(c.getPrerequisites() != null ? new ArrayList<>(c.getPrerequisites()) : new ArrayList<>());
        dto.setCreatedAt(c.getCreatedAt());
        dto.setUpdatedAt(c.getUpdatedAt());

        if (c.getModules() != null) {
            dto.setModules(c.getModules().stream()
                .map(m -> new CredentialModuleDto(m.getId(), m.getTitle(), m.getDescription(), m.getDuration(), m.getOrderIndex()))
                .collect(Collectors.toList()));
        }

        // Populate user contextual enrollment status if user logged in
        if (user != null) {
            Optional<CredentialEnrollment> enrollmentOpt = enrollmentRepository.findByUserIdAndCourseId(user.getId(), c.getId());
            if (enrollmentOpt.isPresent()) {
                CredentialEnrollment e = enrollmentOpt.get();
                dto.setEnrolled(true);
                dto.setProgressPercentage(e.getProgressPercentage());
                dto.setUserStatus(e.getStatus());
                dto.setCompletedModulesCount(e.getCompletedModulesCount());
                dto.setCredentialId(e.getCredentialId());
            } else {
                dto.setEnrolled(false);
                dto.setProgressPercentage(0);
                dto.setUserStatus("NOT_ENROLLED");
                dto.setCompletedModulesCount(0);
            }
        }

        return dto;
    }

    private void copyDtoToEntity(CredentialCourseDto dto, CredentialCourse c) {
        if (dto.getTitle() != null) c.setTitle(dto.getTitle().trim());
        if (dto.getSlug() != null && !dto.getSlug().isBlank()) c.setSlug(dto.getSlug().trim());
        if (dto.getProvider() != null) c.setProvider(dto.getProvider().trim());
        if (dto.getCategory() != null) c.setCategory(dto.getCategory().trim());
        if (dto.getLevel() != null) c.setLevel(dto.getLevel().trim());
        if (dto.getDuration() != null) c.setDuration(dto.getDuration().trim());
        if (dto.getDescription() != null) c.setDescription(dto.getDescription());
        if (dto.getShortDescription() != null) c.setShortDescription(dto.getShortDescription());
        if (dto.getThumbnail() != null) c.setThumbnail(dto.getThumbnail());
        if (dto.getCredentialName() != null) c.setCredentialName(dto.getCredentialName().trim());
        if (dto.getCredentialType() != null) c.setCredentialType(dto.getCredentialType().trim());
        if (dto.getCredentialUrl() != null) c.setCredentialUrl(dto.getCredentialUrl().trim());
        if (dto.getPrice() != null) c.setPrice(dto.getPrice());
        if (dto.getDiscount() != null) c.setDiscount(dto.getDiscount());
        c.setFree(dto.isFree() || (dto.getPrice() != null && dto.getPrice() <= 0));
        c.setPublished(dto.isPublished());
        c.setFeatured(dto.isFeatured());
        if (dto.getRating() != null) c.setRating(dto.getRating());
        if (dto.getLearnersCount() != null) c.setLearnersCount(dto.getLearnersCount());
        if (dto.getCareerSlug() != null) c.setCareerSlug(dto.getCareerSlug().trim());
        if (dto.getLearningOutcomes() != null) c.setLearningOutcomes(dto.getLearningOutcomes());
        if (dto.getPrerequisites() != null) c.setPrerequisites(dto.getPrerequisites());
    }
}
