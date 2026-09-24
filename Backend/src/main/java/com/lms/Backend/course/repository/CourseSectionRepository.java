package com.lms.Backend.course.repository;

import com.lms.Backend.course.entity.CourseSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseSectionRepository extends JpaRepository<CourseSection, Long> {
    List<CourseSection> findByCourseIdOrderByDisplayOrderAsc(Long courseId);
    long countByCourseId(Long courseId);
    void deleteByCourseId(Long courseId);
}
