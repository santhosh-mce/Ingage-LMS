package com.lms.Backend.user.repository;

import com.lms.Backend.user.entity.UserSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserSkillRepository extends JpaRepository<UserSkill, Long> {

    List<UserSkill> findByUserIdOrderByNameAsc(UUID userId);

    List<UserSkill> findByUserIdAndCategoryOrderByNameAsc(UUID userId, String category);

    Optional<UserSkill> findByIdAndUserId(Long id, UUID userId);

    boolean existsByUserIdAndNameIgnoreCase(UUID userId, String name);

    void deleteByIdAndUserId(Long id, UUID userId);

    void deleteByUserId(UUID userId);

    long countByUserId(UUID userId);
}
