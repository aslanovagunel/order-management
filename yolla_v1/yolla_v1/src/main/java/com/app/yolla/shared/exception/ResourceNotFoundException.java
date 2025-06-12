package com.app.yolla.shared.exception;

/**
 * Resurs Tapılmadı Exception Sinfi
 * <p>
 * Bu exception məlumat tapılmadıqda atılır.
 * Məsələn: İstifadəçi ID ilə tapılmadıqda
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

