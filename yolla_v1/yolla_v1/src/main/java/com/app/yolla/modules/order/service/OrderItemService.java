package com.app.yolla.modules.order.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.yolla.modules.order.dto.OrderItemResponse;
import com.app.yolla.modules.order.dto.OrderItemResponseDTO;
import com.app.yolla.modules.order.entity.Order;
import com.app.yolla.modules.order.entity.OrderItem;
import com.app.yolla.modules.order.repository.OrderItemRepository;
import com.app.yolla.modules.product.entity.Product;
import com.app.yolla.modules.product.service.ProductService;
import com.app.yolla.modules.user.dto.UserDTO;
import com.app.yolla.modules.user.service.UserService;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class OrderItemService {

	@Autowired
	private OrderItemRepository repository;

	@Autowired
	private ModelMapper mapper;

	@Autowired
	private ProductService productService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private UserService userService;

	public OrderItemResponse getOrderItems(UUID id, Integer length, Integer begin) {
		String phone = (String) userService.findPhone();
		UserDTO en = userService.findByPhoneNumber(phone);

		List<OrderItem> orderItems = repository.findByOrderId(id);
		List<OrderItemResponseDTO> list = new ArrayList<OrderItemResponseDTO>();

		Order op = orderService.findByOrder(id);
		if (!op.getUserId().equals(en.getId())) {
			throw new RuntimeException("Bu sifariş sizə aid deyil!");
		}

		for (OrderItem or : orderItems) {
			// Order or = or.getOrder();

			OrderItemResponseDTO dto = new OrderItemResponseDTO();
			Product pr = or.getProduct();
			dto.setPrice(pr.getPrice());
			dto.setProductId(pr.getId());
			dto.setProductName(pr.getName());
			dto.setQuantity(or.getQuantity());

			list.add(dto);
		}

		OrderItemResponse resp = new OrderItemResponse();
		resp.setOrderId(id);
		resp.setStatus("PENDING");
		resp.setCreatedAt(op.getCreatedAt());
		resp.setTotalAmount(op.getTotalAmount());
		resp.setItems(list);

		return resp;
	}
}
