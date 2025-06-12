package com.app.yolla.modules.order.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class OrderItemResponseDTO {
	private Long productId;
	private String productName;
	private Integer quantity;
	private BigDecimal price;
}
