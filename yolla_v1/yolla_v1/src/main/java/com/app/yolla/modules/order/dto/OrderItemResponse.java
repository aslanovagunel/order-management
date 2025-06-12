package com.app.yolla.modules.order.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class OrderItemResponse {
	private Long orderId;
	private BigDecimal totalAmount;
	private String status;
	private LocalDateTime createdAt;
	private List<OrderItemResponseDTO> items;
}
