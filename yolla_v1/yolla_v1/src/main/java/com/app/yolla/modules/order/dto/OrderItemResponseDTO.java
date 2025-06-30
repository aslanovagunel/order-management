package com.app.yolla.modules.order.dto;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.Data;

@Data
public class OrderItemResponseDTO {
	private UUID productId;
	private String productName;
	private Integer quantity;
	private BigDecimal price;
}
