package com.app.yolla.modules.user.dto;

import com.app.yolla.modules.user.entity.UserRole;

/**
 * İstifadəçi Yeniləmə Request DTO
 * <p>
 * Bu sinif mövcud istifadəçi məlumatlarını yeniləmək üçün istifadə olunur.
 * Bütün sahələr ixtiyaridir - yalnız dəyişdirilən sahələr göndərilir.
 * <p>
 * Analogi: Bu sinif "profil redaktə formu" kimidir - istifadəçi
 * yalnız dəyişdirmək istədiyi sahələri doldurur.
 */
public class UserUpdateRequest {

    private String fullName;

    private String email;

    private UserRole role;

    private Boolean isActive;

    // Default konstruktor
    public UserUpdateRequest() {
    }

    // Konstruktor
    public UserUpdateRequest(String fullName, String email, UserRole role, Boolean isActive) {
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