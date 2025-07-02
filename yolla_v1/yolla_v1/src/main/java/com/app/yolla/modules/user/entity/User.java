package com.app.yolla.modules.user.entity;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * İstifadəçi Entity Sinfi
 * <p>
 * Bu sinif verilənlər bazasındaki users cədvəlini təmsil edir.
 * Həm müştərilər, həm də sistemdəki digər istifadəçiləri (hazırlayan, admin) saxlayır.
 * <p>
 * Analoji: Bu sinif bir "şəxsiyyət vəsiqəsi" kimidir - hər istifadəçinin
 * əsas məlumatlarını saxlayır (ad, telefon, rol və s.)
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class) // Avtomatik tarix yazmaq üçün
public class User {

	@Id
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "id", columnDefinition = "VARCHAR(36)", nullable = false, updatable = false)
	@JdbcTypeCode(SqlTypes.CHAR)
	private UUID id;

    /**
     * Telefon nömrəsi - sistemdə unikal identifikator
     * Format: +994501234567
     */
    @Column(name = "phone_number", unique = true, nullable = false, length = 20)
    @NotBlank(message = "Telefon nömrəsi boş ola bilməz")
    @Pattern(regexp = "^\\+994[0-9]{9}$",
            message = "Telefon nömrəsi +994XXXXXXXXX formatında olmalıdır")
    private String phoneNumber;

    /**
     * İstifadəçinin tam adı
     */
    @Column(name = "full_name", length = 255)
    @Size(min = 2, max = 255, message = "Ad 2-255 simvol arasında olmalıdır")
    private String fullName;

    /**
     * Email ünvanı (ixtiyari)
     */

    @Email(message = "Email düzgün formatda olmalıdır")
	@NotBlank(message = "Email boş ola bilməz")
	@Column(name = "email", length = 255, unique = true)
    private String email;

    /**
     * İstifadəçinin rolu (CUSTOMER, PREPARER, ADMIN)
     */
    @Enumerated(EnumType.STRING)
	@Column(name = "role", nullable = false, length = 50)
    private UserRole role = UserRole.CUSTOMER;

    /**
     * İstifadəçinin aktiv olub-olmadığı
     */
    @Column(name = "is_active")
    private Boolean isActive = true;

    /**
     * Yaradılma tarixi (avtomatik)
     */
    @CreatedDate
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

    /**
     * Yenilənmə tarixi (avtomatik)
     */
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Konstruktorlar


    public User(String phoneNumber, String fullName, UserRole role) {
        this.phoneNumber = phoneNumber;
        this.fullName = fullName;
        this.role = role;
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
     * İstifadəçinin müştəri olub-olmadığını yoxlayır
     */
    public boolean isCustomer() {
        return UserRole.CUSTOMER.equals(this.role);
    }

    /**
     * İstifadəçinin hazırlayan olub-olmadığını yoxlayır
     */
    public boolean isPreparer() {
        return UserRole.PREPARER.equals(this.role);
    }

    /**
     * İstifadəçinin admin olub-olmadığını yoxlayır
     */
    public boolean isAdmin() {
        return UserRole.ADMIN.equals(this.role);
    }

    // equals və hashCode (id əsasında)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // toString metodu (debug üçün faydalıdır)
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", fullName='" + fullName + '\'' +
                ", role=" + role +
                ", isActive=" + isActive +
                '}';
    }
}