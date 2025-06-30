package com.app.yolla.modules.auth.dto;

import java.util.UUID;

/**
 * User Info DTO
 * <p>
 * Token ilə birlikdə qaytarılan istifadəçi məlumatları
 */
public class UserInfo {

	private UUID id;
    private String phoneNumber;
    private String fullName;
    private String email;
    private String role;
    private Boolean isActive;

    public UserInfo() {
    }

	public UserInfo(UUID id, String phoneNumber, String fullName, String email, String role, Boolean isActive) {
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.isActive = isActive;
    }

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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "id=" + id +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", fullName='" + fullName + '\'' +
                ", role='" + role + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
