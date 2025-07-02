package com.app.yolla.modules.order.dto;

import java.util.List;

import com.app.yolla.modules.order.entity.OrderStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateRequest {


	@NotNull(message = "Sifariş statusu boş ola bilməz")
	private OrderStatus status = OrderStatus.PENDING;

	@NotBlank(message = "Çatdırılma ünvanı boş ola bilməz")
	@Size(max = 255, message = "Çatdırılma ünvanı 255 simvoldan uzun ola bilməz")
	private String deliveryAddress;

	@Size(max = 500, message = "Qeyd 500 simvoldan uzun ola bilməz")
	private String notes;

	@NotEmpty(message = "Sifariş maddələri boş ola bilməz")
	private List<OrderItemRequest> items;
}