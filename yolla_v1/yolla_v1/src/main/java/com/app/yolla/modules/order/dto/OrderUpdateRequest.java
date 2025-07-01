package com.app.yolla.modules.order.dto;

import java.util.List;

import com.app.yolla.modules.order.entity.OrderStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * İstifadəçi Yeniləmə Request DTO
 * <p>
 * Bu sinif mövcud istifadəçi məlumatlarını yeniləmək üçün istifadə olunur.
 * Bütün sahələr ixtiyaridir - yalnız dəyişdirilən sahələr göndərilir.
 * <p>
 * Analogi: Bu sinif "profil redaktə formu" kimidir - istifadəçi
 * yalnız dəyişdirmək istədiyi sahələri doldurur.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderUpdateRequest {
	@NotNull(message = "Status boş ola bilməz")
	private OrderStatus status;

	@Size(max = 500, message = "Qeyd 500 simvoldan uzun ola bilməz")
	private String notes;

	@Size(max = 255, message = "Ünvan 255 simvoldan uzun ola bilməz")
	@NotBlank(message = "Çatdırılma ünvanı boş ola bilməz")
	private String deliveryAddress;

	private List<OrderItemUpdateRequest> items;

}