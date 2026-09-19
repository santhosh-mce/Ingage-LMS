package com.lms.Backend.discount.repository;

import com.lms.Backend.discount.entity.DiscountUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DiscountUsageRepository extends JpaRepository<DiscountUsage, Long> {

    List<DiscountUsage> findByDiscountIdOrderByUsedAtDesc(Long discountId);

    long countByDiscountId(Long discountId);

    long countByDiscountIdAndUserId(Long discountId, UUID userId);

    @Query("SELECT COALESCE(SUM(du.discountAmount), 0.0) FROM DiscountUsage du")
    Double getTotalDiscountAmountGiven();

    @Query("SELECT COALESCE(SUM(du.discountAmount), 0.0) FROM DiscountUsage du WHERE du.discount.id = :discountId")
    Double getTotalDiscountAmountByDiscountId(@Param("discountId") Long discountId);

    @Query("SELECT COALESCE(SUM(du.order.finalAmount), 0.0) FROM DiscountUsage du WHERE du.discount.id = :discountId AND du.order IS NOT NULL")
    Double getRevenueGeneratedByDiscountId(@Param("discountId") Long discountId);
}
