package com.lms.Backend.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record VerifyLoginOtpRequest(
    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email address")
    String email,

    @NotBlank(message = "OTP is required")
    @Pattern(regexp = "^\\d{6}$", message = "OTP must be a 6-digit numeric code")
    String otp
) {
}
