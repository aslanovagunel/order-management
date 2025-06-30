package com.app.yolla.modules.user.controller;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.yolla.modules.user.dto.UserCreateRequest;
import com.app.yolla.modules.user.dto.UserDTO;
import com.app.yolla.modules.user.dto.UserUpdateRequest;
import com.app.yolla.modules.user.entity.UserRole;
import com.app.yolla.modules.user.service.UserService;
import com.app.yolla.shared.dto.ApiResponse;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * İstifadəçi Controller Sinfi
 * <p>
 * Bu sinif HTTP request-lərini qəbul edir və müvafiq cavabları qaytarır.
 * RESTful API endpoint-lərini təmin edir.
 * <p>
 * Analogi: Bu sinif bir "resepsiyonçu" kimidir - müştərilərin sorğularını
 * qəbul edir və düzgün şöbəyə yönləndirir.
 */

@Tag(name = "User", description = "İstifadəçi idarəetməsi əməliyyatları")
@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*") // CORS icazəsi
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    /**
     * Yeni istifadəçi yaradır
     * POST /users
     * Yalnız admin-lər istifadə edə bilər
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserDTO>> createUser(
            @Valid @RequestBody UserCreateRequest request) {

        logger.info("Yeni istifadəçi yaratma sorğusu: {}", request.getPhoneNumber());

        try {
            UserDTO createdUser = userService.createUser(request);

            ApiResponse<UserDTO> response = new ApiResponse<>(
                    true,
                    "İstifadəçi uğurla yaradıldı",
                    createdUser
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            logger.error("İstifadəçi yaratma xətası: ", e);

            ApiResponse<UserDTO> response = new ApiResponse<>(
                    false,
                    "İstifadəçi yaradılarkən xəta baş verdi: " + e.getMessage(),
                    null
            );

            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * İstifadəçini ID ilə tapır
     * GET /users/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userService.findById(#id).phoneNumber == authentication.name")
	public ResponseEntity<ApiResponse<UserDTO>> getUserById(@PathVariable("id") UUID id) {

        logger.debug("İstifadəçi sorğusu: ID={}", id);

        try {
            UserDTO user = userService.findById(id);

            ApiResponse<UserDTO> response = new ApiResponse<>(
                    true,
                    "İstifadəçi tapıldı",
                    user
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("İstifadəçi tapma xətası: ID={}", id, e);

            ApiResponse<UserDTO> response = new ApiResponse<>(
                    false,
                    "İstifadəçi tapılmadı: " + e.getMessage(),
                    null
            );

			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * Telefon nömrəsi ilə istifadəçi tapır
     * GET /users/phone/{phoneNumber}
     */
    @GetMapping("/phone/{phoneNumber}")
    @PreAuthorize("hasRole('ADMIN') or #phoneNumber == authentication.name")
    public ResponseEntity<ApiResponse<UserDTO>> getUserByPhone(
			@PathVariable(name = "phoneNumber") String phoneNumber) {

        logger.debug("Telefon nömrəsi ilə istifadəçi sorğusu: {}", phoneNumber);

        try {
            UserDTO user = userService.findByPhoneNumber(phoneNumber);

            ApiResponse<UserDTO> response = new ApiResponse<>(
                    true,
                    "İstifadəçi tapıldı",
                    user
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Telefon nömrəsi ilə istifadəçi tapma xətası: {}", phoneNumber, e);

            ApiResponse<UserDTO> response = new ApiResponse<>(
                    false,
                    "İstifadəçi tapılmadı: " + e.getMessage(),
                    null
            );

			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * Bütün istifadəçiləri siyahıya alır (səhifələməklə)
     * GET /users?page=0&size=10&sort=createdAt,desc
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<UserDTO>>> getAllUsers(
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "10") int size,
			@RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
			@RequestParam(name = "sortDir", defaultValue = "desc") String sortDir) {

        logger.debug("Bütün istifadəçilər sorğusu: page={}, size={}", page, size);

        try {
            // Sort direction təyin et
            Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ?
                    Sort.Direction.DESC : Sort.Direction.ASC;

            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
            Page<UserDTO> users = userService.findAllUsers(pageable);

            ApiResponse<Page<UserDTO>> response = new ApiResponse<>(
                    true,
                    "İstifadəçilər uğurla tapıldı",
                    users
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("İstifadəçiləri siyahıya alma xətası: ", e);

            ApiResponse<Page<UserDTO>> response = new ApiResponse<>(
                    false,
                    "İstifadəçiləri siyahıya alarkən xəta baş verdi: " + e.getMessage(),
                    null
            );

            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * İstifadəçi məlumatlarını yeniləyir
     * PUT /users/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userService.findById(#id).phoneNumber == authentication.name")
    public ResponseEntity<ApiResponse<UserDTO>> updateUser(
			@PathVariable("id") UUID id,
            @Valid @RequestBody UserUpdateRequest request) {

        logger.info("İstifadəçi yeniləmə sorğusu: ID={}", id);

        try {
            UserDTO updatedUser = userService.updateUser(id, request);

            ApiResponse<UserDTO> response = new ApiResponse<>(
                    true,
                    "İstifadəçi uğurla yeniləndi",
                    updatedUser
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("İstifadəçi yeniləmə xətası: ID={}", id, e);

            ApiResponse<UserDTO> response = new ApiResponse<>(
                    false,
                    "İstifadəçi yenilənərkən xəta baş verdi: " + e.getMessage(),
                    null
            );

            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * İstifadəçini deaktiv edir
     * DELETE /users/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponse<Void>> deactivateUser(@PathVariable("id") UUID id) {

        logger.info("İstifadəçi deaktiv etmə sorğusu: ID={}", id);

        try {
            userService.deactivateUser(id);

            ApiResponse<Void> response = new ApiResponse<>(
                    true,
                    "İstifadəçi uğurla deaktiv edildi",
                    null
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("İstifadəçi deaktiv etmə xətası: ID={}", id, e);

            ApiResponse<Void> response = new ApiResponse<>(
                    false,
                    "İstifadəçi deaktiv edilərkən xəta baş verdi: " + e.getMessage(),
                    null
            );

            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * İstifadəçini yenidən aktivləşdirir
     * PUT /users/{id}/reactivate
     */
    @PutMapping("/{id}/reactivate")
    @PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponse<Void>> reactivateUser(@PathVariable("id") UUID id) {

        logger.info("İstifadəçi aktivləşdirmə sorğusu: ID={}", id);

        try {
            userService.reactivateUser(id);

            ApiResponse<Void> response = new ApiResponse<>(
                    true,
                    "İstifadəçi uğurla aktivləşdirildi",
                    null
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("İstifadəçi aktivləşdirmə xətası: ID={}", id, e);

            ApiResponse<Void> response = new ApiResponse<>(
                    false,
                    "İstifadəçi aktivləşdirilərkən xəta baş verdi: " + e.getMessage(),
                    null
            );

            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Roluna görə istifadəçiləri tapır
     * GET /users/role/{role}
     */
    @GetMapping("/role/{role}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PREPARER')")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getUsersByRole(
			@Parameter(description = "Rolu böyük hərflərlə daxil edin: ADMIN, CUSTOMER, PREPARER", example = "ADMIN") @PathVariable(name = "role") UserRole role) {

        logger.debug("Rol üzrə istifadəçi sorğusu: {}", role);

        try {
            List<UserDTO> users = userService.findUsersByRole(role);

            ApiResponse<List<UserDTO>> response = new ApiResponse<>(
                    true,
                    "İstifadəçilər tapıldı",
                    users
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Rol üzrə istifadəçi tapma xətası: {}", role, e);

            ApiResponse<List<UserDTO>> response = new ApiResponse<>(
                    false,
                    "İstifadəçilər tapılarkən xəta baş verdi: " + e.getMessage(),
                    null
            );

            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Ad üzrə istifadəçi axtarışı
     * GET /users/search?name=john&page=0&size=10
     */
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<UserDTO>>> searchUsers(
			@RequestParam(name = "name") String name, @RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "10") int size) {

        logger.debug("İstifadəçi axtarış sorğusu: name={}", name);

        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<UserDTO> users = userService.searchUsersByName(name, pageable);

            ApiResponse<Page<UserDTO>> response = new ApiResponse<>(
                    true,
                    "Axtarış nəticələri",
                    users
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("İstifadəçi axtarış xətası: name={}", name, e);

            ApiResponse<Page<UserDTO>> response = new ApiResponse<>(
                    false,
                    "Axtarış zamanı xəta baş verdi: " + e.getMessage(),
                    null
            );

            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Son qeydiyyatdan keçən istifadəçiləri tapır
     * GET /users/recent?days=7
     */
    @GetMapping("/recent")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getRecentUsers(
            @RequestParam(defaultValue = "7") int days) {

        logger.debug("Son istifadəçilər sorğusu: days={}", days);

        try {
            List<UserDTO> users = userService.findRecentUsers(days);

            ApiResponse<List<UserDTO>> response = new ApiResponse<>(
                    true,
                    "Son " + days + " gündə qeydiyyatdan keçən istifadəçilər",
                    users
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Son istifadəçilər sorğusu xətası: days={}", days, e);

            ApiResponse<List<UserDTO>> response = new ApiResponse<>(
                    false,
                    "Son istifadəçiləri taparkən xəta baş verdi: " + e.getMessage(),
                    null
            );

            return ResponseEntity.badRequest().body(response);
        }
    }
}