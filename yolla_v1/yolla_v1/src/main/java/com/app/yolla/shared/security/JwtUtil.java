package com.app.yolla.shared.security;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * Sadə JWT Utility Sinfi (Heç bir external library olmadan)
 * <p>
 * Bu versiya custom token system istifadə edir.
 * JWT library əvəzinə sadə, təhlükəsiz token yaradır.
 */
@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${jwt.secret:yolla_default_secret_key_2024}")
    private String jwtSecret;

    @Value("${jwt.expiration:86400000}")
    private long jwtExpirationMs;

    /**
     * İstifadəçi üçün token yaradır
     */
	public String generateToken(String phoneNumber, String role, UUID userId) {
        logger.debug("Token yaradılır: telefon={}, rol={}", phoneNumber, role);

        long expirationTime = System.currentTimeMillis() + jwtExpirationMs;
        String data = phoneNumber + "|" + role + "|" + userId + "|" + expirationTime;
        String signature = createSignature(data);
        String token = data + "|" + signature;

        return Base64.getEncoder().encodeToString(token.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Refresh token yaradır
     */
    public String generateRefreshToken(String phoneNumber) {
        logger.debug("Refresh token yaradılır: telefon={}", phoneNumber);

        long expirationTime = System.currentTimeMillis() + (jwtExpirationMs * 7); // 7 gün
        String data = phoneNumber + "|REFRESH|0|" + expirationTime;
        String signature = createSignature(data);
        String token = data + "|" + signature;

        return Base64.getEncoder().encodeToString(token.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Token-dan telefon nömrəsini çıxarır
     */
    public String getPhoneNumberFromToken(String token) {
        try {
            String[] parts = decodeAndSplitToken(token);
            return parts[0];
        } catch (Exception e) {
            logger.error("Token-dan telefon çıxarılarkən xəta: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Token-dan rol məlumatını çıxarır
     */
    public String getRoleFromToken(String token) {
        try {
            String[] parts = decodeAndSplitToken(token);
            return parts[1];
        } catch (Exception e) {
            logger.error("Token-dan rol çıxarılarkən xəta: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Token-dan istifadəçi ID-sini çıxarır
     */
    public Long getUserIdFromToken(String token) {
        try {
            String[] parts = decodeAndSplitToken(token);
            if (!"REFRESH".equals(parts[1])) {
                return Long.parseLong(parts[2]);
            }
            return null;
        } catch (Exception e) {
            logger.error("Token-dan userId çıxarılarkən xəta: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Token-ın etibarlı olub-olmadığını yoxlayır
     */
    public Boolean isTokenValid(String token) {
        try {
            String[] parts = decodeAndSplitToken(token);

            // Signature yoxla
            String data = parts[0] + "|" + parts[1] + "|" + parts[2] + "|" + parts[3];
            String expectedSignature = createSignature(data);
            String actualSignature = parts[4];

            if (!expectedSignature.equals(actualSignature)) {
                logger.warn("Token signature yanlışdır");
                return false;
            }

            // Expiration yoxla
            long expirationTime = Long.parseLong(parts[3]);
            if (System.currentTimeMillis() > expirationTime) {
                logger.warn("Token vaxtı bitib");
                return false;
            }

            return true;
        } catch (Exception e) {
            logger.error("Token doğrulama xətası: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Token-ın access token olub-olmadığını yoxlayır
     */
    public Boolean isAccessToken(String token) {
        try {
            String role = getRoleFromToken(token);
            return role != null && !"REFRESH".equals(role);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Token-ın refresh token olub-olmadığını yoxlayır
     */
    public Boolean isRefreshToken(String token) {
        try {
            String role = getRoleFromToken(token);
            return "REFRESH".equals(role);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * UserDetails ilə token doğrulama
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        try {
            String phoneNumber = getPhoneNumberFromToken(token);
            return phoneNumber != null &&
                    phoneNumber.equals(userDetails.getUsername()) &&
                    isTokenValid(token);
        } catch (Exception e) {
            logger.error("Token doğrulama xətası: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Token məlumatları üçün helper sinif
     */
    public UserTokenInfo extractUserInfo(String token) {
        try {
            String phoneNumber = getPhoneNumberFromToken(token);
            String role = getRoleFromToken(token);
            Long userId = getUserIdFromToken(token);
            Date expiration = new Date(System.currentTimeMillis() + jwtExpirationMs);

            return new UserTokenInfo(phoneNumber, role, userId, expiration);
        } catch (Exception e) {
            logger.error("Token məlumatları çıxarılarkən xəta: {}", e.getMessage());
            throw new RuntimeException("Token məlumatları çıxarıla bilmədi", e);
        }
    }

    /**
     * Token qısaltılmış formada göstərmək üçün
     */
    public String getMaskedToken(String token) {
        if (token == null || token.length() < 20) {
            return "***";
        }
        return token.substring(0, 10) + "..." + token.substring(token.length() - 10);
    }

    // Private helper metodlar

    /**
     * HMAC-SHA256 signature yaradır
     */
    private String createSignature(String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(jwtSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(keySpec);
            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            logger.error("Signature yaratma xətası: {}", e.getMessage());
            return "invalid_signature";
        }
    }

    /**
     * Token-u decode edib hissələrə ayırır
     */
    private String[] decodeAndSplitToken(String token) {
        try {
            String decoded = new String(Base64.getDecoder().decode(token), StandardCharsets.UTF_8);
            String[] parts = decoded.split("\\|");
            if (parts.length != 5) {
                throw new IllegalArgumentException("Token formatı yanlışdır");
            }
            return parts;
        } catch (Exception e) {
            logger.error("Token decode xətası: {}", e.getMessage());
            throw new RuntimeException("Token decode edilə bilmədi", e);
        }
    }

    /**
     * Token məlumatları üçün helper sinif
     */
    public static class UserTokenInfo {
        private final String phoneNumber;
        private final String role;
        private final Long userId;
        private final Date expiration;

        public UserTokenInfo(String phoneNumber, String role, Long userId, Date expiration) {
            this.phoneNumber = phoneNumber;
            this.role = role;
            this.userId = userId;
            this.expiration = expiration;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public String getRole() {
            return role;
        }

        public Long getUserId() {
            return userId;
        }

        public Date getExpiration() {
            return expiration;
        }

        public boolean isExpired() {
            return expiration.before(new Date());
        }

        @Override
        public String toString() {
            return "UserTokenInfo{" +
                    "phoneNumber='" + phoneNumber + '\'' +
                    ", role='" + role + '\'' +
                    ", userId=" + userId +
                    ", expiration=" + expiration +
                    '}';
        }
    }
}