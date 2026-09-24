package com.lms.Backend.user.repository;

import com.lms.Backend.user.entity.UserProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserProjectRepository extends JpaRepository<UserProject, Long> {

    List<UserProject> findByUserIdOrderByCreatedAtDesc(UUID userId);

    Optional<UserProject> findByIdAndUserId(Long id, UUID userId);

    void deleteByIdAndUserId(Long id, UUID userId);

    long countByUserId(UUID userId);

    long countByUserIdAndStatusIgnoreCase(UUID userId, String status);
}
