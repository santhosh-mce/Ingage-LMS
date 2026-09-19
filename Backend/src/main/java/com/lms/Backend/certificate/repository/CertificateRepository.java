package com.lms.Backend.certificate.repository;

import com.lms.Backend.certificate.entity.Certificate;
import com.lms.Backend.certificate.entity.CertificateStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {

    Optional<Certificate> findByVerificationCodeIgnoreCase(String verificationCode);

    Optional<Certificate> findByCertificateNumberIgnoreCase(String certificateNumber);

    Optional<Certificate> findByUserIdAndCourseId(UUID userId, Long courseId);

    boolean existsByUserIdAndCourseId(UUID userId, Long courseId);

    List<Certificate> findByUserIdOrderByIssuedAtDesc(UUID userId);

    List<Certificate> findByCourseIdOrderByIssuedAtDesc(Long courseId);

    long countByCourseId(Long courseId);

    long countByStatus(CertificateStatus status);

    List<Certificate> findAllByOrderByIssuedAtDesc();
}
