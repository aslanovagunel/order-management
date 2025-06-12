package com.app.yolla;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Yolla Sifariş İdarəetməsi Sisteminin əsas başlanğıc sinfi
 * <p>
 * Müvəqqəti olaraq sadə versiya - database olmadan işləyir.
 */
@SpringBootApplication
@EnableJpaAuditing
//@EnableJpaRepositories(basePackages = {"com.app.yolla.modules.user.repository"})
//@EntityScan(basePackages = {"com.app.yolla.modules.user.entity"})
public class YollaApplication {

    public static void main(String[] args) {
        SpringApplication.run(YollaApplication.class, args);
        System.out.println("🚀 Yolla Sifariş Sistemi uğurla başladı!");
        System.out.println("📱 API əlçatan: http://localhost:8080/api/v1");
        System.out.println("🔍 Health Check: http://localhost:8080/api/v1/auth/health");
        System.out.println("🔑 Test Token: http://localhost:8080/api/v1/auth/test-token");
    }
}