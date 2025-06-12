package com.app.yolla.modules.product.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductAddRequest {
	@NotBlank(message = "Məhsulun adı boş ola bilməz")
	@Size(max = 100, message = "Məhsulun adı ən çox 100 simvol ola bilər")
	private String name;

	@Size(max = 500, message = "Təsvir ən çox 500 simvol ola bilər")
	private String description;

	@NotNull(message = "Qiymət boş ola bilməz")
	@DecimalMin(value = "0.0", inclusive = false, message = "Qiymət 0-dan böyük olmalıdır")
	@Digits(integer = 10, fraction = 2, message = "Qiymət maksimum 10 tam və 2 onluq rəqəm ola bilər")
	private BigDecimal price;

	@NotNull(message = "Stok miqdarı boş ola bilməz")
	@Min(value = 0, message = "Stok miqdarı mənfi ola bilməz")
	private Integer stockQuantity;




}
