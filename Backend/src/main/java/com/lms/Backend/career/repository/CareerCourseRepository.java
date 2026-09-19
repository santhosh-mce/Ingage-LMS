package com.lms.Backend.career.repository;

import com.lms.Backend.career.entity.CareerCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CareerCourseRepository extends JpaRepository<CareerCourse, Long> {
    List<CareerCourse> findByCareerIdOrderByDisplayOrderAsc(Long careerId);
    long countByCareerId(Long careerId);
    boolean existsByCareerIdAndCourseId(Long careerId, Long courseId);
    void deleteByCareerId(Long careerId);
}
