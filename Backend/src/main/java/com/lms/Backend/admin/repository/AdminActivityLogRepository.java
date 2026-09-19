package com.lms.Backend.admin.repository;

import com.lms.Backend.admin.entity.AdminActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminActivityLogRepository extends JpaRepository<AdminActivityLog, Long> {

    List<AdminActivityLog> findAllByOrderByCreatedAtDesc();

    List<AdminActivityLog> findTop100ByOrderByCreatedAtDesc();

    List<AdminActivityLog> findByEntityTypeOrderByCreatedAtDesc(String entityType);
}
