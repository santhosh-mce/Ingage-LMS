package com.lms.Backend.course.repository;

import com.lms.Backend.course.entity.CourseCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseCategoryRepository extends JpaRepository<CourseCategory, Long> {
    List<CourseCategory> findAllByOrderByDisplayOrderAsc();
    List<CourseCategory> findByActiveTrueOrderByDisplayOrderAsc();
    Optional<CourseCategory> findBySlug(String slug);
    Optional<CourseCategory> findBySlugIgnoreCase(String slug);
    Optional<CourseCategory> findByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);
    boolean existsBySlugIgnoreCase(String slug);
    List<CourseCategory> findByActiveOrderByDisplayOrderAsc(boolean active);
}
