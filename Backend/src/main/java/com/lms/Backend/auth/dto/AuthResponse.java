package com.lms.Backend.auth.dto;

import java.util.UUID;

public record AuthResponse(
    String message,
    String token,
    UUID userId,
    String name,
    String email,
    String role
) {
}