package com.lms.Backend.credential.repository;

import com.lms.Backend.credential.entity.CredentialModule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CredentialModuleRepository extends JpaRepository<CredentialModule, Long> {
    List<CredentialModule> findByCourseIdOrderByOrderIndexAsc(Long courseId);
}
