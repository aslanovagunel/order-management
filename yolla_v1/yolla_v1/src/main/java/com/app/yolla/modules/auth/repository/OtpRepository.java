package com.app.yolla.modules.auth.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.app.yolla.modules.auth.entity.OtpCode;
import com.app.yolla.modules.auth.entity.OtpType;

/**
 * OTP Repository İnterfeysi
 * <p>
 * Bu interfeys OTP kodları ilə əlaqəli verilənlər bazası əməliyyatlarını həyata keçirir.
 */
@Repository
public interface OtpRepository extends JpaRepository<OtpCode, UUID> {

    /**
     * Telefon nömrəsi və OTP növünə görə ən son aktiv OTP-ni tapır
     */
    Optional<OtpCode> findTopByPhoneNumberAndOtpTypeAndIsUsedFalseOrderByCreatedAtDesc(
            String phoneNumber, OtpType otpType);

    /**
     * Telefon nömrəsi və OTP növünə görə bütün aktiv OTP-ləri tapır
     */
    List<OtpCode> findByPhoneNumberAndOtpTypeAndIsUsedFalse(String phoneNumber, OtpType otpType);

    /**
     * Müəyyən vaxtdan sonra yaradılmış OTP-lərin sayını qaytarır
     */
    long countByPhoneNumberAndOtpTypeAndCreatedAtAfter(
            String phoneNumber, OtpType otpType, LocalDateTime createdAfter);

    /**
     * Bugün göndərilən OTP sayını qaytarır
     */
    long countByCreatedAtAfter(LocalDateTime createdAfter);

    /**
     * Bugün uğurla doğrulanmış OTP sayını qaytarır
     */
    long countByCreatedAtAfterAndIsUsedTrue(LocalDateTime createdAfter);

    /**
     * Telefon nömrəsi və OTP növünə görə bütün OTP-ləri istifadə olunmuş kimi qeyd edir
     */
    @Modifying
    @Query("UPDATE OtpCode o SET o.isUsed = true WHERE o.phoneNumber = :phoneNumber AND o.otpType = :otpType AND o.isUsed = false")
    void markAllAsUsedByPhoneNumberAndOtpType(@Param("phoneNumber") String phoneNumber,
                                              @Param("otpType") OtpType otpType);

    /**
     * Müəyyən vaxtdan əvvəl yaradılmış OTP-ləri silir
     */
    @Modifying
    @Query("DELETE FROM OtpCode o WHERE o.createdAt < :createdBefore")
    int deleteByCreatedAtBefore(@Param("createdBefore") LocalDateTime createdBefore);

    /**
     * Vaxtı bitmiş OTP-ləri tapır
     */
    List<OtpCode> findByExpiresAtBeforeAndIsUsedFalse(LocalDateTime currentTime);

    /**
     * IP ünvanına görə son OTP-ləri tapır (spam qarşısında)
     */
    List<OtpCode> findByIpAddressAndCreatedAtAfterOrderByCreatedAtDesc(
            String ipAddress, LocalDateTime createdAfter);

    /**
     * Müəyyən telefon nömrəsi üçün son OTP-ləri tapır
     */
    List<OtpCode> findTop5ByPhoneNumberOrderByCreatedAtDesc(String phoneNumber);

    /**
     * OTP növünə görə statistika
     */
    @Query("SELECT o.otpType as otpType, COUNT(o) as count FROM OtpCode o WHERE o.createdAt >= :fromDate GROUP BY o.otpType")
    List<Object[]> getOtpStatisticsByType(@Param("fromDate") LocalDateTime fromDate);

    /**
     * Uğur dərəcəsi statistikası
     */
    @Query("SELECT " +
            "COUNT(o) as total, " +
            "SUM(CASE WHEN o.isUsed = true THEN 1 ELSE 0 END) as successful " +
            "FROM OtpCode o WHERE o.createdAt >= :fromDate")
    Object[] getSuccessRateStatistics(@Param("fromDate") LocalDateTime fromDate);
}