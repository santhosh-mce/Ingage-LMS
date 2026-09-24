package com.lms.Backend.opportunity.repository;

import com.lms.Backend.opportunity.entity.Opportunity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OpportunityRepository extends JpaRepository<Opportunity, Long> {

    List<Opportunity> findByActiveTrueAndPublishedTrueOrderByDisplayOrderAscCreatedAtDesc();

    List<Opportunity> findAllByOrderByDisplayOrderAscCreatedAtDesc();

    long countByActiveTrueAndPublishedTrue();

    @Query("SELECT COUNT(DISTINCT o.company) FROM Opportunity o WHERE o.active = true AND o.published = true")
    long countDistinctActivePublishedCompanies();

    @Query("SELECT o FROM Opportunity o WHERE o.active = true AND o.published = true " +
           "AND (:type IS NULL OR LOWER(o.type) = LOWER(:type)) " +
           "AND (:workMode IS NULL OR LOWER(o.workMode) = LOWER(:workMode)) " +
           "AND (:query IS NULL OR (" +
           "  LOWER(o.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "  LOWER(o.company) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "  LOWER(o.location) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "  LOWER(o.category) LIKE LOWER(CONCAT('%', :query, '%'))" +
           ")) " +
           "ORDER BY o.displayOrder ASC, o.createdAt DESC")
    List<Opportunity> searchPublicOpportunities(
        @Param("type") String type,
        @Param("workMode") String workMode,
        @Param("query") String query
    );
}
