package com.lms.Backend.admin.service;

import com.lms.Backend.certificate.entity.Certificate;
import com.lms.Backend.certificate.repository.CertificateRepository;
import com.lms.Backend.learning.entity.Enrollment;
import com.lms.Backend.learning.entity.EnrollmentStatus;
import com.lms.Backend.learning.repository.EnrollmentRepository;
import com.lms.Backend.payment.entity.Payment;
import com.lms.Backend.payment.repository.PaymentRepository;
import com.lms.Backend.user.entity.User;
import com.lms.Backend.user.entity.UserRole;
import com.lms.Backend.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class AdminUserService {

    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final PaymentRepository paymentRepository;
    private final CertificateRepository certificateRepository;

    public AdminUserService(
        UserRepository userRepository,
        EnrollmentRepository enrollmentRepository,
        PaymentRepository paymentRepository,
        CertificateRepository certificateRepository
    ) {
        this.userRepository = userRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.paymentRepository = paymentRepository;
        this.certificateRepository = certificateRepository;
    }

    public List<Map<String, Object>> getAllUsers(String search) {
        List<User> users;
        if (search != null && !search.trim().isEmpty()) {
            users = userRepository.searchUsers(search.trim());
        } else {
            users = userRepository.findAllByOrderByCreatedAtDesc();
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (User u : users) {
            Map<String, Object> dto = new LinkedHashMap<>();
            dto.put("id", u.getId());
            dto.put("name", u.getName());
            dto.put("email", u.getEmail());
            dto.put("role", u.getRole().name());
            dto.put("phone", u.getPhone() != null ? u.getPhone() : "");
            dto.put("active", u.isActive());
            dto.put("status", u.isActive() ? "ACTIVE" : "INACTIVE");
            dto.put("registrationDate", u.getCreatedAt() != null ? u.getCreatedAt().toString() : "");
            dto.put("lastLogin", u.getLastLogin() != null ? u.getLastLogin().toString() : "");

            List<Enrollment> enrollments = enrollmentRepository.findByUserIdOrderByEnrolledAtDesc(u.getId());
            dto.put("enrolledCourses", enrollments.size());

            long completedCount = enrollments.stream().filter(e -> e.getStatus() == EnrollmentStatus.COMPLETED).count();
            dto.put("completedCourses", completedCount);

            long payCount = paymentRepository.countByUserId(u.getId());
            Double totalSpent = paymentRepository.getTotalSpentByUserId(u.getId());
            dto.put("paymentCount", payCount);
            dto.put("totalSpent", totalSpent != null ? totalSpent : 0.0);

            result.add(dto);
        }
        return result;
    }

    public Map<String, Object> getUserDetails(UUID userId) {
        User u = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        Map<String, Object> details = new LinkedHashMap<>();

        // Profile
        Map<String, Object> profile = new LinkedHashMap<>();
        profile.put("id", u.getId());
        profile.put("name", u.getName());
        profile.put("email", u.getEmail());
        profile.put("phone", u.getPhone() != null ? u.getPhone() : "");
        profile.put("role", u.getRole().name());
        profile.put("status", u.isActive() ? "ACTIVE" : "INACTIVE");
        profile.put("active", u.isActive());
        profile.put("createdDate", u.getCreatedAt() != null ? u.getCreatedAt().toString() : "");
        profile.put("lastLogin", u.getLastLogin() != null ? u.getLastLogin().toString() : "");
        profile.put("profileImage", u.getProfileImage());
        details.put("profile", profile);

        // Learning
        List<Enrollment> enrollments = enrollmentRepository.findByUserIdOrderByEnrolledAtDesc(u.getId());
        List<Map<String, Object>> learning = new ArrayList<>();
        int completedCount = 0;
        int inProgressCount = 0;
        double sumProgress = 0.0;

        for (Enrollment e : enrollments) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("enrollmentId", e.getId());
            item.put("courseId", e.getCourse().getId());
            item.put("courseTitle", e.getCourse().getTitle());
            item.put("status", e.getStatus().name());
            item.put("progress", e.getProgressPercentage());
            item.put("enrolledAt", e.getEnrolledAt().toString());
            item.put("completedAt", e.getCompletedAt() != null ? e.getCompletedAt().toString() : null);
            item.put("lastAccessedAt", e.getLastAccessedAt() != null ? e.getLastAccessedAt().toString() : null);

            if (e.getStatus() == EnrollmentStatus.COMPLETED) {
                completedCount++;
            } else {
                inProgressCount++;
            }
            sumProgress += e.getProgressPercentage();
            learning.add(item);
        }

        Map<String, Object> learningSummary = new LinkedHashMap<>();
        learningSummary.put("coursesEnrolled", enrollments.size());
        learningSummary.put("coursesInProgress", inProgressCount);
        learningSummary.put("coursesCompleted", completedCount);
        learningSummary.put("overallProgress", enrollments.isEmpty() ? 0.0 : Math.round((sumProgress / enrollments.size()) * 10.0) / 10.0);
        learningSummary.put("enrollments", learning);
        details.put("learning", learningSummary);

        // Payments
        List<Payment> payments = paymentRepository.findByUserIdOrderByCreatedAtDesc(u.getId());
        List<Map<String, Object>> paymentList = new ArrayList<>();
        for (Payment p : payments) {
            Map<String, Object> pay = new LinkedHashMap<>();
            pay.put("paymentId", p.getId());
            pay.put("paymentNumber", p.getPaymentNumber());
            pay.put("orderId", p.getOrder() != null ? p.getOrder().getOrderNumber() : "");
            pay.put("course", p.getCourse().getTitle());
            pay.put("amount", p.getAmount());
            pay.put("discount", p.getDiscount());
            pay.put("finalAmount", p.getFinalAmount());
            pay.put("paymentStatus", p.getPaymentStatus().name());
            pay.put("paymentDate", p.getCreatedAt().toString());
            paymentList.add(pay);
        }
        details.put("payments", paymentList);

        // Certificates
        List<Certificate> certs = certificateRepository.findByUserIdOrderByIssuedAtDesc(u.getId());
        List<Map<String, Object>> certList = new ArrayList<>();
        for (Certificate c : certs) {
            Map<String, Object> cert = new LinkedHashMap<>();
            cert.put("certificateId", c.getId());
            cert.put("certificateNumber", c.getCertificateNumber());
            cert.put("verificationCode", c.getVerificationCode());
            cert.put("course", c.getCourse().getTitle());
            cert.put("issueDate", c.getIssuedAt().toString());
            cert.put("status", c.getStatus().name());
            cert.put("downloadUrl", "/api/certificates/verify/" + c.getVerificationCode() + "/download");
            certList.add(cert);
        }
        details.put("certificates", certList);

        return details;
    }

    @Transactional
    public void toggleUserStatus(UUID userId, boolean active) {
        User u = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        u.setActive(active);
        userRepository.save(u);
    }

    @Transactional
    public void updateUserRole(UUID userId, UserRole role) {
        User u = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        u.setRole(role);
        userRepository.save(u);
    }

    @Transactional
    public Map<String, Object> deleteOrDeactivateUser(UUID userId) {
        User u = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        long enrollments = enrollmentRepository.findByUserIdOrderByEnrolledAtDesc(userId).size();
        long payments = paymentRepository.countByUserId(userId);
        long certs = certificateRepository.findByUserIdOrderByIssuedAtDesc(userId).size();

        // Safety rule: If user has learning or payment history, soft-deactivate to preserve database integrity
        if (enrollments > 0 || payments > 0 || certs > 0) {
            u.setActive(false);
            userRepository.save(u);
            return Map.of("deactivated", true, "message", "User has active records. Account has been safely deactivated instead of deleted.");
        } else {
            userRepository.delete(u);
            return Map.of("deleted", true, "message", "User account removed.");
        }
    }
}
