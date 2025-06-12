package com.app.yolla.modules.order.dto;

import java.util.List;

import lombok.Data;

@Data
public class OrderResponse {
	private List<OrderDTO> list;
}
