package com.app.yolla.shared.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.app.yolla.shared.dto.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * JWT Authentication Entry Point
 * <p>
 * Bu sinif authentication xətaları zamanı cavab yaradır.
 * İstifadəçi etibarlı token olmadan qorumalı resursa daxil olmağa çalışdıqda bu işləyir.
 * <p>
 * Analogi: Bu sinif bir "qapıçı" kimidir - kimlik vəsiqəsi olmayan şəxsləri
 * düzgün mesajla geri göndərir.
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationEntryPoint.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Authentication xətası zamanı çalışan metod
     */
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        logger.warn("Authentication xətası: URI={}, Xəta={}",
                request.getRequestURI(), authException.getMessage());

        // HTTP cavab status və content type təyin et
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        // Xəta mesajını hazırla
        String errorMessage = "Bu əməliyyat üçün giriş tələb olunur";
        String errorCode = "AUTHENTICATION_REQUIRED";

        // Xəta növünə görə daha dəqiq mesaj ver
        if (authException.getMessage().contains("JWT")) {
            errorMessage = "Token etibarsızdır və ya vaxtı bitib";
            errorCode = "INVALID_TOKEN";
        } else if (authException.getMessage().contains("Access Denied")) {
            errorMessage = "Bu əməliyyat üçün icazəniz yoxdur";
            errorCode = "ACCESS_DENIED";
        }

        // API cavab obyektini yaradırıq
        ApiResponse<Object> apiResponse = new ApiResponse<>(
                false,
                errorMessage,
                null,
                errorCode
        );

        // JSON olaraq cavab yazırıq
        String jsonResponse = objectMapper.writeValueAsString(apiResponse);
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }
}