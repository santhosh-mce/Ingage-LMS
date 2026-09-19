package com.lms.Backend.admin.service;

import com.lms.Backend.course.entity.DiscountType;
import com.lms.Backend.discount.entity.Discount;
import com.lms.Backend.discount.entity.DiscountUsage;
import com.lms.Backend.discount.repository.DiscountRepository;
import com.lms.Backend.discount.repository.DiscountUsageRepository;
import com.lms.Backend.payment.entity.Order;
import com.lms.Backend.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class AdminDiscountService {

    private final DiscountRepository discountRepository;
    private final DiscountUsageRepository discountUsageRepository;

    public AdminDiscountService(DiscountRepository discountRepository, DiscountUsageRepository discountUsageRepository) {
        this.discountRepository = discountRepository;
        this.discountUsageRepository = discountUsageRepository;
    }

    public List<Map<String, Object>> getAllDiscounts() {
        List<Discount> discounts = discountRepository.findAllByOrderByCreatedAtDesc();
        List<Map<String, Object>> result = new ArrayList<>();

        Instant now = Instant.now();
        for (Discount d : discounts) {
            Map<String, Object> dto = new LinkedHashMap<>();
            dto.put("id", d.getId());
            dto.put("couponCode", d.getCouponCode());
            dto.put("discountType", d.getDiscountType().name());
            dto.put("discountValue", d.getDiscountValue());
            dto.put("startDate", d.getStartDate() != null ? d.getStartDate().toString() : null);
            dto.put("endDate", d.getEndDate() != null ? d.getEndDate().toString() : null);
            dto.put("usageLimit", d.getUsageLimit());
            dto.put("perUserLimit", d.getPerUserLimit());
            dto.put("minPurchaseAmount", d.getMinPurchaseAmount());
            dto.put("maxDiscount", d.getMaxDiscount());
            dto.put("active", d.isActive());
            dto.put("usedCount", d.getUsedCount());

            boolean expired = d.getEndDate() != null && d.getEndDate().isBefore(now);
            dto.put("expired", expired);
            dto.put("status", !d.isActive() ? "INACTIVE" : (expired ? "EXPIRED" : "ACTIVE"));

            Double totalDiscountGiven = discountUsageRepository.getTotalDiscountAmountByDiscountId(d.getId());
            Double rev = discountUsageRepository.getRevenueGeneratedByDiscountId(d.getId());
            dto.put("totalDiscountGiven", totalDiscountGiven != null ? totalDiscountGiven : 0.0);
            dto.put("revenueGenerated", rev != null ? rev : 0.0);
            dto.put("createdAt", d.getCreatedAt().toString());

            result.add(dto);
        }
        return result;
    }

    @Transactional
    public Discount createDiscount(Map<String, Object> payload) {
        String code = ((String) payload.get("couponCode")).trim().toUpperCase();
        if (discountRepository.existsByCouponCodeIgnoreCase(code)) {
            throw new IllegalArgumentException("Coupon code already exists: " + code);
        }

        String typeStr = (String) payload.getOrDefault("discountType", "PERCENTAGE");
        DiscountType type = DiscountType.valueOf(typeStr.toUpperCase());
        double value = Double.parseDouble(payload.get("discountValue").toString());

        Instant startDate = payload.containsKey("startDate") && payload.get("startDate") != null
            ? Instant.parse(payload.get("startDate").toString()) : Instant.now();
        Instant endDate = payload.containsKey("endDate") && payload.get("endDate") != null
            ? Instant.parse(payload.get("endDate").toString()) : null;

        int usageLimit = payload.containsKey("usageLimit") ? Integer.parseInt(payload.get("usageLimit").toString()) : 100;
        int perUserLimit = payload.containsKey("perUserLimit") ? Integer.parseInt(payload.get("perUserLimit").toString()) : 1;
        double minPurchase = payload.containsKey("minPurchaseAmount") ? Double.parseDouble(payload.get("minPurchaseAmount").toString()) : 0.0;
        Double maxDiscount = payload.containsKey("maxDiscount") && payload.get("maxDiscount") != null
            ? Double.parseDouble(payload.get("maxDiscount").toString()) : null;
        boolean active = !payload.containsKey("active") || Boolean.parseBoolean(payload.get("active").toString());

        Discount discount = new Discount(code, type, value, startDate, endDate, usageLimit, perUserLimit, minPurchase, maxDiscount, active);
        return discountRepository.save(discount);
    }

    @Transactional
    public Discount updateDiscount(Long id, Map<String, Object> payload) {
        Discount d = discountRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Discount not found: " + id));

        if (payload.containsKey("couponCode")) d.setCouponCode(((String) payload.get("couponCode")).trim().toUpperCase());
        if (payload.containsKey("discountType")) d.setDiscountType(DiscountType.valueOf(((String) payload.get("discountType")).toUpperCase()));
        if (payload.containsKey("discountValue")) d.setDiscountValue(Double.parseDouble(payload.get("discountValue").toString()));
        if (payload.containsKey("startDate")) d.setStartDate(Instant.parse(payload.get("startDate").toString()));
        if (payload.containsKey("endDate")) d.setEndDate(payload.get("endDate") != null ? Instant.parse(payload.get("endDate").toString()) : null);
        if (payload.containsKey("usageLimit")) d.setUsageLimit(Integer.parseInt(payload.get("usageLimit").toString()));
        if (payload.containsKey("perUserLimit")) d.setPerUserLimit(Integer.parseInt(payload.get("perUserLimit").toString()));
        if (payload.containsKey("minPurchaseAmount")) d.setMinPurchaseAmount(Double.parseDouble(payload.get("minPurchaseAmount").toString()));
        if (payload.containsKey("maxDiscount")) d.setMaxDiscount(payload.get("maxDiscount") != null ? Double.parseDouble(payload.get("maxDiscount").toString()) : null);
        if (payload.containsKey("active")) d.setActive(Boolean.parseBoolean(payload.get("active").toString()));

        return discountRepository.save(d);
    }

    @Transactional
    public void toggleDiscountStatus(Long id, boolean active) {
        Discount d = discountRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Discount not found: " + id));
        d.setActive(active);
        discountRepository.save(d);
    }

    @Transactional
    public void deleteDiscount(Long id) {
        discountRepository.deleteById(id);
    }

    public Map<String, Object> getDiscountAnalytics() {
        long totalCoupons = discountRepository.count();
        long activeCoupons = discountRepository.countByActiveTrue();
        long expiredCoupons = discountRepository.countExpiredDiscounts();
        long totalUses = discountUsageRepository.count();
        Double totalDiscountGiven = discountUsageRepository.getTotalDiscountAmountGiven();

        List<Discount> all = discountRepository.findAll();
        double totalRev = 0.0;
        for (Discount d : all) {
            Double r = discountUsageRepository.getRevenueGeneratedByDiscountId(d.getId());
            if (r != null) totalRev += r;
        }

        Map<String, Object> analytics = new LinkedHashMap<>();
        analytics.put("totalCoupons", totalCoupons);
        analytics.put("activeCoupons", activeCoupons);
        analytics.put("expiredCoupons", expiredCoupons);
        analytics.put("totalUses", totalUses);
        analytics.put("totalDiscountGiven", totalDiscountGiven != null ? totalDiscountGiven : 0.0);
        analytics.put("revenueGenerated", totalRev);
        return analytics;
    }

    public Map<String, Object> validateAndCalculateDiscount(String couponCode, Double originalAmount, UUID userId) {
        if (couponCode == null || couponCode.isBlank()) {
            return Map.of("valid", false, "message", "Coupon code is empty");
        }

        Optional<Discount> opt = discountRepository.findByCouponCodeIgnoreCase(couponCode.trim());
        if (opt.isEmpty()) {
            return Map.of("valid", false, "message", "Invalid coupon code");
        }

        Discount d = opt.get();
        if (!d.isActive()) {
            return Map.of("valid", false, "message", "This coupon is currently inactive");
        }

        Instant now = Instant.now();
        if (d.getStartDate() != null && now.isBefore(d.getStartDate())) {
            return Map.of("valid", false, "message", "This coupon has not started yet");
        }
        if (d.getEndDate() != null && now.isAfter(d.getEndDate())) {
            return Map.of("valid", false, "message", "This coupon has expired");
        }

        if (d.getUsageLimit() != null && d.getUsedCount() >= d.getUsageLimit()) {
            return Map.of("valid", false, "message", "This coupon has reached its total usage limit");
        }

        if (userId != null && d.getPerUserLimit() != null) {
            long userUsed = discountUsageRepository.countByDiscountIdAndUserId(d.getId(), userId);
            if (userUsed >= d.getPerUserLimit()) {
                return Map.of("valid", false, "message", "You have already reached the redemption limit for this coupon");
            }
        }

        if (d.getMinPurchaseAmount() != null && originalAmount < d.getMinPurchaseAmount()) {
            return Map.of("valid", false, "message", "Minimum purchase of ₹" + d.getMinPurchaseAmount() + " required to use this coupon");
        }

        double discountAmount = 0.0;
        if (d.getDiscountType() == DiscountType.PERCENTAGE) {
            discountAmount = originalAmount * (d.getDiscountValue() / 100.0);
            if (d.getMaxDiscount() != null && discountAmount > d.getMaxDiscount()) {
                discountAmount = d.getMaxDiscount();
            }
        } else {
            discountAmount = d.getDiscountValue();
        }

        discountAmount = Math.min(discountAmount, originalAmount);
        double finalAmount = Math.max(0.0, originalAmount - discountAmount);

        return Map.of(
            "valid", true,
            "couponCode", d.getCouponCode(),
            "discountType", d.getDiscountType().name(),
            "discountValue", d.getDiscountValue(),
            "discountAmount", Math.round(discountAmount * 100.0) / 100.0,
            "finalAmount", Math.round(finalAmount * 100.0) / 100.0,
            "discountId", d.getId()
        );
    }

    @Transactional
    public void recordUsage(Discount discount, User user, Order order, Double discountAmount) {
        DiscountUsage usage = new DiscountUsage(discount, user, order, discountAmount);
        discountUsageRepository.save(usage);
        discount.setUsedCount(discount.getUsedCount() + 1);
        discountRepository.save(discount);
    }
}
