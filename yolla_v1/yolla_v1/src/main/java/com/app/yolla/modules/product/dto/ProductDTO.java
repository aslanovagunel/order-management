package com.app.yolla.modules.product.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import jakarta.persistence.Column;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ProductDTO {
	private UUID id;

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

	private Boolean active;

	@CreatedDate
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@LastModifiedDate
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	public ProductDTO(UUID id,
			@NotBlank(message = "Məhsulun adı boş ola bilməz") @Size(max = 100, message = "Məhsulun adı ən çox 100 simvol ola bilər") String name,
			@Size(max = 500, message = "Təsvir ən çox 500 simvol ola bilər") String description,
			@NotNull(message = "Qiymət boş ola bilməz") @DecimalMin(value = "0.0", inclusive = false, message = "Qiymət 0-dan böyük olmalıdır") @Digits(integer = 10, fraction = 2, message = "Qiymət maksimum 10 tam və 2 onluq rəqəm ola bilər") BigDecimal price,
			@NotNull(message = "Stok miqdarı boş ola bilməz") @Min(value = 0, message = "Stok miqdarı mənfi ola bilməz") Integer stockQuantity,
			Boolean active, LocalDateTime createdAt, LocalDateTime updatedAt) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.price = price;
		this.stockQuantity = stockQuantity;
		this.active = active;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public ProductDTO() {
		super();
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public Integer getStockQuantity() {
		return stockQuantity;
	}

	public void setStockQuantity(Integer stockQuantity) {
		this.stockQuantity = stockQuantity;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}


}
