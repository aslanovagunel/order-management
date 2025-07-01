package com.app.yolla.modules.order.dto;

import java.util.UUID;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderItemUpdateRequest {
	@NotNull(message = "Məhsul ID-si boş ola bilməz")
	private UUID productId;

	@NotNull(message = "Miqdar boş ola bilməz")
	@Min(value = 1, message = "Miqdar minimum 1 olmalıdır")
	private Integer quantity;
}
