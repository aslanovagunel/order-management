package com.app.yolla.modules.auth.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.yolla.shared.dto.ApiResponse;
import com.app.yolla.shared.security.JwtUtil;

/**
 * Sadə Auth Controller (Database olmadan)
 * <p>
 * Bu controller müvəqqəti olaraq database dependency olmadan işləyir.
 * Əsas funksionallığı test etmək üçün yaradılıb.
 */
@RestController
@RequestMapping("/simple-auth")
@CrossOrigin(origins = "*")
public class SimpleAuthController {

    private static final Logger logger = LoggerFactory.getLogger(SimpleAuthController.class);

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Test üçün sadə token yaratma endpoint-i
     * POST /auth/test-token
     */
    @PostMapping("/test-token")
    public ResponseEntity<ApiResponse<Map<String, Object>>> generateTestToken(
            @RequestParam(defaultValue = "+994501234567") String phoneNumber,
            @RequestParam(defaultValue = "CUSTOMER") String role) {

        logger.info("Test token yaradılır: telefon={}, rol={}", phoneNumber, role);

        try {
            // Test token yarad
            String accessToken = jwtUtil.generateToken(phoneNumber, role, 1L);
            String refreshToken = jwtUtil.generateRefreshToken(phoneNumber);

            Map<String, Object> response = new HashMap<>();
            response.put("accessToken", accessToken);
            response.put("refreshToken", refreshToken);
            response.put("tokenType", "Bearer");
            response.put("expiresIn", 86400); // 24 saat
            response.put("phoneNumber", phoneNumber);
            response.put("role", role);

            ApiResponse<Map<String, Object>> apiResponse = ApiResponse.success(
                    "Test token uğurla yaradıldı",
                    response
            );

            return ResponseEntity.ok(apiResponse);

        } catch (Exception e) {
            logger.error("Test token yaratma xətası: ", e);

            ApiResponse<Map<String, Object>> apiResponse = ApiResponse.error(
                    "Token yaradılmadı: " + e.getMessage()
            );

            return ResponseEntity.badRequest().body(apiResponse);
        }
    }

    /**
     * Token doğrulama endpoint-i
     * POST /auth/validate
     */
    @PostMapping("/validate")
    public ResponseEntity<ApiResponse<Map<String, Object>>> validateToken(
            @RequestHeader("Authorization") String authHeader) {

        logger.info("Token doğrulama sorğusu");

        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.badRequest().body(
                        ApiResponse.error("Authorization header yanlışdır")
                );
            }

            String token = authHeader.substring(7);

            if (jwtUtil.isTokenValid(token) && jwtUtil.isAccessToken(token)) {
                Map<String, Object> tokenInfo = new HashMap<>();
                tokenInfo.put("phoneNumber", jwtUtil.getPhoneNumberFromToken(token));
                tokenInfo.put("role", jwtUtil.getRoleFromToken(token));
                tokenInfo.put("userId", jwtUtil.getUserIdFromToken(token));
                tokenInfo.put("valid", true);

                return ResponseEntity.ok(ApiResponse.success("Token etibarlıdır", tokenInfo));
            } else {
                return ResponseEntity.badRequest().body(
                        ApiResponse.error("Token etibarsızdır və ya vaxtı bitib")
                );
            }

        } catch (Exception e) {
            logger.error("Token doğrulama xətası: ", e);
            return ResponseEntity.badRequest().body(
                    ApiResponse.error("Token doğrulanmadı: " + e.getMessage())
            );
        }
    }

    /**
     * Health check endpoint-i
     * GET /auth/health
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> healthCheck() {
        logger.debug("Auth health check sorğusu");

        Map<String, Object> healthInfo = new HashMap<>();
        healthInfo.put("status", "UP");
        healthInfo.put("service", "auth");
        healthInfo.put("timestamp", System.currentTimeMillis());

        ApiResponse<String> response = ApiResponse.success(
                "Auth servisi işləyir",
                "OK"
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Test məlumatları endpoint-i
     * GET /auth/info
     */
    @GetMapping("/info")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("service", "Yolla Auth Service");
        info.put("version", "1.0.0");
        info.put("mode", "development");
        info.put("features", new String[]{"custom-jwt", "token-validation", "test-mode"});

        return ResponseEntity.ok(ApiResponse.success("Servis məlumatları", info));
    }
}