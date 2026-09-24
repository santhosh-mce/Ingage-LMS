package com.lms.Backend.common.exception;

public class PasswordResetException extends RuntimeException {

    public enum ErrorCode {
        INVALID,
        EXPIRED,
        ALREADY_USED
    }

    private final ErrorCode errorCode;

    public PasswordResetException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
