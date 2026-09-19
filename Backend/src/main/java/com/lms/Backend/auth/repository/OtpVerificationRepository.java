package com.lms.Backend.auth.repository;

import com.lms.Backend.auth.entity.OtpVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OtpVerificationRepository extends JpaRepository<OtpVerification, UUID> {

    Optional<OtpVerification> findTopByEmailIgnoreCaseAndPurposeAndUsedFalseOrderByCreatedAtDesc(String email, String purpose);

    @Modifying
    @Query("UPDATE OtpVerification o SET o.used = true WHERE LOWER(o.email) = LOWER(:email) AND o.purpose = :purpose")
    void markAllUsedForEmail(@Param("email") String email, @Param("purpose") String purpose);
}
