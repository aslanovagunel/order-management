package com.app.yolla.modules.user.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.yolla.modules.user.dto.UserCreateRequest;
import com.app.yolla.modules.user.dto.UserDTO;
import com.app.yolla.modules.user.dto.UserUpdateRequest;
import com.app.yolla.modules.user.entity.User;
import com.app.yolla.modules.user.entity.UserRole;
import com.app.yolla.modules.user.repository.UserRepository;
import com.app.yolla.shared.exception.DuplicateResourceException;
import com.app.yolla.shared.exception.ResourceNotFoundException;

/**
 * İstifadəçi Xidmət Sinfi
 * <p>
 * Bu sinif bütün istifadəçi əlaqəli biznes məntiqi əməliyyatlarını həyata keçirir.
 * Controller və Repository arasında körpü rolunu oynayır.
 * <p>
 * Analogi: Bu sinif bir "müdir" kimidir - qərarları verir,
 * qaydaları yoxlayır və əməliyyatları koordinasiya edir.
 */
@Service
//@Transactional
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    /**
     * Telefon nömrəsi ilə istifadəçi tapır
     * OTP sistemi və giriş üçün əsas metoddur
     */
    public UserDTO findByPhoneNumber(String phoneNumber) {
        logger.debug("Telefon nömrəsi ilə istifadəçi axtarılır: {}", phoneNumber);

        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Bu telefon nömrəsi ilə istifadəçi tapılmadı: " + phoneNumber));

        return convertToDTO(user);
    }

    /**
     * Yeni istifadəçi yaradır
     * Qeydiyyat prosesi üçün istifadə olunur
     */
    public UserDTO createUser(UserCreateRequest request) {
        logger.info("Yeni istifadəçi yaradılır: {}", request.getPhoneNumber());

        // Telefon nömrəsinin təkrarlanmamasını yoxla
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new DuplicateResourceException(
                    "Bu telefon nömrəsi artıq qeydiyyatdan keçib: " + request.getPhoneNumber());
        }

        // Email-in təkrarlanmamasını yoxla (əgər verilmişsə)
        if (request.getEmail() != null && userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException(
                    "Bu email artıq istifadə olunur: " + request.getEmail());
        }

        // Yeni istifadəçi entity yaradırıq
        User user = new User();
        user.setPhoneNumber(request.getPhoneNumber());
		user.setFullName(request.getFullName());
		user.setEmail(request.getEmail());
        user.setRole(request.getRole());
        user.setIsActive(true);

        // Verilənlər bazasına saxla
        User savedUser = userRepository.save(user);

        logger.info("İstifadəçi uğurla yaradıldı: ID={}, Telefon={}",
                savedUser.getId(), savedUser.getPhoneNumber());

        return convertToDTO(savedUser);
    }

    /**
     * İstifadəçi məlumatlarını yeniləyir
     */
	public UserDTO updateUser(UUID userId, UserUpdateRequest request) {
        logger.info("İstifadəçi yenilənir: ID={}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "İstifadəçi tapılmadı: " + userId));

        // Yalnız dəyişdirilən sahələri yenilə
		if (request.getFullName() != null && !request.getFullName().trim().isEmpty()) {
            user.setFullName(request.getFullName());
        }

		if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            // Email-in digər istifadəçi tərəfindən istifadə olunmadığını yoxla
            if (!request.getEmail().equals(user.getEmail()) &&
                    userRepository.existsByEmail(request.getEmail())) {
                throw new DuplicateResourceException(
                        "Bu email artıq istifadə olunur: " + request.getEmail());
            }
            user.setEmail(request.getEmail());
        }

		if (request.getRole() != null) {
            user.setRole(request.getRole());
        }

		if (request.getIsActive() != null) {
            user.setIsActive(request.getIsActive());
        }

        User savedUser = userRepository.save(user);

        logger.info("İstifadəçi uğurla yeniləndi: ID={}", savedUser.getId());

        return convertToDTO(savedUser);
    }

    /**
     * İstifadəçini ID ilə tapır
     */
    @Transactional(readOnly = true)
	public UserDTO findById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "İstifadəçi tapılmadı: " + userId));

        return convertToDTO(user);
    }

    /**
     * Bütün istifadəçiləri siyahıya alır (səhifələməklə)
     */
    @Transactional(readOnly = true)
    public Page<UserDTO> findAllUsers(Pageable pageable) {
        logger.debug("Bütün istifadəçilər siyahıya alınır");

        return userRepository.findAll(pageable)
                .map(this::convertToDTO);
    }

    /**
     * Aktiv istifadəçiləri tapır
     */
    @Transactional(readOnly = true)
    public List<UserDTO> findActiveUsers() {
        return userRepository.findByIsActiveTrue()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Roluna görə istifadəçiləri tapır
     */
    @Transactional(readOnly = true)
    public List<UserDTO> findUsersByRole(UserRole role) {
        return userRepository.findByRoleAndIsActiveTrue(role)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Ad üzrə istifadəçi axtarışı
     */
    @Transactional(readOnly = true)
    public Page<UserDTO> searchUsersByName(String name, Pageable pageable) {
        return userRepository.findByFullNameContainingIgnoreCase(name, pageable)
                .map(this::convertToDTO);
    }

    /**
     * İstifadəçini soft delete edir (məlumatı silmir, aktiv statusunu dəyişir)
     */
	public void deactivateUser(UUID userId) {
        logger.info("İstifadəçi deaktiv edilir: ID={}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "İstifadəçi tapılmadı: " + userId));

        user.setIsActive(false);
        userRepository.save(user);

        logger.info("İstifadəçi deaktiv edildi: ID={}", userId);
    }

    /**
     * İstifadəçini yenidən aktivləşdirir
     */
	public void reactivateUser(UUID userId) {
        logger.info("İstifadəçi yenidən aktivləşdirilir: ID={}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "İstifadəçi tapılmadı: " + userId));

        user.setIsActive(true);
        userRepository.save(user);

        logger.info("İstifadəçi yenidən aktivləşdirildi: ID={}", userId);
    }

    /**
     * Telefon nömrəsinin mövcudluğunu yoxlayır
     * OTP göndərməzdən əvvəl istifadə olunur
     */
    @Transactional(readOnly = true)
    public boolean existsByPhoneNumber(String phoneNumber) {
        return userRepository.existsByPhoneNumber(phoneNumber);
    }

    /**
     * Son qeydiyyatdan keçən istifadəçiləri tapır
     */
    @Transactional(readOnly = true)
    public List<UserDTO> findRecentUsers(int days) {
        LocalDateTime fromDate = LocalDateTime.now().minusDays(days);
        return userRepository.findUsersRegisteredAfter(fromDate)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Entity-ni DTO-ya çevirmək üçün helper metod
     */
    private UserDTO convertToDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getPhoneNumber(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                user.getIsActive(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }


	public Object findPhone() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();
		return principal;
	}

	public UserDTO getUserByEmail(String email) {
		logger.debug("Email ilə istifadəçi axtarılır: {}", email);

		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("Bu email ilə istifadəçi tapılmadı: " + email));

		return convertToDTO(user);
	}
}