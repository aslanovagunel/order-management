package com.app.yolla.shared.exception;

import com.app.yolla.shared.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Global Exception Handler
 * <p>
 * Bu sinif bütün sistemdə baş verən xətaları tutub, müvafiq HTTP cavabları yaradır.
 * İstifadəçilərə anlaşılan mesajlar göstərir və daxili xətaları gizlədir.
 * <p>
 * Analogi: Bu sinif bir "böhran meneceri" kimidir - hər növ problemə
 * düzgün cavab verir və sakit hal saxlayır.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Resurs tapılmadı xətaları üçün
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFound(ResourceNotFoundException ex) {
        logger.warn("Resurs tapılmadı: {}", ex.getMessage());

        ApiResponse<Object> response = ApiResponse.notFound(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * Dublikat resurs xətaları üçün
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponse<Object>> handleDuplicateResource(DuplicateResourceException ex) {
        logger.warn("Dublikat resurs: {}", ex.getMessage());

        ApiResponse<Object> response = ApiResponse.error(ex.getMessage(), "DUPLICATE_RESOURCE");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    /**
     * OTP xətaları üçün
     */
    @ExceptionHandler(OtpException.class)
    public ResponseEntity<ApiResponse<Object>> handleOtpException(OtpException ex) {
        logger.warn("OTP xətası: {} - {}", ex.getErrorType(), ex.getMessage());

        ApiResponse<Object> response = ApiResponse.error(ex.getMessage(), ex.getErrorType());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Biznes məntiqi xətaları üçün
     */
    @ExceptionHandler(BusinessLogicException.class)
    public ResponseEntity<ApiResponse<Object>> handleBusinessLogic(BusinessLogicException ex) {
        logger.warn("Biznes məntiqi xətası: {}", ex.getMessage());

        ApiResponse<Object> response = ApiResponse.error(ex.getMessage(), ex.getErrorCode());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Səlahiyyət yoxdur xətaları üçün
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<Object>> handleUnauthorized(UnauthorizedException ex) {
        logger.warn("Səlahiyyət xətası: {}", ex.getMessage());

        ApiResponse<Object> response = ApiResponse.forbidden(ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    /**
     * Validation xətaları üçün
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        logger.warn("Validation xətası: {}", ex.getMessage());

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ApiResponse<Map<String, String>> response = ApiResponse.validationError(
                "Göndərilən məlumatlar düzgün deyil",
                errors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Ümumi xətalar üçün (gözlənilməz xətalar)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception ex) {
        logger.error("Gözlənilməz xəta: ", ex);

        // Məhsul mühitində daxili xəta detallarını göstərmirik
        String message = "Daxili server xətası baş verdi. Dəstək ilə əlaqə saxlayın";

        ApiResponse<Object> response = ApiResponse.internalError(message);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * İllegal Argument xətaları üçün
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgument(IllegalArgumentException ex) {
        logger.warn("Yanlış parametr: {}", ex.getMessage());

        ApiResponse<Object> response = ApiResponse.error(
                "Yanlış parametr: " + ex.getMessage(),
                "INVALID_ARGUMENT"
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Null Pointer xətaları üçün
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ApiResponse<Object>> handleNullPointer(NullPointerException ex) {
        logger.error("Null pointer xətası: ", ex);

        ApiResponse<Object> response = ApiResponse.internalError(
                "Daxili xəta baş verdi. Dəstək ilə əlaqə saxlayın"
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}