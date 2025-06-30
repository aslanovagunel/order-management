package com.app.yolla.modules.auth.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.yolla.modules.auth.entity.OtpCode;
import com.app.yolla.modules.auth.entity.OtpType;
import com.app.yolla.modules.auth.repository.OtpRepository;
import com.app.yolla.shared.exception.OtpException;

/**
 * OTP Xidmət Sinfi
 * <p>
 * Bu sinif OTP kodlarının yaradılması, göndərilməsi və doğrulanması
 * əməliyyatlarını idarə edir.
 * <p>
 * Analogi: Bu sinif bir "təhlükəsizlik şirkəti" kimidir - giriş kodları
 * yaradır, göndərir və doğruluğunu yoxlayır.
 */
@Service
@Transactional(noRollbackFor = Exception.class)
public class OtpService {

    private static final Logger logger = LoggerFactory.getLogger(OtpService.class);
    private static final SecureRandom random = new SecureRandom();

    @Autowired
    private OtpRepository otpRepository;

    @Autowired
    private SmsService smsService;

    @Value("${otp.expiration-minutes:5}")
    private int defaultExpirationMinutes;

    @Value("${otp.max-attempts:3}")
    private int defaultMaxAttempts;

    /**
     * Yeni OTP kodu yaradır və göndərir
     */
	public String generateAndSendOtp(String phoneNumber, OtpType otpType, String ipAddress) {
        logger.info("OTP yaradılır: telefon={}, növ={}", phoneNumber, otpType);

        // Əvvəlki aktiv OTP-ləri ləğv et
        invalidateActiveOtps(phoneNumber, otpType);

        // Yeni OTP kodu yaradırıq
        String otpCode = generateOtpCode();

        // Bitmə vaxtını hesablayırıq
        LocalDateTime expiresAt = LocalDateTime.now()
                .plusMinutes(otpType.getExpirationMinutes());

        // OTP entity yaradırıq
        OtpCode otp = new OtpCode();
        otp.setPhoneNumber(phoneNumber);
        otp.setOtpCode(otpCode);
        otp.setExpiresAt(expiresAt);
        otp.setOtpType(otpType);
        otp.setIpAddress(ipAddress);
        otp.setIsUsed(false);
        otp.setAttemptCount(0);

        // Verilənlər bazasına saxlayırıq
        otpRepository.save(otp);

        // SMS göndəririk
        try {
            String message = otpType.createSmsMessage(otpCode);
            smsService.sendSms(phoneNumber, message);

            logger.info("OTP uğurla göndərildi: telefon={}", phoneNumber);

        } catch (Exception e) {
            logger.error("SMS göndərmə xətası: telefon={}", phoneNumber, e);
            throw new OtpException("SMS göndərilmədi, yenidən cəhd edin", "SMS_SEND_FAILED");
        }
		return otpCode;
    }

    /**
     * OTP kodunu doğrulayır
     */
    public void verifyOtp(String phoneNumber, String otpCode, OtpType otpType) {
        logger.info("OTP doğrulanır: telefon={}, növ={}", phoneNumber, otpType);

        // Ən son aktiv OTP-ni tapırıq
        Optional<OtpCode> otpOptional = otpRepository
                .findTopByPhoneNumberAndOtpTypeAndIsUsedFalseOrderByCreatedAtDesc(
                        phoneNumber, otpType);

        if (otpOptional.isEmpty()) {
            logger.warn("Aktiv OTP tapılmadı: telefon={}", phoneNumber);
            throw new OtpException("OTP kodu tapılmadı və ya vaxtı bitib", "OTP_NOT_FOUND");
        }

        OtpCode otp = otpOptional.get();

        // Cəhd sayını artır
        otp.incrementAttemptCount();
        otpRepository.save(otp);

        // Maksimum cəhd sayını yoxla
        if (otp.hasExceededMaxAttempts(otpType.getMaxAttempts())) {
            logger.warn("Maksimum cəhd sayı aşıldı: telefon={}, cəhd={}",
                    phoneNumber, otp.getAttemptCount());
            throw new OtpException("Çox yanlış cəhd. Yeni kod tələb edin", "MAX_ATTEMPTS_EXCEEDED");
        }

        // Vaxtın bitib-bitmədiyini yoxla
        if (otp.isExpired()) {
            logger.warn("OTP vaxtı bitib: telefon={}, bitmə_vaxtı={}",
                    phoneNumber, otp.getExpiresAt());
            throw new OtpException("OTP kodunun vaxtı bitib", "OTP_EXPIRED");
        }

        // OTP kodunu yoxla
        if (!otp.getOtpCode().equals(otpCode)) {
            logger.warn("Yanlış OTP kodu: telefon={}", phoneNumber);
            throw new OtpException("OTP kodu yanlışdır", "INVALID_OTP");
        }

        // OTP doğrudur - istifadə olunmuş kimi qeyd et
        otp.markAsUsed();
        otpRepository.save(otp);

        logger.info("OTP uğurla doğrulandı: telefon={}", phoneNumber);
    }

    /**
     * Rate limiting - eyni telefon üçün tez-tez OTP sorğusunu məhdudlaşdırır
     */
    public boolean canRequestOtp(String phoneNumber, OtpType otpType) {
        LocalDateTime oneMinuteAgo = LocalDateTime.now().minusMinutes(1);

        long recentOtpCount = otpRepository.countByPhoneNumberAndOtpTypeAndCreatedAtAfter(
                phoneNumber, otpType, oneMinuteAgo);

        if (recentOtpCount >= 3) { // 1 dəqiqədə maksimum 3 OTP
            logger.warn("Rate limit aşıldı: telefon={}, say={}", phoneNumber, recentOtpCount);
            return false;
        }

        return true;
    }

    /**
     * OTP məlumatlarını əldə edir (admin üçün)
     */
    @Transactional(readOnly = true)
    public Optional<OtpCode> getOtpInfo(String phoneNumber, OtpType otpType) {
        return otpRepository.findTopByPhoneNumberAndOtpTypeAndIsUsedFalseOrderByCreatedAtDesc(
                phoneNumber, otpType);
    }

    /**
     * 6 rəqəmli təsadüfi OTP kodu yaradır
     */
    private String generateOtpCode() {
        int code = 100000 + random.nextInt(900000); // 100000-999999 arası
        return String.valueOf(code);
    }

    /**
     * Əvvəlki aktiv OTP kodlarını ləğv edir
     */
    private void invalidateActiveOtps(String phoneNumber, OtpType otpType) {
        otpRepository.markAllAsUsedByPhoneNumberAndOtpType(phoneNumber, otpType);
        logger.debug("Əvvəlki OTP kodları ləğv edildi: telefon={}, növ={}", phoneNumber, otpType);
    }

    /**
     * Köhnə OTP kodlarını təmizləyir (scheduled task kimi çalışa bilər)
     */
    public void cleanupExpiredOtps() {
        LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);
        int deletedCount = otpRepository.deleteByCreatedAtBefore(oneDayAgo);

        if (deletedCount > 0) {
            logger.info("Köhnə OTP kodları təmizləndi: say={}", deletedCount);
        }
    }

    /**
     * OTP statistikası
     */
    @Transactional(readOnly = true)
    public OtpStatistics getOtpStatistics() {
        LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);

        long todayCount = otpRepository.countByCreatedAtAfter(today);
        long successfulCount = otpRepository.countByCreatedAtAfterAndIsUsedTrue(today);

        return new OtpStatistics(todayCount, successfulCount);
    }

    /**
     * OTP statistika sinfi
     */
    public static class OtpStatistics {
        private final long totalSent;
        private final long successfullyVerified;
        private final double successRate;

        public OtpStatistics(long totalSent, long successfullyVerified) {
            this.totalSent = totalSent;
            this.successfullyVerified = successfullyVerified;
            this.successRate = totalSent > 0 ? (double) successfullyVerified / totalSent * 100 : 0;
        }

        public long getTotalSent() {
            return totalSent;
        }

        public long getSuccessfullyVerified() {
            return successfullyVerified;
        }

        public double getSuccessRate() {
            return successRate;
        }
    }
}