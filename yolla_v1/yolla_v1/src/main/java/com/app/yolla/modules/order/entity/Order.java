package com.app.yolla.modules.order.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "orders")
@EntityListeners(AuditingEntityListener.class) // Avtomatik tarix yazmaq üçün
public class Order {
	@Id
	@GeneratedValue
	private UUID id;

	private UUID userId;

	@Enumerated(EnumType.STRING)
	private OrderStatus status;

	@Column(nullable = false)
	private BigDecimal totalAmount;

	@CreationTimestamp
	private LocalDateTime createdAt;

	private String notes;

	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<OrderItem> items;


}