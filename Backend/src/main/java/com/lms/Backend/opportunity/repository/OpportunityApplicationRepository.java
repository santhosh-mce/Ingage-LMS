package com.lms.Backend.opportunity.repository;

import com.lms.Backend.opportunity.entity.OpportunityApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OpportunityApplicationRepository extends JpaRepository<OpportunityApplication, Long> {

    List<OpportunityApplication> findByUserIdOrderByAppliedAtDesc(UUID userId);

    Optional<OpportunityApplication> findByUserIdAndOpportunityId(UUID userId, Long opportunityId);

    boolean existsByUserIdAndOpportunityId(UUID userId, Long opportunityId);

    long countByUserId(UUID userId);
}
