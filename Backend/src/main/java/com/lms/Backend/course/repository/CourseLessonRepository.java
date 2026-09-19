package com.lms.Backend.course.repository;

import com.lms.Backend.course.entity.CourseLesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseLessonRepository extends JpaRepository<CourseLesson, Long> {
    List<CourseLesson> findBySectionIdOrderByDisplayOrderAsc(Long sectionId);
    List<CourseLesson> findBySection_Course_IdOrderBySection_DisplayOrderAscDisplayOrderAsc(Long courseId);
    long countBySection_Course_Id(Long courseId);
    long countBySection_Course_IdAndRequiredTrue(Long courseId);

    @Query("SELECT l FROM CourseLesson l JOIN FETCH l.section s JOIN FETCH s.course c WHERE l.id = :lessonId")
    Optional<CourseLesson> findByIdWithSectionAndCourse(@Param("lessonId") Long lessonId);
}

