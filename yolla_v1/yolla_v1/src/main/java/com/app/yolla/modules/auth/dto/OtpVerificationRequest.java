package com.app.yolla.modules.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * OTP Verification Request DTO
 * <p>
 * OTP doğrulama sorğusu üçün istifadə olunur
 */
public class OtpVerificationRequest {

    @NotBlank(message = "Telefon nömrəsi mütləqdir")
    @Pattern(regexp = "^\\+994[0-9]{9}$",
            message = "Telefon nömrəsi +994XXXXXXXXX formatında olmalıdır")
    private String phoneNumber;

    @NotBlank(message = "OTP kodu mütləqdir")
    @Size(min = 6, max = 6, message = "OTP kodu 6 rəqəm olmalıdır")
    private String otpCode;

    public OtpVerificationRequest() {
    }

    public OtpVerificationRequest(String phoneNumber, String otpCode) {
        this.phoneNumber = phoneNumber;
        this.otpCode = otpCode;
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

    @Override
    public String toString() {
        return "OtpVerificationRequest{phoneNumber='" + phoneNumber + "', otpCode='****'}";
    }
}
