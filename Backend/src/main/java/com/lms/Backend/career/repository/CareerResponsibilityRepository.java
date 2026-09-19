package com.lms.Backend.career.repository;

import com.lms.Backend.career.entity.CareerResponsibility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CareerResponsibilityRepository extends JpaRepository<CareerResponsibility, Long> {
    List<CareerResponsibility> findByCareerIdOrderByDisplayOrderAsc(Long careerId);
}
