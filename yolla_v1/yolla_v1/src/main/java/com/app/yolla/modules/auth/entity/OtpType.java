package com.app.yolla.modules.auth.entity;


/**
 * OTP Növü Enum Sinfi
 * <p>
 * Bu enum müxtəlif OTP istifadə hallarını müəyyən edir.
 * Hər növün öz xüsusiyyətləri ola bilər (müddət, mesaj məzmunu və s.)
 */
public enum OtpType {

    /**
     * Sistemi daxil olmaq üçün OTP
     * İstifadə halı: Mövcud istifadəçinin girişi
     */
    LOGIN("Giriş", "Giriş üçün təsdiq kodunuz: "),

    /**
     * Qeydiyyat üçün OTP
     * İstifadə halı: Yeni istifadəçinin qeydiyyatı
     */
    REGISTRATION("Qeydiyyat", "Qeydiyyat üçün təsdiq kodunuz: "),

    /**
     * Parol bərpası üçün OTP (gələcək üçün)
     * İstifadə halı: İstifadəçi parolunu unutdqda
     */
    PASSWORD_RESET("Parol Bərpası", "Parol bərpası üçün təsdiq kodunuz: "),

    /**
     * Telefon nömrəsi dəyişmək üçün OTP (gələcək üçün)
     * İstifadə halı: İstifadəçi telefon nömrəsini dəyişdirmək istədikdə
     */
    PHONE_CHANGE("Telefon Dəyişikliyi", "Telefon dəyişikliyi üçün təsdiq kodunuz: ");

    private final String displayName;
    private final String messagePrefix;

    OtpType(String displayName, String messagePrefix) {
        this.displayName = displayName;
        this.messagePrefix = messagePrefix;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getMessagePrefix() {
        return messagePrefix;
    }

    /**
     * SMS mesajını hazırlayır
     */
    public String createSmsMessage(String otpCode) {
        return messagePrefix + otpCode + ". Kodu başqası ilə bölüşməyin.";
    }

    /**
     * OTP-nin etibarlılıq müddətini dəqiqə ilə qaytarır
     */
	public int getExpirationMinutes() {
		switch (this) {
		case LOGIN:
		case REGISTRATION:
			return 5; // 5 dəqiqə
		case PASSWORD_RESET:
			return 10; // 10 dəqiqə
		case PHONE_CHANGE:
			return 15; // 15 dəqiqə
		default:
			return 5;
		}
	}

	/**
	 * Maksimum cəhd sayını qaytarır
	 */
	public int getMaxAttempts() {
		switch (this) {
		case LOGIN:
		case REGISTRATION:
			return 3;
		case PASSWORD_RESET:
		case PHONE_CHANGE:
			return 5;
		default:
			return 3;
		}
	}
}