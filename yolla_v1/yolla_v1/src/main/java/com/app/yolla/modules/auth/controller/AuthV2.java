package com.app.yolla.modules.auth.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.yolla.modules.auth.dto.AuthResponse;
import com.app.yolla.modules.auth.dto.LoginRequest;
import com.app.yolla.modules.auth.dto.OtpVerificationRequest;
import com.app.yolla.modules.auth.service.AuthService;
import com.app.yolla.shared.dto.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping(path = "/auth/v2")
public class AuthV2 {

	private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
	
	@Autowired
	private AuthService authService;

	@PostMapping(path = "/login")
	public ResponseEntity<ApiResponse<String>> sendOtp(@Valid @RequestBody LoginRequest request,
			HttpServletRequest httpRequest) {

		logger.info("OTP göndərmə sorğusu: telefon={}", request.getPhoneNumber());

		try {
			// IP ünvanını əldə et
			String ipAddress = getClientIpAddress(httpRequest);

			String result = authService.sendOtp(request.getPhoneNumber(), ipAddress);

			ApiResponse<String> response = ApiResponse.success("OTP kodu telefon nömrənizə göndərildi", result);

			return ResponseEntity.ok(response);

		} catch (Exception e) {
			logger.error("OTP göndərmə xətası: telefon={}", request.getPhoneNumber(), e);

			ApiResponse<String> response = ApiResponse.error("OTP göndərilmədi: " + e.getMessage());

			return ResponseEntity.badRequest().body(response);
		}
	}

	private String getClientIpAddress(HttpServletRequest request) {
		String xForwardedForHeader = request.getHeader("X-Forwarded-For");
		if (xForwardedForHeader == null) {
			return request.getRemoteAddr();
		} else {
			// X-Forwarded-For header-i vergüllə ayrılmış IP-lərin siyahısını ehtiva edə
			// bilər
			return xForwardedForHeader.split(",")[0].trim();
		}
	}

	@PostMapping("/verify-otp")
	public ResponseEntity<ApiResponse<AuthResponse>> verifyOtp(@Valid @RequestBody OtpVerificationRequest request) {

		logger.info("OTP doğrulama sorğusu: telefon={}", request.getPhoneNumber());

		try {
			AuthResponse authResponse = authService.verifyOtpAndLogin(request.getPhoneNumber(), request.getOtpCode());

			ApiResponse<AuthResponse> response = ApiResponse.success("Uğurla giriş etdiniz", authResponse);

			return ResponseEntity.ok(response);

		} catch (Exception e) {
			logger.error("OTP doğrulama xətası: telefon={}", request.getPhoneNumber(), e);

			ApiResponse<AuthResponse> response = ApiResponse.error("Giriş uğursuz: " + e.getMessage());

			return ResponseEntity.badRequest().body(response);
		}
	}

}
