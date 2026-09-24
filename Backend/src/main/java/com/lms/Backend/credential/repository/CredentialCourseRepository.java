package com.lms.Backend.credential.repository;

import com.lms.Backend.credential.entity.CredentialCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CredentialCourseRepository extends JpaRepository<CredentialCourse, Long> {

    Optional<CredentialCourse> findBySlug(String slug);

    List<CredentialCourse> findByPublishedTrueOrderByFeaturedDescCreatedAtDesc();

    List<CredentialCourse> findByPublishedTrueAndFeaturedTrue();

    List<CredentialCourse> findByPublishedTrueAndCategoryIgnoreCase(String category);

    List<CredentialCourse> findByPublishedTrueAndCareerSlug(String careerSlug);

    @Query("SELECT DISTINCT c.category FROM CredentialCourse c WHERE c.published = true")
    List<String> findDistinctCategories();

    @Query("SELECT c FROM CredentialCourse c WHERE c.published = true AND " +
           "(LOWER(c.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(c.category) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(c.credentialName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(c.description) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<CredentialCourse> searchCourses(@Param("query") String query);
}
