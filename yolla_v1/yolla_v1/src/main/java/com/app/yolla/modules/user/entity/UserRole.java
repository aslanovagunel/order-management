package com.app.yolla.modules.user.entity;



/**
 * İstifadəçi Rolları Enum Sinfi
 * <p>
 * Bu enum sistemimizdəki bütün mümkün istifadəçi rollərini müəyyən edir.
 * Hər rolun öz icazələri və səlahiyyətləri var.
 * <p>
 * Analoji: Bu, bir şirkətdəki vəzifələr kimi - hər vəzifənin öz
 * məsuliyyətləri və səlahiyyətləri var.
 */

public enum UserRole {

    /**
     * MÜŞTƏRİ - sifarişləri verən şəxslər
     * Səlahiyyətləri:
     * - Məhsulları görmək
     * - Səbətə məhsul əlavə etmək
     * - Sifariş vermək
     * - Öz sifarişlərini görmək
     * - Profil məlumatlarını yeniləmək
     */
	CUSTOMER("CUSTOMER", "ROLE_CUSTOMER"),

    /**
     * HAZIRLAYAN - sifarişləri hazırlayan işçilər
     * Səlahiyyətləri:
     * - Bütün sifarişləri görmək
     * - Sifarişlərin statusunu yeniləmək
     * - Sifarişləri hazırladı kimi qeyd etmək
     */
	PREPARER("PREPARER", "ROLE_PREPARER"),

    /**
     * ADMİN - sistem idarəçiləri
     * Səlahiyyətləri:
     * - Bütün əməliyyatları görə bilir
     * - Məhsulları əlavə/redaktə/silə bilir
     * - İstifadəçiləri idarə edə bilir
     * - Sistem hesabatlarını görmək
     */
	ADMIN("ADMIN", "ROLE_ADMIN");

    private final String displayName;  // Göstəriləcək ad
    private final String authority;    // Spring Security üçün

    /**
     * Enum konstruktor
     */
	UserRole(String displayName, String authority) {
		this.displayName = displayName;
		this.authority = authority;
	}

	/**
	 * İnsan tərəfindən oxunan adı qaytarır
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * Spring Security üçün səlahiyyət adını qaytarır
	 */
	public String getAuthority() {
		return authority;
	}

//    /**
//     * Rolun müştəri olub-olmadığını yoxlayır
//     */
//    public boolean isCustomer() {
//        return this == CUSTOMER;
//    }
//
//    /**
//     * Rolun hazırlayan olub-olmadığını yoxlayır
//     */
//    public boolean isPreparer() {
//        return this == PREPARER;
//    }
//
//    /**
//     * Rolun admin olub-olmadığını yoxlayır
//     */
//    public boolean isAdmin() {
//        return this == ADMIN;
//    }
//
//    /**
//     * Rolun admin və ya hazırlayan olub-olmadığını yoxlayır
//     * (yəni həm sifarişləri hazırlamaq səlahiyyəti var)
//     */
//    public boolean canPrepareOrders() {
//        return this == ADMIN || this == PREPARER;
//    }
//
//    /**
//     * Rolun sistem idarəetməsi səlahiyyəti var-yoxsa yoxlayır
//     */
//    public boolean canManageSystem() {
//        return this == ADMIN;
//    }
//
//    /**
//     * String-dən UserRole-a çevirmək üçün
//     */
//    public static UserRole fromString(String roleStr) {
//        if (roleStr == null) {
//            return CUSTOMER; // Default
//        }
//
//        try {
//            return UserRole.valueOf(roleStr.toUpperCase());
//        } catch (IllegalArgumentException e) {
//            return CUSTOMER; // Yanlış dəyər olsa default qaytarır
//        }
//    }
//
//    /**
//     * Bütün rolların siyahısını string array kimi qaytarır
//     */
//    public static String[] getAllRoleNames() {
//        UserRole[] roles = UserRole.values();
//        String[] roleNames = new String[roles.length];
//
//        for (int i = 0; i < roles.length; i++) {
//            roleNames[i] = roles[i].name();
//        }
//
//        return roleNames;
//    }
//
//    /**
//     * toString metodu - göstəriləcək adı qaytarır
//     */
//    @Override
//    public String toString() {
//        return displayName;
//    }
}