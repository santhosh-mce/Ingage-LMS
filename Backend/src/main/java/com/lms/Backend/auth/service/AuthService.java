package com.lms.Backend.auth.service;

import com.lms.Backend.auth.dto.AuthResponse;
import com.lms.Backend.auth.dto.LoginRequest;
import com.lms.Backend.auth.dto.RegisterRequest;
import com.lms.Backend.auth.dto.RequestLoginOtpRequest;
import com.lms.Backend.auth.dto.VerifyLoginOtpRequest;
import com.lms.Backend.auth.entity.OtpVerification;
import com.lms.Backend.auth.repository.OtpVerificationRepository;
import com.lms.Backend.auth.security.JwtService;
import com.lms.Backend.user.entity.AuthProvider;
import com.lms.Backend.user.entity.User;
import com.lms.Backend.user.entity.UserRole;
import com.lms.Backend.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final UserRepository userRepository;
    private final OtpVerificationRepository otpVerificationRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TransactionTemplate transactionTemplate;

    public AuthService(
        UserRepository userRepository,
        OtpVerificationRepository otpVerificationRepository,
        PasswordEncoder passwordEncoder,
        JwtService jwtService,
        PlatformTransactionManager transactionManager
    ) {
        this.userRepository = userRepository;
        this.otpVerificationRepository = otpVerificationRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    /**
     * Direct registration without OTP or email verification.
     * User account is created immediately with hashed password and active status.
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String normalizedEmail = request.email().trim().toLowerCase();
        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new IllegalArgumentException("An account with this email already exists");
        }

        String hashedPassword = passwordEncoder.encode(request.password());

        User user = new User();
        user.setName(request.name().trim());
        user.setEmail(normalizedEmail);
        user.setPasswordHash(hashedPassword);
        user.setRole(UserRole.STUDENT);
        user.setProvider(AuthProvider.LOCAL);
        user.setEmailVerified(true);
        user.setActive(true);
        user.setWelcomeEmailSent(false);

        User savedUser = userRepository.save(user);
        log.info("[REGISTER] User account created successfully for email: {}", normalizedEmail);

        return new AuthResponse(
            "Account created successfully. Please log in.",
            null,
            savedUser.getId(),
            savedUser.getName(),
            savedUser.getEmail(),
            savedUser.getRole().name()
        );
    }

    /**
     * Direct login with email and password.
     */
    @Transactional
    public AuthResponse login(LoginRequest request) {
        String normalizedEmail = request.email().trim().toLowerCase();
        User user = userRepository.findByEmailIgnoreCase(normalizedEmail)
            .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!user.isActive()) {
            throw new IllegalArgumentException("Your account is deactivated. Please contact support.");
        }

        if (user.getPasswordHash() == null || !passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        user.setLastLogin(Instant.now());
        userRepository.save(user);

        return new AuthResponse(
            "Login successful",
            jwtService.generateToken(user.getEmail()),
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getRole().name()
        );
    }

    /**
     * Non-SMTP OTP Request for login.
     * Generates a secure 6-digit numeric OTP and stores it with 5-minute expiry.
     */
    @Transactional
    public Map<String, String> requestLoginOtp(RequestLoginOtpRequest request) {
        String normalizedEmail = request.email().trim().toLowerCase();
        User user = userRepository.findByEmailIgnoreCase(normalizedEmail)
            .orElseThrow(() -> new IllegalArgumentException("No account found with this email address. Please sign up."));

        if (!user.isActive()) {
            throw new IllegalArgumentException("Your account is deactivated. Please contact support.");
        }

        // Invalidate prior unused LOGIN OTPs
        otpVerificationRepository.markAllUsedForEmail(normalizedEmail, "LOGIN");

        // Generate 6-digit numeric OTP
        String otp = String.format("%06d", SECURE_RANDOM.nextInt(1_000_000));
        String hashedOtp = passwordEncoder.encode(otp);

        OtpVerification record = new OtpVerification();
        record.setEmail(normalizedEmail);
        record.setOtpHash(hashedOtp);
        record.setPurpose("LOGIN");
        record.setExpiresAt(Instant.now().plus(5, ChronoUnit.MINUTES));
        record.setAttemptCount(0);
        record.setUsed(false);
        record.setCreatedAt(Instant.now());
        record.setLastResentAt(Instant.now());

        otpVerificationRepository.save(record);
        log.info("[LOGIN-OTP] 6-digit login OTP generated for email: {} (expires in 5 minutes). SMTP is disabled.", normalizedEmail);

        return Map.of(
            "message", "Verification code generated successfully. It will expire in 5 minutes.",
            "email", normalizedEmail,
            "otp", otp // Provided for non-SMTP local/testing verification
        );
    }

    /**
     * Non-SMTP OTP Verification for login.
     * Verifies the 6-digit code against database record and issues a JWT token.
     */
    @Transactional
    public AuthResponse verifyLoginOtp(VerifyLoginOtpRequest request) {
        String normalizedEmail = request.email().trim().toLowerCase();
        String submittedOtp = request.otp().trim();

        OtpVerification record = otpVerificationRepository
            .findTopByEmailIgnoreCaseAndPurposeAndUsedFalseOrderByCreatedAtDesc(normalizedEmail, "LOGIN")
            .orElseThrow(() -> new IllegalArgumentException("Verification code has expired or is invalid. Please request a new code."));

        if (record.isExpired()) {
            record.setUsed(true);
            otpVerificationRepository.save(record);
            throw new IllegalArgumentException("Verification code has expired. Please request a new code.");
        }

        if (record.getAttemptCount() >= 5) {
            record.setUsed(true);
            otpVerificationRepository.save(record);
            throw new IllegalArgumentException("Maximum verification attempts exceeded. Please request a new code.");
        }

        if (!passwordEncoder.matches(submittedOtp, record.getOtpHash())) {
            record.setAttemptCount(record.getAttemptCount() + 1);
            otpVerificationRepository.save(record);
            int remaining = 5 - record.getAttemptCount();
            if (remaining <= 0) {
                record.setUsed(true);
                otpVerificationRepository.save(record);
                throw new IllegalArgumentException("Too many incorrect attempts. Verification code has been invalidated.");
            }
            throw new IllegalArgumentException("Invalid verification code. " + remaining + " attempt(s) remaining.");
        }

        record.setUsed(true);
        otpVerificationRepository.save(record);

        User user = userRepository.findByEmailIgnoreCase(normalizedEmail)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setLastLogin(Instant.now());
        user.setEmailVerified(true);
        userRepository.save(user);

        return new AuthResponse(
            "Login successful",
            jwtService.generateToken(user.getEmail()),
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getRole().name()
        );
    }

    public User findOrCreateOAuthUser(
        AuthProvider provider,
        String providerId,
        String email,
        String name,
        String profileImage
    ) {
        String lockKey = (email != null && !email.isBlank())
            ? email.trim().toLowerCase().intern()
            : providerId.intern();

        synchronized (lockKey) {
            return transactionTemplate.execute(status -> {
                java.util.Optional<User> existingUserOpt = userRepository.findByProviderAndProviderId(provider, providerId);
                if (existingUserOpt.isEmpty()) {
                    existingUserOpt = userRepository.findByEmailIgnoreCase(email);
                }

                User user;
                if (existingUserOpt.isPresent()) {
                    user = existingUserOpt.get();
                    user.setProvider(provider);
                    user.setProviderId(providerId);
                    user.setEmailVerified(true);
                    user.setLastLogin(Instant.now());
                    if (name != null && !name.isBlank()) {
                        user.setName(name);
                    }
                    if (user.getRole() == null) {
                        user.setRole(UserRole.STUDENT);
                    }
                    // IMPORTANT: Do NOT modify existingUser.profileImage
                } else {
                    user = new User();
                    user.setName(name);
                    user.setEmail(email);
                    user.setProvider(provider);
                    user.setProviderId(providerId);
                    user.setProfileImage(profileImage);
                    user.setEmailVerified(true);
                    user.setRole(UserRole.STUDENT);
                    user.setLastLogin(Instant.now());
                }

                return userRepository.saveAndFlush(user);
            });
        }
    }
}
