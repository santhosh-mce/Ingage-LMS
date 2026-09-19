package com.lms.Backend.common.exception;

public class EmailVerificationException extends RuntimeException {

    public enum ErrorCode {
        INVALID,
        EXPIRED,
        ALREADY_USED,
        MAX_ATTEMPTS_EXCEEDED,
        NOT_FOUND
    }

    private final ErrorCode errorCode;

    public EmailVerificationException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
