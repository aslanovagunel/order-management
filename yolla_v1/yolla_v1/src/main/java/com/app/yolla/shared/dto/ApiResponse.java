package com.app.yolla.shared.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

/**
 * API Cavab DTO Sinfi
 * <p>
 * Bu sinif bütün API endpoint-lərindən gələn cavabları standartlaşdırır.
 * Hər cavabda uğur statusu, mesaj və məlumat olur.
 * <p>
 * Analogi: Bu sinif bir "məktub zərfi" kimidir - içərisində həm
 * məktub (data), həm də məktubun vəziyyəti haqqında məlumat var.
 */
@JsonInclude(JsonInclude.Include.NON_NULL) // null sahələri JSON-a daxil etmə
public class ApiResponse<T> {

    /**
     * Əməliyyatın uğurlu olub-olmadığını göstərir
     */
    private boolean success;

    /**
     * İstifadəçiyə göstəriləcək mesaj
     */
    private String message;

    /**
     * Əməliyyatın nəticəsi olaraq qaytarılan məlumat
     */
    private T data;

    /**
     * Cavabın yaradılma vaxtı
     */
    private LocalDateTime timestamp;

    /**
     * Xəta kodu (xəta zamanı)
     */
    private String errorCode;

    // Default konstruktor
    public ApiResponse() {
        this.timestamp = LocalDateTime.now();
    }

    // Uğurlu cavab üçün konstruktor
    public ApiResponse(boolean success, String message, T data) {
        this();
        this.success = success;
        this.message = message;
        this.data = data;
    }

    // Xətalı cavab üçün konstruktor
    public ApiResponse(boolean success, String message, T data, String errorCode) {
        this(success, message, data);
        this.errorCode = errorCode;
    }

    // Static metodlar - tez-tez istifadə olunan cavablar üçün

    /**
     * Uğurlu cavab yaradır
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    /**
     * Uğurlu cavab yaradır (məlumat olmadan)
     */
    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(true, message, null);
    }

    /**
     * Xətalı cavab yaradır
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }

    /**
     * Xətalı cavab yaradır (xəta kodu ilə)
     */
    public static <T> ApiResponse<T> error(String message, String errorCode) {
        return new ApiResponse<>(false, message, null, errorCode);
    }

    /**
     * Validation xətası üçün cavab
     */
    public static <T> ApiResponse<T> validationError(String message, T errors) {
        return new ApiResponse<>(false, message, errors, "VALIDATION_ERROR");
    }

    /**
     * Resurs tapılmadı xətası üçün cavab
     */
    public static <T> ApiResponse<T> notFound(String message) {
        return new ApiResponse<>(false, message, null, "NOT_FOUND");
    }

    /**
     * İcazə yoxdur xətası üçün cavab
     */
    public static <T> ApiResponse<T> forbidden(String message) {
        return new ApiResponse<>(false, message, null, "FORBIDDEN");
    }

    /**
     * Daxili server xətası üçün cavab
     */
    public static <T> ApiResponse<T> internalError(String message) {
        return new ApiResponse<>(false, message, null, "INTERNAL_ERROR");
    }

    // Getter və Setter metodları
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public String toString() {
        return "ApiResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", timestamp=" + timestamp +
                ", errorCode='" + errorCode + '\'' +
                '}';
    }
}