package com.lms.Backend.payment.repository;

import com.lms.Backend.payment.entity.Payment;
import com.lms.Backend.payment.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByPaymentNumber(String paymentNumber);

    Optional<Payment> findByRazorpayPaymentId(String razorpayPaymentId);

    List<Payment> findByUserIdOrderByCreatedAtDesc(UUID userId);

    Optional<Payment> findFirstByUserIdAndCourseIdOrderByCreatedAtDesc(UUID userId, Long courseId);

    boolean existsByUserIdAndCourseIdAndPaymentStatus(UUID userId, Long courseId, PaymentStatus status);

    List<Payment> findByCourseIdOrderByCreatedAtDesc(Long courseId);

    List<Payment> findAllByOrderByCreatedAtDesc();

    long countByPaymentStatus(PaymentStatus status);

    long countByUserId(UUID userId);

    long countByCourseId(Long courseId);

    long countByCourseIdAndPaymentStatus(Long courseId, PaymentStatus status);

    @Query("SELECT COALESCE(SUM(p.finalAmount), 0.0) FROM Payment p WHERE p.paymentStatus = 'PAID'")
    Double getTotalRevenue();

    @Query("SELECT COALESCE(SUM(p.finalAmount), 0.0) FROM Payment p WHERE p.paymentStatus = 'PAID' AND p.createdAt >= :since")
    Double getRevenueSince(@Param("since") Instant since);

    @Query("SELECT COALESCE(SUM(p.finalAmount), 0.0) FROM Payment p WHERE p.paymentStatus = 'PAID' AND p.course.id = :courseId")
    Double getRevenueByCourseId(@Param("courseId") Long courseId);

    @Query("SELECT COALESCE(SUM(p.finalAmount), 0.0) FROM Payment p WHERE p.paymentStatus = 'PAID' AND p.user.id = :userId")
    Double getTotalSpentByUserId(@Param("userId") UUID userId);
}
