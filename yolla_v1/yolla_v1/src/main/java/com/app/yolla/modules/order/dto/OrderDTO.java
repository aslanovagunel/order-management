package com.app.yolla.modules.order.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import com.app.yolla.modules.order.entity.OrderStatus;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {

	private UUID id;

	private UUID userId;

	@Enumerated(EnumType.STRING)
	private OrderStatus status;

	@Column(nullable = false)
	private BigDecimal totalAmount;

	@CreationTimestamp
	private LocalDateTime createdAt;

	private String notes;
}