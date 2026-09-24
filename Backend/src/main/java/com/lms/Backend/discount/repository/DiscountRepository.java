package com.lms.Backend.discount.repository;

import com.lms.Backend.discount.entity.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {

    Optional<Discount> findByCouponCodeIgnoreCase(String couponCode);

    boolean existsByCouponCodeIgnoreCase(String couponCode);

    List<Discount> findAllByOrderByCreatedAtDesc();

    long countByActiveTrue();

    @Query("SELECT COUNT(d) FROM Discount d WHERE d.endDate IS NOT NULL AND d.endDate < CURRENT_TIMESTAMP")
    long countExpiredDiscounts();
}
