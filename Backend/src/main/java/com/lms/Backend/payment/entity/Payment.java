package com.lms.Backend.payment.entity;

import com.lms.Backend.career.entity.Career;
import com.lms.Backend.course.entity.Course;
import com.lms.Backend.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "payment_number", nullable = false, unique = true, length = 100)
    private String paymentNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id", nullable = true)
    private Course course;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "career_id", nullable = true)
    private Career career;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type", length = 30)
    private PaymentItemType paymentType = PaymentItemType.COURSE;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private Double discount = 0.0;

    @Column(name = "final_amount", nullable = false)
    private Double finalAmount;

    @Column(name = "payment_method", length = 50)
    private String paymentMethod = "Razorpay";

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 30)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Column(name = "razorpay_payment_id", length = 100)
    private String razorpayPaymentId;

    @Column(name = "razorpay_order_id", length = 100)
    private String razorpayOrderId;

    @Column(name = "razorpay_signature", length = 255)
    private String razorpaySignature;

    @Column(length = 10)
    private String currency = "INR";

    @Column(name = "signature_verified", nullable = false)
    private boolean signatureVerified = false;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    public Payment() {}

    public Payment(String paymentNumber, Order order, User user, Course course, Double amount, Double discount, Double finalAmount) {
        this.paymentNumber = paymentNumber;
        this.order = order;
        this.user = user;
        this.course = course;
        this.paymentType = PaymentItemType.COURSE;
        this.amount = amount;
        this.discount = discount != null ? discount : 0.0;
        this.finalAmount = finalAmount;
        this.paymentStatus = PaymentStatus.PENDING;
    }

    public Payment(String paymentNumber, Order order, User user, Career career, Double amount, Double discount, Double finalAmount) {
        this.paymentNumber = paymentNumber;
        this.order = order;
        this.user = user;
        this.career = career;
        this.paymentType = PaymentItemType.CAREER_PATH;
        this.amount = amount;
        this.discount = discount != null ? discount : 0.0;
        this.finalAmount = finalAmount;
        this.paymentStatus = PaymentStatus.PENDING;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPaymentNumber() { return paymentNumber; }
    public void setPaymentNumber(String paymentNumber) { this.paymentNumber = paymentNumber; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }

    public Career getCareer() { return career; }
    public void setCareer(Career career) { this.career = career; }

    public PaymentItemType getPaymentType() { return paymentType != null ? paymentType : PaymentItemType.COURSE; }
    public void setPaymentType(PaymentItemType paymentType) { this.paymentType = paymentType; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public Double getDiscount() { return discount; }
    public void setDiscount(Double discount) { this.discount = discount; }

    public Double getFinalAmount() { return finalAmount; }
    public void setFinalAmount(Double finalAmount) { this.finalAmount = finalAmount; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getRazorpayPaymentId() { return razorpayPaymentId; }
    public void setRazorpayPaymentId(String razorpayPaymentId) { this.razorpayPaymentId = razorpayPaymentId; }

    public String getRazorpayOrderId() { return razorpayOrderId; }
    public void setRazorpayOrderId(String razorpayOrderId) { this.razorpayOrderId = razorpayOrderId; }

    public String getRazorpaySignature() { return razorpaySignature; }
    public void setRazorpaySignature(String razorpaySignature) { this.razorpaySignature = razorpaySignature; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public boolean isSignatureVerified() { return signatureVerified; }
    public void setSignatureVerified(boolean signatureVerified) { this.signatureVerified = signatureVerified; }

    public Instant getCreatedAt() { return createdAt; }
}
