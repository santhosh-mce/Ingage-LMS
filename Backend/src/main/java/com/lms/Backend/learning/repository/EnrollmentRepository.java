package com.lms.Backend.learning.repository;

import com.lms.Backend.learning.entity.Enrollment;
import com.lms.Backend.learning.entity.EnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    Optional<Enrollment> findByUserIdAndCourseId(UUID userId, Long courseId);

    boolean existsByUserIdAndCourseId(UUID userId, Long courseId);

    List<Enrollment> findByUserIdOrderByEnrolledAtDesc(UUID userId);

    List<Enrollment> findByCourseIdOrderByEnrolledAtDesc(Long courseId);

    long countByCourseId(Long courseId);

    long countByCourseIdAndStatus(Long courseId, EnrollmentStatus status);

    long countByStatus(EnrollmentStatus status);

    @Query("SELECT COUNT(DISTINCT e.user.id) FROM Enrollment e WHERE e.status = 'ACTIVE'")
    long countDistinctActiveLearners();

    @Query("SELECT AVG(e.progressPercentage) FROM Enrollment e WHERE e.course.id = :courseId")
    Double getAverageProgressByCourseId(@Param("courseId") Long courseId);
}
