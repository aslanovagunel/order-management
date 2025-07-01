package com.app.yolla.modules.order.dto;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.Data;

@Data
public class OrderItemDTO {
	private UUID productId;
	private String productName;
	private BigDecimal unitPrice;
	private Integer quantity;
}
