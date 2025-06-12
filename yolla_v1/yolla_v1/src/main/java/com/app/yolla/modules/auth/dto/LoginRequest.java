package com.app.yolla.modules.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Login Request DTO
 * <p>
 * OTP göndərmə sorğusu üçün istifadə olunur
 */
public class LoginRequest {

    @NotBlank(message = "Telefon nömrəsi mütləqdir")
    @Pattern(regexp = "^\\+994[0-9]{9}$",
            message = "Telefon nömrəsi +994XXXXXXXXX formatında olmalıdır")
    private String phoneNumber;

    public LoginRequest() {
    }

    public LoginRequest(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return "LoginRequest{phoneNumber='" + phoneNumber + "'}";
    }
}

