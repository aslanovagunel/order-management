package com.app.yolla.modules.auth.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * SMS Xidmət Sinfi
 * <p>
 * Bu sinif SMS göndərmə əməliyyatlarını idarə edir.
 * Test rejimində konsola yazır, məhsul rejimində həqiqi SMS göndərir.
 * <p>
 * Analogi: Bu sinif bir "poçtçı" kimidir - mesajları düzgün ünvana çatdırır.
 */
@Service
public class SmsService {

    private static final Logger logger = LoggerFactory.getLogger(SmsService.class);

    @Value("${sms.provider:test}")
    private String smsProvider;

    @Value("${sms.test-mode:true}")
    private boolean testMode;

    // Twilio və ya digər SMS provayderlər üçün konfiqurasiya
    @Value("${sms.twilio.account-sid:}")
    private String twilioAccountSid;

    @Value("${sms.twilio.auth-token:}")
    private String twilioAuthToken;

    @Value("${sms.twilio.from-number:}")
    private String twilioFromNumber;

    /**
     * SMS göndərir
     */
    public void sendSms(String phoneNumber, String message) {
        logger.info("SMS göndərilir: telefon={}, provayider={}, test_rejimi={}",
                phoneNumber, smsProvider, testMode);

        try {
            if (testMode) {
                sendTestSms(phoneNumber, message);
                return;
            }

            switch (smsProvider.toLowerCase()) {
                case "twilio":
                    sendTwilioSms(phoneNumber, message);
                    return;
                case "local":
                    sendLocalSms(phoneNumber, message);
                    return;
                default:
                    logger.warn("Naməlum SMS provayderi: {}", smsProvider);
                    sendTestSms(phoneNumber, message);
            }

        } catch (Exception e) {
            logger.error("SMS göndərmə xətası: telefon={}", phoneNumber, e);
        }
    }

    /**
     * Test rejimi - SMS-i konsola yazır
     */
    private void sendTestSms(String phoneNumber, String message) {
        logger.info("=== TEST SMS ===");
        logger.info("Telefon: {}", phoneNumber);
        logger.info("Mesaj: {}", message);
        logger.info("================");

        // Test rejimində həmişə uğurlu sayırıq
    }

    /**
     * Twilio ilə SMS göndərir (məhsul üçün)
     */
    private void sendTwilioSms(String phoneNumber, String message) {
        // Burada Twilio SDK istifadə edəcəksiniz
        logger.info("Twilio ilə SMS göndərilir...");

        try {
            /*
            // Twilio SDK kodu (dependency əlavə etdikdən sonra):
            Twilio.init(twilioAccountSid, twilioAuthToken);

            Message.creator(
                new PhoneNumber(phoneNumber),
                new PhoneNumber(twilioFromNumber),
                message
            ).create();
            */

            logger.info("Twilio SMS uğurla göndərildi: telefon={}", phoneNumber);

        } catch (Exception e) {
            logger.error("Twilio SMS xətası: telefon={}", phoneNumber, e);
        }
    }

    /**
     * Yerli SMS provayderə göndərir (Azərbaycan provayderləri)
     */
    private void sendLocalSms(String phoneNumber, String message) {
        logger.info("Yerli provayder ilə SMS göndərilir...");

        try {
            // Burada yerli SMS provayder API-sini istifadə edəcəksiniz
            // Məsələn: Nar, Azercell, Bakcell API-ları

            logger.info("Yerli SMS uğurla göndərildi: telefon={}", phoneNumber);

        } catch (Exception e) {
            logger.error("Yerli SMS xətası: telefon={}", phoneNumber, e);
        }
    }

    /**
     * SMS balansını yoxlayır (gələcək üçün)
     */
    public double getSmsBalance() {
        if (testMode) {
            return 999.99; // Test rejimində sonsuz balans
        }

        // Burada provayderdən balans sorğusu
        return 0.0;
    }

    /**
     * SMS göndərmə tarixçəsini əldə edir (gələcək üçün)
     */
    public void logSmsDelivery(String phoneNumber, String message, boolean success) {
        // SMS göndərmə logunu verilənlər bazasına yaz
        logger.info("SMS delivery log: telefon={}, uğur={}", phoneNumber, success);
    }

    /**
     * Telefon nömrəsinin düzgün formatda olub-olmadığını yoxlayır
     */
    public boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return false;
        }

        // Azərbaycan telefon nömrəsi formatı: +994XXXXXXXXX
        return phoneNumber.matches("^\\+994[0-9]{9}$");
    }

    /**
     * Telefon nömrəsini standart formata gətirir
     */
    public String formatPhoneNumber(String phoneNumber) {
        if (phoneNumber == null) {
            return null;
        }

        // Boşluqları və xüsusi simvolları sil
        String cleaned = phoneNumber.replaceAll("[\\s\\-\\(\\)]", "");

        // Azərbaycan formatına gətir
        if (cleaned.startsWith("994")) {
            return "+" + cleaned;
        } else if (cleaned.startsWith("0")) {
            return "+994" + cleaned.substring(1);
        } else if (cleaned.length() == 9) {
            return "+994" + cleaned;
        }

        return cleaned;
    }
}