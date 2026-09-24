package com.lms.Backend.project.repository;

import com.lms.Backend.project.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findByActiveTrueAndPublishedTrueOrderByDisplayOrderAscTitleAsc();

    List<Project> findAllByOrderByDisplayOrderAscTitleAsc();

    Optional<Project> findBySlug(String slug);

    boolean existsBySlug(String slug);

    List<Project> findByIndustryIgnoreCaseAndActiveTrueAndPublishedTrue(String industry);
}
