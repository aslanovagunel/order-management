package com.app.yolla.modules.order.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemRequest {
	@NotNull(message = "Məhsul ID-si boş ola bilməz")
	private UUID productId;

	@NotNull(message = "Miqdar boş ola bilməz")
	private Integer quantity;
}
