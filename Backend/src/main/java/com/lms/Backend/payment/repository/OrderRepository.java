package com.lms.Backend.payment.repository;

import com.lms.Backend.payment.entity.Order;
import com.lms.Backend.payment.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderNumber(String orderNumber);

    Optional<Order> findByRazorpayOrderId(String razorpayOrderId);

    List<Order> findByUserIdOrderByCreatedAtDesc(UUID userId);

    Optional<Order> findFirstByUserIdAndCourseIdOrderByCreatedAtDesc(UUID userId, Long courseId);

    Optional<Order> findFirstByUserIdAndCareerIdOrderByCreatedAtDesc(UUID userId, Long careerId);

    List<Order> findByUserIdAndCourseId(UUID userId, Long courseId);

    List<Order> findByUserIdAndCareerId(UUID userId, Long careerId);

    List<Order> findAllByOrderByCreatedAtDesc();

    long countByStatus(OrderStatus status);
}
