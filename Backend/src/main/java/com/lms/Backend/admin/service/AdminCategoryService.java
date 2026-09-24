package com.lms.Backend.admin.service;

import com.lms.Backend.course.entity.Course;
import com.lms.Backend.course.entity.CourseCategory;
import com.lms.Backend.course.repository.CourseCategoryRepository;
import com.lms.Backend.course.repository.CourseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminCategoryService {

    private static final Logger log = LoggerFactory.getLogger(AdminCategoryService.class);

    private final CourseCategoryRepository categoryRepository;
    private final CourseRepository courseRepository;

    public AdminCategoryService(
        CourseCategoryRepository categoryRepository,
        CourseRepository courseRepository
    ) {
        this.categoryRepository = categoryRepository;
        this.courseRepository = courseRepository;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getAllCategories(String search, String status) {
        List<CourseCategory> categories = categoryRepository.findAllByOrderByDisplayOrderAsc();
        List<Map<String, Object>> result = new ArrayList<>();

        String cleanSearch = (search != null && !search.isBlank()) ? search.trim().toLowerCase() : null;
        String cleanStatus = (status != null && !status.isBlank()) ? status.trim().toUpperCase() : "ALL";

        for (CourseCategory c : categories) {
            // Filter by Status
            if ("ACTIVE".equals(cleanStatus) && !c.isActive()) {
                continue;
            }
            if ("INACTIVE".equals(cleanStatus) && c.isActive()) {
                continue;
            }

            // Filter by Search (name, slug, description)
            if (cleanSearch != null) {
                boolean matchName = c.getName() != null && c.getName().toLowerCase().contains(cleanSearch);
                boolean matchSlug = c.getSlug() != null && c.getSlug().toLowerCase().contains(cleanSearch);
                boolean matchDesc = c.getDescription() != null && c.getDescription().toLowerCase().contains(cleanSearch);
                if (!matchName && !matchSlug && !matchDesc) {
                    continue;
                }
            }

            long courseCount = courseRepository.countByCategoryIgnoreCase(c.getName());

            Map<String, Object> dto = new LinkedHashMap<>();
            dto.put("id", c.getId());
            dto.put("name", c.getName());
            dto.put("slug", c.getSlug());
            dto.put("description", c.getDescription() != null ? c.getDescription() : "");
            dto.put("active", c.isActive());
            dto.put("status", c.isActive() ? "Active" : "Inactive");
            dto.put("coursesCount", courseCount);
            dto.put("displayOrder", c.getDisplayOrder());
            dto.put("createdAt", c.getCreatedAt() != null ? c.getCreatedAt().toString() : null);
            dto.put("updatedAt", c.getUpdatedAt() != null ? c.getUpdatedAt().toString() : null);

            result.add(dto);
        }

        return result;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getActiveCategories() {
        List<CourseCategory> categories = categoryRepository.findByActiveTrueOrderByDisplayOrderAsc();
        List<Map<String, Object>> result = new ArrayList<>();

        for (CourseCategory c : categories) {
            Map<String, Object> dto = new LinkedHashMap<>();
            dto.put("id", c.getId());
            dto.put("name", c.getName());
            dto.put("slug", c.getSlug());
            dto.put("description", c.getDescription());
            dto.put("displayOrder", c.getDisplayOrder());
            result.add(dto);
        }

        return result;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getCategoryById(Long id) {
        CourseCategory c = categoryRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + id));

        long courseCount = courseRepository.countByCategoryIgnoreCase(c.getName());

        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", c.getId());
        dto.put("name", c.getName());
        dto.put("slug", c.getSlug());
        dto.put("description", c.getDescription() != null ? c.getDescription() : "");
        dto.put("active", c.isActive());
        dto.put("status", c.isActive() ? "Active" : "Inactive");
        dto.put("coursesCount", courseCount);
        dto.put("displayOrder", c.getDisplayOrder());
        dto.put("createdAt", c.getCreatedAt() != null ? c.getCreatedAt().toString() : null);
        dto.put("updatedAt", c.getUpdatedAt() != null ? c.getUpdatedAt().toString() : null);

        return dto;
    }

    @Transactional
    public CourseCategory createCategory(Map<String, Object> payload) {
        if (!payload.containsKey("name") || payload.get("name") == null || payload.get("name").toString().trim().isBlank()) {
            throw new IllegalArgumentException("Category name is required.");
        }

        String name = payload.get("name").toString().trim();

        // Check for duplicate name
        if (categoryRepository.existsByNameIgnoreCase(name)) {
            throw new IllegalArgumentException("Category with this name already exists.");
        }

        // Generate / validate slug
        String slug = payload.get("slug") != null ? payload.get("slug").toString().trim() : "";
        if (slug.isBlank()) {
            slug = generateSlug(name);
        } else {
            slug = cleanSlug(slug);
        }

        if (categoryRepository.existsBySlugIgnoreCase(slug)) {
            throw new IllegalArgumentException("This slug is already in use.");
        }

        String description = payload.get("description") != null ? payload.get("description").toString().trim() : "";
        boolean active = true;
        if (payload.containsKey("active")) {
            active = Boolean.parseBoolean(payload.get("active").toString());
        } else if (payload.containsKey("status")) {
            active = !"INACTIVE".equalsIgnoreCase(payload.get("status").toString().trim());
        }

        int displayOrder = (int) (categoryRepository.count() + 1);
        if (payload.containsKey("displayOrder") && payload.get("displayOrder") != null) {
            try {
                displayOrder = Integer.parseInt(payload.get("displayOrder").toString());
            } catch (NumberFormatException ignored) {}
        }

        CourseCategory category = new CourseCategory(name, slug, description, displayOrder);
        category.setActive(active);

        CourseCategory saved = categoryRepository.save(category);
        log.info("[AdminCategoryService] Created course category '{}' (slug: {}, id: {})", name, slug, saved.getId());
        return saved;
    }

    @Transactional
    public CourseCategory updateCategory(Long id, Map<String, Object> payload) {
        CourseCategory category = categoryRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + id));

        String oldName = category.getName();

        if (payload.containsKey("name")) {
            String newName = payload.get("name") != null ? payload.get("name").toString().trim() : "";
            if (newName.isBlank()) {
                throw new IllegalArgumentException("Category name is required.");
            }
            if (!newName.equalsIgnoreCase(category.getName()) && categoryRepository.existsByNameIgnoreCase(newName)) {
                throw new IllegalArgumentException("Category with this name already exists.");
            }
            category.setName(newName);
        }

        if (payload.containsKey("slug")) {
            String newSlug = payload.get("slug") != null ? cleanSlug(payload.get("slug").toString().trim()) : "";
            if (newSlug.isBlank()) {
                newSlug = generateSlug(category.getName());
            }
            if (!newSlug.equalsIgnoreCase(category.getSlug()) && categoryRepository.existsBySlugIgnoreCase(newSlug)) {
                throw new IllegalArgumentException("This slug is already in use.");
            }
            category.setSlug(newSlug);
        }

        if (payload.containsKey("description")) {
            category.setDescription(payload.get("description") != null ? payload.get("description").toString().trim() : "");
        }

        if (payload.containsKey("active")) {
            category.setActive(Boolean.parseBoolean(payload.get("active").toString()));
        } else if (payload.containsKey("status")) {
            category.setActive(!"INACTIVE".equalsIgnoreCase(payload.get("status").toString().trim()));
        }

        if (payload.containsKey("displayOrder") && payload.get("displayOrder") != null) {
            try {
                category.setDisplayOrder(Integer.parseInt(payload.get("displayOrder").toString()));
            } catch (NumberFormatException ignored) {}
        }

        CourseCategory saved = categoryRepository.save(category);

        // If the category name was updated, synchronize courses that were using the old category name
        if (!oldName.equalsIgnoreCase(saved.getName())) {
            List<Course> courses = courseRepository.findByCategoryIgnoreCase(oldName);
            for (Course course : courses) {
                course.setCategory(saved.getName());
                courseRepository.save(course);
            }
            log.info("[AdminCategoryService] Synchronized {} courses from old category '{}' to '{}'",
                courses.size(), oldName, saved.getName());
        }

        return saved;
    }

    @Transactional
    public CourseCategory toggleStatus(Long id, Boolean active) {
        CourseCategory category = categoryRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + id));

        boolean newActive = active != null ? active : !category.isActive();
        category.setActive(newActive);
        CourseCategory saved = categoryRepository.save(category);
        log.info("[AdminCategoryService] Toggled category '{}' active status to {}", category.getName(), newActive);
        return saved;
    }

    @Transactional
    public void deleteCategory(Long id) {
        CourseCategory category = categoryRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + id));

        // Strict deletion guard: check if any courses are assigned
        long assignedCount = courseRepository.countByCategoryIgnoreCase(category.getName());
        if (assignedCount > 0) {
            throw new IllegalStateException(
                String.format("This category is currently assigned to %d course%s. Please reassign or remove the category from those courses before deleting.",
                    assignedCount, assignedCount == 1 ? "" : "s")
            );
        }

        categoryRepository.delete(category);
        log.info("[AdminCategoryService] Deleted category '{}' (id: {})", category.getName(), id);
    }

    private String generateSlug(String text) {
        if (text == null) return "";
        return text.toLowerCase().trim()
            .replaceAll("[^a-z0-9]+", "-")
            .replaceAll("^-|-$", "");
    }

    private String cleanSlug(String slug) {
        return slug.toLowerCase().trim()
            .replaceAll("[^a-z0-9-]", "")
            .replaceAll("-+", "-")
            .replaceAll("^-|-$", "");
    }
}
