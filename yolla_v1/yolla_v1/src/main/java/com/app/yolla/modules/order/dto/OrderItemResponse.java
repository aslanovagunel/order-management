package com.app.yolla.modules.order.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import lombok.Data;

@Data
public class OrderItemResponse {
	private UUID orderId;
	private BigDecimal totalAmount;
	private String status;
	private LocalDateTime createdAt;
	private List<OrderItemResponseDTO> items;
}
