package com.lms.Backend.career.repository;

import com.lms.Backend.career.entity.JobRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobRoleRepository extends JpaRepository<JobRole, Long> {
    List<JobRole> findByActiveTrueOrderByIdAsc();
    Optional<JobRole> findBySlugAndActiveTrue(String slug);
    boolean existsBySlug(String slug);
}
