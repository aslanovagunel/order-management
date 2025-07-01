package com.app.yolla.modules.product.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ProductUpdateRequest {

	private String name;

	private String description;

	private BigDecimal price;

	private Integer stockQuantity;
}
