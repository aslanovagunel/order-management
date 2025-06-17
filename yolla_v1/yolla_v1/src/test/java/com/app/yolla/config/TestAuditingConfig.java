package com.app.yolla.config;

import java.util.Optional;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;

@TestConfiguration
public class TestAuditingConfig {
	@Bean
	public AuditorAware<String> auditorAware() {
		return () -> Optional.of("test-user");
	}
}
