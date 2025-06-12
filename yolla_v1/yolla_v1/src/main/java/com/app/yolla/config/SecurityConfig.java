package com.app.yolla.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.app.yolla.shared.security.JwtAuthenticationEntryPoint;
import com.app.yolla.shared.security.JwtAuthenticationFilter;

/**
 * Təhlükəsizlik Konfiqurasiya Sinfi
 * <p>
 * Bu sinif bütün təhlükəsizlik qaydalarını müəyyən edir:
 * - JWT token doğrulaması
 * - URL-lərin icazələri
 * - CORS tənzimləmələri
 * - Password şifrələməsi
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // Metod səviyyəsində icazələr üçün
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Parol şifreləmə bean-i
     * BCrypt istifadə edirik - ən təhlükəsiz üsullardan biri
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Authentication Manager bean-i
     * İstifadəçi doğrulaması üçün lazımdır
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * CORS konfiqurasiyası
     * Frontend aplikasiyaları ilə əlaqə üçün lazımdır
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // İcazə verilən domenləri əlavə edin
        configuration.setAllowedOriginPatterns(List.of("*"));

        // İcazə verilən HTTP metodları
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
        ));

        // İcazə verilən headers
        configuration.setAllowedHeaders(List.of("*"));

        // Credentials-a icazə
        configuration.setAllowCredentials(true);

        // Bütün URL-lərə tətbiq et
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    /**
     * Əsas təhlükəsizlik konfiqurasiyası
     * Bu metod hansı URL-lərin açıq, hansılarının qorumalı olduğunu müəyyən edir
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http

                // CSRF-ni söndür (REST API üçün lazım deyil)
                .csrf(AbstractHttpConfigurer::disable)

                // CORS konfiqurasiyasını aktivləşdir
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Session istifadə etmirik (JWT istifadə edirik)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Authentication exception handler
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(jwtAuthenticationEntryPoint))

                // URL icazələri - şimdilik hamısını açıq buraxaq
                .authorizeHttpRequests(authz -> authz
                        // Bütün URL-ləri açıq burax (development üçün)
						.anyRequest().permitAll())
				.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()));

        // JWT filter əlavə et
        http.addFilterBefore(jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}