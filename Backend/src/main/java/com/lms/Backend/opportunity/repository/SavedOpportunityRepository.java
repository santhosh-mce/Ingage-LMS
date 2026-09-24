package com.lms.Backend.opportunity.repository;

import com.lms.Backend.opportunity.entity.SavedOpportunity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SavedOpportunityRepository extends JpaRepository<SavedOpportunity, Long> {

    List<SavedOpportunity> findByUserIdOrderBySavedAtDesc(UUID userId);

    Optional<SavedOpportunity> findByUserIdAndOpportunityId(UUID userId, Long opportunityId);

    boolean existsByUserIdAndOpportunityId(UUID userId, Long opportunityId);

    long countByUserId(UUID userId);

    void deleteByUserIdAndOpportunityId(UUID userId, Long opportunityId);
}
