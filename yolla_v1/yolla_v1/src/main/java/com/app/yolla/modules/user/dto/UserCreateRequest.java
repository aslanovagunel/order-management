package com.app.yolla.modules.user.dto;

import com.app.yolla.modules.user.entity.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * İstifadəçi Yaratma Request DTO
 * <p>
 * Bu sinif yeni istifadəçi yaratmaq üçün gələn məlumatları qəbul edir.
 * Yalnız yaratma zamanı lazım olan sahələri ehtiva edir.
 * <p>
 * Analogi: Bu sinif bir "qeydiyyat formu" kimidir - istifadəçinin
 * doldurmalı olduğu sahələri göstərir.
 */
public class UserCreateRequest {

    @NotBlank(message = "Telefon nömrəsi mütləqdir")
    @Pattern(regexp = "^\\+994[0-9]{9}$",
            message = "Telefon nömrəsi +994XXXXXXXXX formatında olmalıdır")
    private String phoneNumber;

    @Size(min = 2, max = 255, message = "Ad 2-255 simvol arasında olmalıdır")
    private String fullName;

    @Email(message = "Email düzgün formatda olmalıdır")
    private String email;

    private UserRole role = UserRole.CUSTOMER; // Default olaraq müştəri

    // Default konstruktor
    public UserCreateRequest() {
    }

    // Konstruktor
    public UserCreateRequest(String phoneNumber, String fullName, String email, UserRole role) {
        this.phoneNumber = phoneNumber;
        this.fullName = fullName;
        this.email = email;
        this.role = role != null ? role : UserRole.CUSTOMER;
    }

    // Getter və Setter metodları
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role != null ? role : UserRole.CUSTOMER;
    }

    @Override
    public String toString() {
        return "UserCreateRequest{" +
                "phoneNumber='" + phoneNumber + '\'' +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                '}';
    }
}