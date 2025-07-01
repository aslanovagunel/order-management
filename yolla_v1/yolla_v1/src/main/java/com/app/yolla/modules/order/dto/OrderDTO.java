package com.app.yolla.modules.order.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.app.yolla.modules.order.entity.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {

	private UUID id;

	private UUID userId;

	private String userPhone;
	private String userFullName;

	private OrderStatus status;

	private BigDecimal totalAmount;

	private String deliveryAddress;

	private String notes;

	private LocalDateTime createdAt;

	private List<OrderItemDTO> items;

	private LocalDateTime deliveryTime;
}