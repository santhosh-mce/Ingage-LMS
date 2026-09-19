package com.lms.Backend.career.repository;

import com.lms.Backend.career.entity.CareerRoadmap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CareerRoadmapRepository extends JpaRepository<CareerRoadmap, Long> {
    List<CareerRoadmap> findByCareerIdOrderByDisplayOrderAsc(Long careerId);
}
