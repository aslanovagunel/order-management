package com.app.yolla.modules.product.dto;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductUpdateRequest {
	@NotNull(message = "Məhsulun id'si boş ola bilməz")
	private UUID id;

	private String name;

	private String description;

	private BigDecimal price;

	private Integer stockQuantity;
}
