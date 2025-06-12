package com.app.yolla.shared.exception;

/**
 * Dublikat Resurs Exception Sinfi
 * <p>
 * Bu exception artıq mövcud olan məlumat təkrar əlavə edildikdə atılır.
 * Məsələn: Eyni telefon nömrəsi ilə qeydiyyat cəhdi
 */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }

    public DuplicateResourceException(String message, Throwable cause) {
        super(message, cause);
    }
}

