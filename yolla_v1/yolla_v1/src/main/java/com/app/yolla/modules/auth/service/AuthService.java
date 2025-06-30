package com.app.yolla.modules.auth.service;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.yolla.modules.auth.dto.AuthResponse;
import com.app.yolla.modules.auth.dto.UserInfo;
import com.app.yolla.modules.auth.entity.OtpType;
import com.app.yolla.modules.user.dto.UserCreateRequest;
import com.app.yolla.modules.user.dto.UserDTO;
import com.app.yolla.modules.user.entity.UserRole;
import com.app.yolla.modules.user.service.UserService;
import com.app.yolla.shared.exception.DuplicateResourceException;
import com.app.yolla.shared.exception.OtpException;
import com.app.yolla.shared.exception.ResourceNotFoundException;
import com.app.yolla.shared.security.JwtUtil;

/**
 * Authentication Service Sinfi
 *
 * Bu sinif giriş və qeydiyyat proseslərini idarə edir.
 * OTP doğrulaması, token yaratma və istifadəçi idarəetməsi əməliyyatlarını birləşdirir.
 *
 * Analogi: Bu sinif bir "cavabdeh menecer" kimidir - müxtəlif şöbələri (OTP, User, JWT)
 * koordinasiya edərək tam giriş prosesini idarə edir.
 */
@Service
@Transactional
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private UserService userService;

    @Autowired
    private OtpService otpService;

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    /**
     * OTP göndərir - həm mövcud istifadəçilər, həm də yeni qeydiyyat üçün
     */
	public Map<String, Object> sendOtp(String phoneNumber, String ipAddress) {
        logger.info("OTP göndərmə prosesi başlayır: telefon={}", phoneNumber);

        // Rate limiting yoxla
        if (!otpService.canRequestOtp(phoneNumber, OtpType.LOGIN)) {
            throw new OtpException("Çox tez-tez OTP sorğusu. Bir az gözləyin", "RATE_LIMIT_EXCEEDED");
        }

        // İstifadəçinin mövcud olub-olmadığını yoxla
        boolean userExists = userService.existsByPhoneNumber(phoneNumber);

        OtpType otpType = userExists ? OtpType.LOGIN : OtpType.REGISTRATION;

        // OTP yaradıb göndər
		String sendOtp = otpService.generateAndSendOtp(phoneNumber, otpType, ipAddress);

        String message = userExists ?
                "Giriş üçün OTP göndərildi" :
                "Qeydiyyat üçün OTP göndərildi";

        logger.info("OTP uğurla göndərildi: telefon={}, növ={}", phoneNumber, otpType);
        Map<String, Object> data = new HashMap<>();
        data.put("info", message);
        data.put("otp", sendOtp);

		return data;
		
    }

    /**
     * OTP doğrular və giriş edir
     */
	@Transactional(noRollbackFor = OtpException.class)
    public AuthResponse verifyOtpAndLogin(String phoneNumber, String otpCode) {
        logger.info("OTP doğrulama prosesi: telefon={}", phoneNumber);

        UserDTO user;
        boolean isNewUser = false;

        // İstifadəçinin mövcudluğunu yoxla
        try {
			user = userService.findByPhoneNumber(phoneNumber);
			otpService.verifyOtp(phoneNumber, otpCode, OtpType.LOGIN);

		} catch (ResourceNotFoundException e) {
			otpService.verifyOtp(phoneNumber, otpCode, OtpType.REGISTRATION);

			user = createNewUser(phoneNumber);
			isNewUser = true;
        }

        // İstifadəçinin aktiv olub-olmadığını yoxla
        if (!user.getIsActive()) {
            throw new OtpException("Hesabınız deaktivdir. Dəstək ilə əlaqə saxlayın", "ACCOUNT_DEACTIVATED");
        }

        // JWT token-lar yaradırıq
        String accessToken = jwtUtil.generateToken(
                user.getPhoneNumber(),
                user.getRole().name(),
                user.getId()
        );

        String refreshToken = jwtUtil.generateRefreshToken(user.getPhoneNumber());

        // AuthResponse yaradırıq
        AuthResponse authResponse = new AuthResponse();
        authResponse.setAccessToken(accessToken);
        authResponse.setRefreshToken(refreshToken);
        authResponse.setExpiresIn(jwtExpirationMs / 1000); // saniyə ilə
        authResponse.setUser(convertToUserInfo(user));

        logger.info("Uğurlu giriş: telefon={}, yeniİstifadəçi={}, rol={}",
                phoneNumber, isNewUser, user.getRole());

        return authResponse;
    }

    /**
     * Refresh token ilə yeni access token yaradır
     */
    public AuthResponse refreshToken(String refreshToken) {
        logger.info("Token yeniləmə prosesi");

        try {
            // Refresh token-ın etibarlı olub-olmadığını yoxla
            if (!jwtUtil.isTokenValid(refreshToken) || !jwtUtil.isRefreshToken(refreshToken)) {
                throw new OtpException("Refresh token etibarsızdır", "INVALID_REFRESH_TOKEN");
            }

            // Token-dan istifadəçi məlumatlarını çıxar
            String phoneNumber = jwtUtil.getPhoneNumberFromToken(refreshToken);
            UserDTO user = userService.findByPhoneNumber(phoneNumber);

            // İstifadəçinin aktiv olub-olmadığını yoxla
            if (!user.getIsActive()) {
                throw new OtpException("Hesabınız deaktivdir", "ACCOUNT_DEACTIVATED");
            }

            // Yeni access token yaradırıq
            String newAccessToken = jwtUtil.generateToken(
                    user.getPhoneNumber(),
                    user.getRole().name(),
                    user.getId()
            );

            // AuthResponse yaradırıq
            AuthResponse authResponse = new AuthResponse();
            authResponse.setAccessToken(newAccessToken);
            authResponse.setRefreshToken(refreshToken); // Eyni refresh token-ı saxlayırıq
            authResponse.setExpiresIn(jwtExpirationMs / 1000);
            authResponse.setUser(convertToUserInfo(user));

            logger.info("Token uğurla yeniləndi: telefon={}", phoneNumber);

            return authResponse;

        } catch (Exception e) {
            logger.error("Token yeniləmə xətası: {}", e.getMessage());
            throw new OtpException("Token yenilənmədi: " + e.getMessage(), "TOKEN_REFRESH_FAILED");
        }
    }

    /**
     * İstifadəçini sistemdən çıxarır
     */
    public void logout(String token) {
        logger.info("Çıxış prosesi");

        try {
            // Token-dan istifadəçi məlumatlarını əldə et
            String phoneNumber = jwtUtil.getPhoneNumberFromToken(token);

            // Burada token-ı blacklist-ə əlavə edə bilərik (Redis və ya verilənlər bazası)
            // Hal-hazırda sadə log yazırıq

            logger.info("İstifadəçi uğurla çıxış etdi: telefon={}", phoneNumber);

        } catch (Exception e) {
            logger.warn("Çıxış zamanı xəta: {}", e.getMessage());
            // Çıxış zamanı xəta kritik deyil, yenə də uğurlu sayılır
        }
    }

    /**
     * Token-ın etibarlı olub-olmadığını yoxlayır
     */
    public boolean validateToken(String token) {
        try {
            return jwtUtil.isTokenValid(token) && jwtUtil.isAccessToken(token);
        } catch (Exception e) {
            logger.warn("Token doğrulama xətası: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Cari istifadəçinin profil məlumatlarını qaytarır
     */
    public UserInfo getCurrentUserProfile(String token) {
        try {
            String phoneNumber = jwtUtil.getPhoneNumberFromToken(token);
            UserDTO user = userService.findByPhoneNumber(phoneNumber);

            return convertToUserInfo(user);

        } catch (Exception e) {
            logger.error("Profil məlumatları əldə edilmədi: {}", e.getMessage());
            throw new ResourceNotFoundException("İstifadəçi profili tapılmadı");
        }
    }

    /**
     * Yeni istifadəçi yaradır (qeydiyyat zamanı)
     */
    private UserDTO createNewUser(String phoneNumber) {
        logger.info("Yeni istifadəçi yaradılır: telefon={}", phoneNumber);

		try {
			UserCreateRequest createRequest = new UserCreateRequest();
			createRequest.setPhoneNumber(phoneNumber);
			createRequest.setRole(UserRole.CUSTOMER);

			return userService.createUser(createRequest);

		} catch (DuplicateResourceException e) {
			logger.warn("İstifadəçi artıq mövcuddur, sistemdən tapılır: {}", phoneNumber);
			return userService.findByPhoneNumber(phoneNumber);
		}
    }

    /**
     * UserDTO-nu UserInfo-ya çevirir
     */
    private UserInfo convertToUserInfo(UserDTO user) {
        return new UserInfo(
                user.getId(),
                user.getPhoneNumber(),
                user.getFullName(),
                user.getEmail(),
                user.getRole().name(),
                user.getIsActive()
        );
    }
}