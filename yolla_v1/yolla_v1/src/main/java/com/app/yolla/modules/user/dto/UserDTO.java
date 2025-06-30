package com.app.yolla.modules.user.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.app.yolla.modules.user.entity.UserRole;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * İstifadəçi DTO (Data Transfer Object) Sinfi
 * <p>
 * Bu sinif API-lar vasitəsilə məlumat mübadiləsi üçün istifadə olunur.
 * Entity sinifindən fərqli olaraq, bu sinifdə yalnız API-da lazım olan sahələr var.
 * <p>
 * Analogi: Bu sinif bir "vizit kartı" kimidir - yalnız lazımi məlumatları göstərir,
 * daxili sistem məlumatlarını (məs. parol) gizlədir.
 */
public class UserDTO {

	private UUID id;

    @NotBlank(message = "Telefon nömrəsi mütləqdir")
    @Pattern(regexp = "^\\+994[0-9]{9}$",
            message = "Telefon nömrəsi +994XXXXXXXXX formatında olmalıdır")
    private String phoneNumber;

    @Size(min = 2, max = 255, message = "Ad 2-255 simvol arasında olmalıdır")
    private String fullName;

    @Email(message = "Email düzgün formatda olmalıdır")
    private String email;

    private UserRole role;
    private Boolean isActive;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

    // Default konstruktor
    public UserDTO() {
    }

    // Tam konstruktor
	public UserDTO(UUID id, String phoneNumber, String fullName, String email,
                   UserRole role, Boolean isActive, LocalDateTime createdAt,
                   LocalDateTime updatedAt) {
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Əsas məlumatlarla konstruktor (qeydiyyat üçün)
    public UserDTO(String phoneNumber, String fullName, UserRole role) {
        this.phoneNumber = phoneNumber;
        this.fullName = fullName;
        this.role = role;
        this.isActive = true;
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
        this.role = role;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Utility metodları

    /**
     * Rolun göstəriləcək adını qaytarır
     */
    public String getRoleDisplayName() {
        return role != null ? role.getDisplayName() : null;
    }

    /**
     * İstifadəçinin aktiv olub-olmadığını string olaraq qaytarır
     */
    public String getStatusText() {
        return Boolean.TRUE.equals(isActive) ? "Aktiv" : "Qeyri-aktiv";
    }

    /**
     * Qısaldılmış telefon nömrəsi (göstərmək üçün)
     * Məsələn: +994501234567 -> +994*****4567
     */
    public String getMaskedPhoneNumber() {
        if (phoneNumber == null || phoneNumber.length() < 8) {
            return phoneNumber;
        }

        String prefix = phoneNumber.substring(0, 4);  // +994
        String suffix = phoneNumber.substring(phoneNumber.length() - 4); // son 4 rəqəm
        return prefix + "*****" + suffix;
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "id=" + id +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", isActive=" + isActive +
                '}';
    }
}