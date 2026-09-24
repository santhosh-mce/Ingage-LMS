package com.lms.Backend.credential.repository;

import com.lms.Backend.credential.entity.CredentialEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CredentialEnrollmentRepository extends JpaRepository<CredentialEnrollment, Long> {

    Optional<CredentialEnrollment> findByUserIdAndCourseId(UUID userId, Long courseId);

    Optional<CredentialEnrollment> findByUserIdAndCourseSlug(UUID userId, String courseSlug);

    List<CredentialEnrollment> findByUserIdOrderByLastAccessedAtDesc(UUID userId);

    boolean existsByUserIdAndCourseId(UUID userId, Long courseId);
}
