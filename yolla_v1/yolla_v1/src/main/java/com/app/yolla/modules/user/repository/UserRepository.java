package com.app.yolla.modules.user.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.app.yolla.modules.user.entity.User;
import com.app.yolla.modules.user.entity.UserRole;

/**
 * İstifadəçi Repository İnterfeysi
 * <p>
 * Bu interfeys verilənlər bazası ilə əlaqə yaradır. JpaRepository istifadə
 * edərək, əsas CRUD əməliyyatlarını avtomatik alırıq.
 * <p>
 * Analoji: Bu interfeys "kitabxanaçı" kimidir - sizə lazım olan istifadəçi
 * məlumatlarını tapıb gətirir.
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

	/**
	 * Telefon nömrəsi ilə istifadəçi tapır Bu metodla OTP sistemi və giriş üçün
	 * istifadə edirik
	 *
	 * @param phoneNumber telefon nömrəsi (+994501234567 formatında)
	 * @return tapılan istifadəçi (və ya boş Optional)
	 */
	Optional<User> findByPhoneNumber(String phoneNumber);

	/**
	 * Telefon nömrəsinin mövcud olub-olmadığını yoxlayır Qeydiyyat zamanı dublikat
	 * yoxlaması üçün
	 */
	boolean existsByPhoneNumber(String phoneNumber);

	/**
	 * Email ünvanı ilə istifadəçi tapır Email unikliyini yoxlamaq üçün
	 */
	Optional<User> findByEmail(String email);

	/**
	 * Email-in mövcud olub-olmadığını yoxlayır
	 */
	boolean existsByEmail(String email);

	/**
	 * Roluna görə istifadəçiləri tapır Məsələn, bütün hazırlayanları tapmaq üçün
	 */
	List<User> findByRole(UserRole role);

	/**
	 * Aktiv istifadəçiləri tapır Sistemdə aktiv olan istifadəçilərin siyahısı üçün
	 */
	List<User> findByIsActiveTrue();

	/**
	 * Rol və aktiv statusuna görə istifadəçiləri tapır Məsələn, aktiv
	 * hazırlayanları tapmaq üçün
	 */
	List<User> findByRoleAndIsActiveTrue(UserRole role);

	/**
	 * Ad üzrə axtarış (case-insensitive) İstifadəçi axtarışı üçün
	 */
	@Query("SELECT u FROM User u WHERE LOWER(u.fullName) LIKE LOWER(CONCAT('%', :name, '%'))")
	Page<User> findByFullNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);

	/**
	 * Telefon nömrəsi və ya ad üzrə axtarış Ümumi axtarış funksiyası üçün
	 */
	@Query("SELECT u FROM User u WHERE " + "LOWER(u.fullName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR "
			+ "u.phoneNumber LIKE CONCAT('%', :searchTerm, '%')")
	Page<User> findByFullNameOrPhoneNumber(@Param("searchTerm") String searchTerm, Pageable pageable);

	/**
	 * Müəyyən tarixdən sonra qeydiyyatdan keçən istifadəçilər Statistika üçün
	 * faydalıdır
	 */
	@Query("SELECT u FROM User u WHERE u.createdAt >= :fromDate ORDER BY u.createdAt DESC")
	List<User> findUsersRegisteredAfter(@Param("fromDate") LocalDateTime fromDate);

	/**
	 * Ən aktiv müştəriləri tapır (sifariş sayına görə) Bu metodda JOIN istifadə
	 * edirik
	 */
//	@Query("SELECT u FROM User u " + "LEFT JOIN Order o ON u.id = o.customer.id "
//			+ "WHERE u.role = 'CUSTOMER' AND u.isActive = true " + "GROUP BY u.id " + "ORDER BY COUNT(o.id) DESC")

	@Query(value = "SELECT u.* FROM users u LEFT JOIN orders o ON u.id = o.user_id "
			+ "WHERE u.role = 'CUSTOMER' AND u.is_active = true " + "GROUP BY u.id "
			+ "ORDER BY COUNT(o.id) DESC", nativeQuery = true)
	Page<User> findMostActiveCustomers(Pageable pageable);

	/**
	 * Son aktivliyinə görə istifadəçiləri tapır Son giriş tarixini yoxlamaq üçün
	 * (əgər belə sahə əlavə edərsək)
	 */
	List<User> findTop10ByIsActiveTrueOrderByUpdatedAtDesc();

	/**
	 * Rol statistikası üçün - hər roldan neçə istifadəçi var
	 */
	@Query("SELECT u.role as role, COUNT(u) as count FROM User u WHERE u.isActive = true GROUP BY u.role")
	List<Object[]> getUserRoleStatistics();

	/**
	 * Custom delete metodu - soft delete (məlumatı silmir, sadəcə aktiv statusunu
	 * dəyişir) Məlumat itkisinin qarşısını almaq üçün
	 */
	@Query("UPDATE User u SET u.isActive = false WHERE u.id = :userId")
	void softDeleteUser(@Param("userId") UUID userId);

	/**
	 * İstifadəçini yenidən aktivləşdirmək üçün
	 */
	@Query("UPDATE User u SET u.isActive = true WHERE u.id = :userId")
	void reactivateUser(@Param("userId") UUID userId);


}