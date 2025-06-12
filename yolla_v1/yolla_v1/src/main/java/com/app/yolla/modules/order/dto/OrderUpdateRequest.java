package com.app.yolla.modules.order.dto;

import com.app.yolla.modules.user.entity.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

/**
 * İstifadəçi Yeniləmə Request DTO
 * <p>
 * Bu sinif mövcud istifadəçi məlumatlarını yeniləmək üçün istifadə olunur.
 * Bütün sahələr ixtiyaridir - yalnız dəyişdirilən sahələr göndərilir.
 * <p>
 * Analogi: Bu sinif "profil redaktə formu" kimidir - istifadəçi
 * yalnız dəyişdirmək istədiyi sahələri doldurur.
 */
public class OrderUpdateRequest {

    @Size(min = 2, max = 255, message = "Ad 2-255 simvol arasında olmalıdır")
    private String fullName;

    @Email(message = "Email düzgün formatda olmalıdır")
    private String email;

    private UserRole role;

    private Boolean isActive;

    // Default konstruktor
    public OrderUpdateRequest() {
    }

    // Konstruktor
    public OrderUpdateRequest(String fullName, String email, UserRole role, Boolean isActive) {
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.isActive = isActive;
    }

    // Getter və Setter metodları
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

    /**
     * Request-də həqiqətən dəyişiklik olub-olmadığını yoxlayır
     */
    public boolean hasAnyChanges() {
        return fullName != null ||
                email != null ||
                role != null ||
                isActive != null;
    }

    @Override
    public String toString() {
        return "UserUpdateRequest{" +
                "fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", isActive=" + isActive +
                '}';
    }
}