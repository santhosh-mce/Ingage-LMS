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

    Optional<Certificate> findByCertificateNumberIgnoreCaseOrVerificationCodeIgnoreCase(String certificateNumber, String verificationCode);

    Optional<Certificate> findByUserIdAndCourseId(UUID userId, Long courseId);

    Optional<Certificate> findByUserIdAndCareerId(UUID userId, Long careerId);

    Optional<Certificate> findByIdAndUserId(Long id, UUID userId);

    boolean existsByUserIdAndCourseId(UUID userId, Long courseId);

    boolean existsByUserIdAndCareerId(UUID userId, Long careerId);

    boolean existsByCertificateNumberIgnoreCase(String certificateNumber);

    List<Certificate> findByUserIdOrderByIssuedAtDesc(UUID userId);

    List<Certificate> findByCourseIdOrderByIssuedAtDesc(Long courseId);

    long countByCourseId(Long courseId);

    long countByStatus(CertificateStatus status);

    List<Certificate> findTop10ByOrderByIssuedAtDesc();

    List<Certificate> findAllByOrderByIssuedAtDesc();
}
