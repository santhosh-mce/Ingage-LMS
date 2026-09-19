package com.lms.Backend.career.repository;

import com.lms.Backend.career.entity.CareerProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CareerProjectRepository extends JpaRepository<CareerProject, Long> {
    List<CareerProject> findByCareerIdOrderByDisplayOrderAsc(Long careerId);
    long countByCareerId(Long careerId);
}
