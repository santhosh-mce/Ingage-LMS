package com.lms.Backend.payment.controller;

import com.lms.Backend.admin.service.AdminDiscountService;
import com.lms.Backend.admin.service.AdminPaymentService;
import com.lms.Backend.user.entity.User;
import com.lms.Backend.user.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping
public class UserPaymentController {

    private final AdminPaymentService paymentService;
    private final AdminDiscountService discountService;
    private final UserRepository userRepository;

    public UserPaymentController(
        AdminPaymentService paymentService,
        AdminDiscountService discountService,
        UserRepository userRepository
    ) {
        this.paymentService = paymentService;
        this.discountService = discountService;
        this.userRepository = userRepository;
    }

    @PostMapping({"/orders/create", "/payments/create-order"})
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> createOrder(
        @RequestBody Map<String, Object> payload,
        Principal principal
    ) {
        User user = userRepository.findByEmailIgnoreCase(principal.getName())
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!payload.containsKey("courseId")) {
            throw new IllegalArgumentException("Course ID is required to create payment order.");
        }

        Long courseId = Long.parseLong(payload.get("courseId").toString());
        String couponCode = payload.get("couponCode") != null ? payload.get("couponCode").toString().trim() : null;

        Map<String, Object> order = paymentService.createOrder(user.getId(), courseId, couponCode);
        return ResponseEntity.ok(order);
    }

    @PostMapping("/payments/verify")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> verifyPayment(
        @RequestBody Map<String, Object> payload,
        Principal principal
    ) {
        User user = userRepository.findByEmailIgnoreCase(principal.getName())
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Support both snake_case (Razorpay direct) and camelCase
        String razorpayPaymentId = (String) (payload.containsKey("razorpay_payment_id")
            ? payload.get("razorpay_payment_id")
            : payload.get("razorpayPaymentId"));

        String razorpayOrderId = (String) (payload.containsKey("razorpay_order_id")
            ? payload.get("razorpay_order_id")
            : payload.get("razorpayOrderId"));

        String razorpaySignature = (String) (payload.containsKey("razorpay_signature")
            ? payload.get("razorpay_signature")
            : payload.get("razorpaySignature"));

        Map<String, Object> res = paymentService.verifyAndCompletePayment(user.getId(), razorpayPaymentId, razorpayOrderId, razorpaySignature);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/payments/my-payments")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Map<String, Object>>> getMyPayments(Principal principal) {
        User user = userRepository.findByEmailIgnoreCase(principal.getName())
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return ResponseEntity.ok(paymentService.getUserPayments(user.getId()));
    }

    @PostMapping("/discounts/validate")
    public ResponseEntity<Map<String, Object>> validateDiscount(
        @RequestBody Map<String, Object> payload,
        Principal principal
    ) {
        String couponCode = (String) payload.get("couponCode");
        Double amount = Double.parseDouble(payload.get("amount").toString());
        UUID userId = null;
        if (principal != null) {
            userId = userRepository.findByEmailIgnoreCase(principal.getName()).map(User::getId).orElse(null);
        }

        Map<String, Object> result = discountService.validateAndCalculateDiscount(couponCode, amount, userId);
        return ResponseEntity.ok(result);
    }
}
