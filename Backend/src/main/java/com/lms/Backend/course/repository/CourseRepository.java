package com.lms.Backend.course.repository;

import com.lms.Backend.course.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    List<Course> findByPublishedTrueOrderByIdAsc();

    Optional<Course> findByIdAndPublishedTrue(Long id);

    @Query("SELECT c FROM Course c WHERE c.published = true AND (" +
           "LOWER(c.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.description) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.category) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "ORDER BY c.id ASC")
    List<Course> searchPublishedCourses(@Param("search") String search);

    boolean existsByTitle(String title);

    long countByStatus(com.lms.Backend.course.entity.CourseStatus status);

    List<Course> findAllByOrderByIdDesc();

    Optional<Course> findBySlug(String slug);
}
