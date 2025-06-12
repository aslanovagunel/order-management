package com.app.yolla.modules.order.dto;

import java.math.BigDecimal;
import java.util.List;

import com.app.yolla.modules.order.entity.OrderStatus;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateRequest {

	@NotNull(message = "Sifariş məbləği boş ola bilməz")
	@DecimalMin(value = "0.0", inclusive = false, message = "Sifariş məbləği 0-dan böyük olmalıdır")
	@Digits(integer = 10, fraction = 2, message = "Məbləğ maksimum 10 tam və 2 onluq rəqəm ola bilər")
	private BigDecimal totalAmount;

	@NotNull(message = "Sifariş statusu boş ola bilməz")
	private OrderStatus status = OrderStatus.PENDING;

	private String notes;

	@NotNull(message = "Sifariş maddələri boş ola bilməz")
	private List<OrderItemRequest> items;

}