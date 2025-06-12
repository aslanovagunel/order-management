package com.app.yolla.modules.auth.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.yolla.modules.auth.dto.AuthResponse;
import com.app.yolla.modules.auth.dto.LoginRequest;
import com.app.yolla.modules.auth.dto.OtpVerificationRequest;
import com.app.yolla.modules.auth.service.AuthService;
import com.app.yolla.shared.dto.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

/**
 * Authentication Controller Sinfi
 * <p>
 * Bu sinif giriş və qeydiyyat əlaqəli bütün HTTP sorğularını idarə edir.
 * OTP göndərmə, doğrulama və token yaratma əməliyyatlarını təmin edir.
 * <p>
 * Analogi: Bu sinif bir "qeydiyyat masası" kimidir - gələnləri qarşılayır,
 * sənədlərini yoxlayır və sistemi girişi təmin edir.
 */
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    /**
     * OTP göndərmə endpoint-i
     * POST /auth/send-otp
     * <p>
     * Bu endpoint istifadəçinin telefon nömrəsinə OTP göndərir.
     * Əgər istifadəçi mövcuddursa giriş, yoxdursa qeydiyyat üçün.
     */
    @PostMapping("/send-otp")
    public ResponseEntity<ApiResponse<String>> sendOtp(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {

        logger.info("OTP göndərmə sorğusu: telefon={}", request.getPhoneNumber());

        try {
            // IP ünvanını əldə et
            String ipAddress = getClientIpAddress(httpRequest);

            String result = authService.sendOtp(request.getPhoneNumber(), ipAddress);

            ApiResponse<String> response = ApiResponse.success(
                    "OTP kodu telefon nömrənizə göndərildi",
                    result
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("OTP göndərmə xətası: telefon={}", request.getPhoneNumber(), e);

            ApiResponse<String> response = ApiResponse.error(
                    "OTP göndərilmədi: " + e.getMessage()
            );

            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * OTP doğrulama və giriş endpoint-i
     * POST /auth/verify-otp
     * <p>
     * Bu endpoint OTP kodunu doğrulayır və JWT token qaytarır.
     */
    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<AuthResponse>> verifyOtp(
            @Valid @RequestBody OtpVerificationRequest request) {

        logger.info("OTP doğrulama sorğusu: telefon={}", request.getPhoneNumber());

		try {
            AuthResponse authResponse = authService.verifyOtpAndLogin(
                    request.getPhoneNumber(),
                    request.getOtpCode()
            );

            ApiResponse<AuthResponse> response = ApiResponse.success(
					"Uğurla giriş etdiniz", 
                    authResponse
            );

            return ResponseEntity.ok(response);

		} catch (Exception e) {
			logger.error("OTP doğrulama xətası: telefon={}", request.getPhoneNumber(), e);

			ApiResponse<AuthResponse> response = ApiResponse.error("Giriş uğursuz: " + e.getMessage());

			return ResponseEntity.badRequest().body(response);
		}
    }

    /**
     * Token yeniləmə endpoint-i
     * POST /auth/refresh-token
     * <p>
     * Bu endpoint refresh token ilə yeni access token yaradır.
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(
            @RequestHeader("Authorization") String refreshToken) {

        logger.info("Token yeniləmə sorğusu");

        try {
            // "Bearer " prefiksini sil
            if (refreshToken.startsWith("Bearer ")) {
                refreshToken = refreshToken.substring(7);
            }

            AuthResponse authResponse = authService.refreshToken(refreshToken);

            ApiResponse<AuthResponse> response = ApiResponse.success(
                    "Token uğurla yeniləndi",
                    authResponse
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Token yeniləmə xətası: ", e);

            ApiResponse<AuthResponse> response = ApiResponse.error(
                    "Token yenilənmədi: " + e.getMessage()
            );

            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Çıxış endpoint-i
     * POST /auth/logout
     * <p>
     * Bu endpoint istifadəçini sistemdən çıxarır (token-ı blacklist edir).
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(
            @RequestHeader("Authorization") String token) {

        logger.info("Çıxış sorğusu");

        try {
            // "Bearer " prefiksini sil
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            authService.logout(token);

            ApiResponse<String> response = ApiResponse.success(
                    "Uğurla çıxış etdiniz",
                    null
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Çıxış xətası: ", e);

            ApiResponse<String> response = ApiResponse.error(
                    "Çıxış zamanı xəta baş verdi: " + e.getMessage()
            );

            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Token etibarlılığını yoxlayır
     * GET /auth/validate-token
     */
    @GetMapping("/validate-token")
    public ResponseEntity<ApiResponse<Boolean>> validateToken(
            @RequestHeader("Authorization") String token) {

        try {
            // "Bearer " prefiksini sil
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            boolean isValid = authService.validateToken(token);

            ApiResponse<Boolean> response = ApiResponse.success(
                    isValid ? "Token etibarlıdır" : "Token etibarsızdır",
                    isValid
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Token doğrulama xətası: ", e);

            ApiResponse<Boolean> response = ApiResponse.error(
                    "Token doğrulanmadı: " + e.getMessage()
            );

            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Profil məlumatlarını əldə edir (cari istifadəçi)
     * GET /auth/profile
     */
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<Object>> getCurrentUserProfile(
            @RequestHeader("Authorization") String token) {

        try {
            // "Bearer " prefiksini sil
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            Object userProfile = authService.getCurrentUserProfile(token);

            ApiResponse<Object> response = ApiResponse.success(
                    "Profil məlumatları",
                    userProfile
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Profil məlumatları əldə edilmədi: ", e);

            ApiResponse<Object> response = ApiResponse.error(
                    "Profil məlumatları əldə edilmədi: " + e.getMessage()
            );

            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Test endpoint-i - sistemin işləyib-işləmədiyini yoxlayır
     * GET /auth/health
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> healthCheck() {
        logger.debug("Auth health check sorğusu");

        ApiResponse<String> response = ApiResponse.success(
                "Authentication sistemi işləyir",
                "OK"
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Client IP ünvanını əldə edir
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedForHeader = request.getHeader("X-Forwarded-For");
        if (xForwardedForHeader == null) {
            return request.getRemoteAddr();
        } else {
            // X-Forwarded-For header-i vergüllə ayrılmış IP-lərin siyahısını ehtiva edə bilər
            return xForwardedForHeader.split(",")[0].trim();
        }
    }
}