package com.app.yolla.modules.auth.entity;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
/**
 * OTP Kodu Entity Sinfi
 * <p>
 * Bu sinif SMS ilə göndərilən təsdiq kodlarını saxlayır.
 * Həm qeydiyyat, həm də giriş üçün istifadə olunur.
 * <p>
 * Analogi: Bu sinif bir "giriş kartı" kimidir - müəyyən müddətə
 * etibarlıdır və yalnız bir dəfə istifadə oluna bilər.
 */
@Entity
@Table(name = "otp_codes")
@Data
@EntityListeners(AuditingEntityListener.class)
public class OtpCode {

	@Id
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "id", columnDefinition = "VARCHAR(36)", nullable = false, updatable = false)
	@JdbcTypeCode(SqlTypes.CHAR)
	private UUID id;

    /**
     * OTP göndərilən telefon nömrəsi
     */
    @Column(name = "phone_number", nullable = false, length = 20)
    @NotBlank(message = "Telefon nömrəsi boş ola bilməz")
    @Pattern(regexp = "^\\+994[0-9]{9}$",
            message = "Telefon nömrəsi +994XXXXXXXXX formatında olmalıdır")
    private String phoneNumber;

    /**
     * 6 rəqəmli OTP kodu
     */
    @Column(name = "otp_code", nullable = false, length = 6)
    @NotBlank(message = "OTP kodu boş ola bilməz")
    @Size(min = 6, max = 6, message = "OTP kodu 6 rəqəm olmalıdır")
    private String otpCode;

    /**
     * OTP kodunun bitmə vaxtı
     */
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    /**
     * OTP kodunun istifadə olunub-olunmadığı
     */
    @Column(name = "is_used")
    private Boolean isUsed = false;

    /**
     * Yanlış cəhd sayı
     */
    @Column(name = "attempt_count")
    private Integer attemptCount = 0;

    /**
     * IP ünvanı (təhlükəsizlik üçün)
     */
    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    /**
     * OTP növü (LOGIN, REGISTRATION, PASSWORD_RESET)
     */
    @Enumerated(EnumType.STRING)
	@Column(name = "otp_type", length = 20)
    private OtpType otpType = OtpType.LOGIN;

    /**
     * Yaradılma tarixi (avtomatik)
     */
    @CreatedDate
//    @Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt = LocalDateTime.now();

    // Konstruktorlar
	public OtpCode() {
	}

	public OtpCode(String phoneNumber, String otpCode, LocalDateTime expiresAt, OtpType otpType) {
		this.phoneNumber = phoneNumber;
		this.otpCode = otpCode;
		this.expiresAt = expiresAt;
		this.otpType = otpType;
	}

	// Utility metodları

	/**
	 * OTP kodunun vaxtının bitib-bitmədiyini yoxlayır
	 */
	public boolean isExpired() {
		return LocalDateTime.now().isAfter(this.expiresAt);
	}

	/**
	 * OTP kodunun etibarlı olub-olmadığını yoxlayır
	 */
	public boolean isValid() {
		return !isUsed && !isExpired();
	}

	/**
	 * OTP kodunu istifadə olunmuş kimi qeyd edir
	 */
	public void markAsUsed() {
		this.isUsed = true;
	}

	/**
	 * Cəhd sayını artırır
	 */
	public void incrementAttemptCount() {
		this.attemptCount = (this.attemptCount == null ? 0 : this.attemptCount) + 1;
	}

	/**
	 * Maksimum cəhd sayına çatıb-çatmadığını yoxlayır
	 */
	public boolean hasExceededMaxAttempts(int maxAttempts) {
		return this.attemptCount != null && this.attemptCount >= maxAttempts;
	}

	// Getter və Setter metodları
	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getOtpCode() {
		return otpCode;
	}

	public void setOtpCode(String otpCode) {
		this.otpCode = otpCode;
	}

	public LocalDateTime getExpiresAt() {
		return expiresAt;
	}

	public void setExpiresAt(LocalDateTime expiresAt) {
		this.expiresAt = expiresAt;
	}

	public Boolean getIsUsed() {
		return isUsed;
	}

	public void setIsUsed(Boolean used) {
		isUsed = used;
	}

	public Integer getAttemptCount() {
		return attemptCount;
	}

	public void setAttemptCount(Integer attemptCount) {
		this.attemptCount = attemptCount;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public OtpType getOtpType() {
		return otpType;
	}

	public void setOtpType(OtpType otpType) {
		this.otpType = otpType;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	// equals və hashCode
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		OtpCode otpCode = (OtpCode) o;
		return Objects.equals(id, otpCode.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public String toString() {
		return "OtpCode{" + "id=" + id + ", phoneNumber='" + phoneNumber + '\'' + ", otpType=" + otpType + ", isUsed="
				+ isUsed + ", expiresAt=" + expiresAt + '}';
	}
}