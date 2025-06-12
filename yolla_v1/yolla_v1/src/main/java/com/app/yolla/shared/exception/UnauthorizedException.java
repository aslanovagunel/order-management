package com.app.yolla.shared.exception;

/**
 * Səlahiyyət Yoxdur Exception Sinfi
 * <p>
 * Bu exception istifadəçinin hərəkət etmək səlahiyyəti olmadıqda atılır.
 */
class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}

