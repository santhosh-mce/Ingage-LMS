package com.lms.Backend.course.repository;

import com.lms.Backend.course.entity.CourseCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseCategoryRepository extends JpaRepository<CourseCategory, Long> {
    List<CourseCategory> findAllByOrderByDisplayOrderAsc();
    Optional<CourseCategory> findBySlug(String slug);
    boolean existsByNameIgnoreCase(String name);
}
