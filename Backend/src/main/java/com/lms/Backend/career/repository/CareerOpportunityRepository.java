package com.lms.Backend.career.repository;

import com.lms.Backend.career.entity.CareerOpportunity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CareerOpportunityRepository extends JpaRepository<CareerOpportunity, Long> {
    List<CareerOpportunity> findByCareerIdOrderByDisplayOrderAsc(Long careerId);
    long countByCareerId(Long careerId);
}
