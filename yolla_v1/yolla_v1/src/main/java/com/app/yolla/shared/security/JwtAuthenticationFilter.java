package com.app.yolla.shared.security;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * JWT Authentication Filter (JWT library olmadan)
 * <p>
 * Bu sinif hər HTTP sorğusunu yoxlayır və custom token-ın etibarlı olub-olmadığını təsdiq edir.
 * JWT library əvəzinə custom token system istifadə edir.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

    // UserService şimdilik söndürülüb - database olmadığı üçün
    // @Autowired
    // private UserService userService;

    /**
     * Hər HTTP sorğusu üçün çalışan əsas filter metodu
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String requestTokenHeader = request.getHeader("Authorization");
        final String requestURI = request.getRequestURI();

        // Debug log
        logger.debug("JWT Filter: URI={}, Authorization={}",
                requestURI,
                requestTokenHeader != null ? "Present" : "Not Present");

        String phoneNumber = null;
        String jwtToken = null;

        // Authorization header-dan JWT token çıxar
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);

            try {
                phoneNumber = jwtUtil.getPhoneNumberFromToken(jwtToken);
                logger.debug("Token-dan telefon nömrəsi çıxarıldı: {}", phoneNumber);

            } catch (IllegalArgumentException e) {
                logger.error("Token alınamadı", e);
            } catch (Exception e) {
                logger.warn("Token vaxtı bitib və ya etibarsızdır: {}", e.getMessage());
            }
        } else {
            logger.debug("Authorization header mövcud deyil və ya Bearer ilə başlamır");
        }

        // Token etibarlıdırsa və istifadəçi authentication olunmamışsa
        if (phoneNumber != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            try {
                // Token-ın etibarlılığını yoxla
                if (jwtUtil.isTokenValid(jwtToken) && jwtUtil.isAccessToken(jwtToken)) {

                    // İstifadəçi məlumatlarını əldə et
                    String role = jwtUtil.getRoleFromToken(jwtToken);
                    Long userId = jwtUtil.getUserIdFromToken(jwtToken);

                    // Spring Security authorities yaradırıq
                    List<SimpleGrantedAuthority> authorities = List.of(
                            new SimpleGrantedAuthority("ROLE_" + role)
                    );

                    // Authentication obyektini yaradırıq
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(phoneNumber, null, authorities);

                    // Request məlumatlarını əlavə edirik
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Security context-ə əlavə edirik
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    logger.debug("İstifadəçi uğurla authentication oldu: telefon={}, rol={}, userId={}",
                            phoneNumber, role, userId);

                } else {
                    logger.warn("Token etibarsızdır və ya access token deyil");
                }

            } catch (Exception e) {
                logger.error("Authentication zamanı xəta: {}", e.getMessage());
                SecurityContextHolder.clearContext();
            }
        }

        // Filter chain-i davam etdir
        filterChain.doFilter(request, response);
    }

    /**
     * Bu filter-in hansı sorğular üçün işləməli olduğunu müəyyən edir
     * Bəzi URL-lər üçün filter-i ötürmək olar (performance üçün)
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();

        // Bu URL-lər üçün JWT yoxlaması etmirik
        return path.startsWith("/auth/") ||
				path.startsWith("/auth/v2/")
				||
                path.startsWith("/api/v1/auth/") ||
                path.startsWith("/health/") ||
                path.startsWith("/actuator/") ||
                path.startsWith("/swagger-ui/") ||
                path.startsWith("/v3/api-docs/") ||
				path.startsWith("/api/v1/h2-console/") ||
                path.equals("/") ||
                path.equals("/favicon.ico");
    }
}