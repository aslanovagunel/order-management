package com.app.yolla.shared.exception;

/**
 * OTP Exception Sinfi
 * <p>
 * OTP əlaqəli xətalar üçün xüsusi exception
 */
public class OtpException extends RuntimeException {

    private final String errorType;

    public OtpException(String message, String errorType) {
        super(message);
        this.errorType = errorType;
    }

    public String getErrorType() {
        return errorType;
    }
}

