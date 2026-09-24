package com.lms.Backend.user.repository;

import com.lms.Backend.user.entity.UserEducation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserEducationRepository extends JpaRepository<UserEducation, Long> {

    List<UserEducation> findByUserIdOrderByGraduationYearDesc(UUID userId);

    List<UserEducation> findByUserIdOrderByCreatedAtDesc(UUID userId);

    Optional<UserEducation> findByIdAndUserId(Long id, UUID userId);

    void deleteByIdAndUserId(Long id, UUID userId);

    void deleteByUserId(UUID userId);

    long countByUserId(UUID userId);
}
