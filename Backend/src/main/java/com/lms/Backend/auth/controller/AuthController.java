package com.lms.Backend.auth.controller;

import com.lms.Backend.auth.dto.AuthResponse;
import com.lms.Backend.auth.dto.LoginRequest;
import com.lms.Backend.auth.dto.RegisterRequest;
import com.lms.Backend.auth.dto.RequestLoginOtpRequest;
import com.lms.Backend.auth.dto.VerifyLoginOtpRequest;
import com.lms.Backend.auth.service.AuthService;
import com.lms.Backend.user.entity.User;
import com.lms.Backend.user.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    public AuthController(AuthService authService, UserRepository userRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/login/request-otp")
    public ResponseEntity<Map<String, String>> requestLoginOtp(@Valid @RequestBody RequestLoginOtpRequest request) {
        return ResponseEntity.ok(authService.requestLoginOtp(request));
    }

    @PostMapping("/login/verify-otp")
    public ResponseEntity<AuthResponse> verifyLoginOtp(@Valid @RequestBody VerifyLoginOtpRequest request) {
        return ResponseEntity.ok(authService.verifyLoginOtp(request));
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, String>> currentUser(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User user = userRepository.findByEmailIgnoreCase(authentication.getName())
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return ResponseEntity.ok(Map.of(
            "message", "Authenticated user",
            "userId", user.getId().toString(),
            "name", user.getName(),
            "email", user.getEmail(),
            "role", user.getRole().name()
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("AUTH_TOKEN", "");
        cookie.setHttpOnly(true);
        cookie.setPath("/api");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return ResponseEntity.noContent().build();
    }
}