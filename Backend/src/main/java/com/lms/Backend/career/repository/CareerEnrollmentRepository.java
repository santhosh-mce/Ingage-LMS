package com.lms.Backend.career.repository;

import com.lms.Backend.career.entity.CareerEnrollment;
import com.lms.Backend.learning.entity.EnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CareerEnrollmentRepository extends JpaRepository<CareerEnrollment, Long> {

    Optional<CareerEnrollment> findByUserIdAndCareerId(UUID userId, Long careerId);

    boolean existsByUserIdAndCareerId(UUID userId, Long careerId);

    boolean existsByUserIdAndCareerIdAndStatus(UUID userId, Long careerId, EnrollmentStatus status);

    List<CareerEnrollment> findByUserId(UUID userId);

    List<CareerEnrollment> findByUserIdOrderByEnrolledAtDesc(UUID userId);

    List<CareerEnrollment> findByCareerId(Long careerId);

    long countByCareerId(Long careerId);
}
