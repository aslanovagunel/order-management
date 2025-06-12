package com.app.yolla.shared.exception;

/**
 * İş Məntiqi Exception Sinfi
 * <p>
 * Bu exception biznes qaydalarına zidd hərəkətlər zamanı atılır.
 * Məsələn: Stokda olmayan məhsul sifarişi
 */
class BusinessLogicException extends RuntimeException {

    private String errorCode;

    public BusinessLogicException(String message) {
        super(message);
    }

    public BusinessLogicException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public BusinessLogicException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getErrorCode() {
        return errorCode;
    }
}

