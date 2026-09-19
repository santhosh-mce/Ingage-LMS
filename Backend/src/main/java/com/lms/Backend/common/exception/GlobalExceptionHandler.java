package com.lms.Backend.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailNotVerifiedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, Object> handleEmailNotVerified(EmailNotVerifiedException exception) {
        return Map.of(
            "timestamp", Instant.now(),
            "status", 403,
            "error", exception.getMessage(),
            "email", exception.getEmail(),
            "emailVerified", false
        );
    }

    @ExceptionHandler(EmailVerificationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleEmailVerificationError(EmailVerificationException exception) {
        return Map.of(
            "timestamp", Instant.now(),
            "status", 400,
            "errorCode", exception.getErrorCode().name(),
            "error", exception.getMessage()
        );
    }

    @ExceptionHandler(PasswordResetException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handlePasswordResetError(PasswordResetException exception) {
        return Map.of(
            "timestamp", Instant.now(),
            "status", 400,
            "errorCode", exception.getErrorCode().name(),
            "error", exception.getMessage()
        );
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handleResourceNotFound(ResourceNotFoundException exception) {
        return Map.of("timestamp", Instant.now(), "status", 404, "error", exception.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleBadRequest(IllegalArgumentException exception) {
        return Map.of("timestamp", Instant.now(), "status", 400, "error", exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleValidationError(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
            .findFirst()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .orElse("Request validation failed");
        return Map.of("timestamp", Instant.now(), "status", 400, "error", message);
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, Object> handleAccessDenied(org.springframework.security.access.AccessDeniedException exception) {
        return Map.of("timestamp", Instant.now(), "status", 403, "error", exception.getMessage());
    }

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Object> handleUnexpectedError(Exception exception) {
        log.error("[GlobalExceptionHandler] Unexpected error", exception);
        return Map.of("timestamp", Instant.now(), "status", 500, "error", exception.getMessage() != null ? exception.getMessage() : "Unexpected server error");
    }
}