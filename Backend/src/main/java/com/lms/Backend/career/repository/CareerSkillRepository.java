package com.lms.Backend.career.repository;

import com.lms.Backend.career.entity.CareerSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CareerSkillRepository extends JpaRepository<CareerSkill, Long> {
    List<CareerSkill> findByCareerIdOrderByDisplayOrderAsc(Long careerId);
    long countByCareerId(Long careerId);
}
