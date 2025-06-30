package com.app.yolla.modules.order.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.yolla.modules.order.dto.OrderCreateRequest;
import com.app.yolla.modules.order.dto.OrderDTO;
import com.app.yolla.modules.order.dto.OrderItemRequest;
import com.app.yolla.modules.order.dto.OrderResponse;
import com.app.yolla.modules.order.entity.Order;
import com.app.yolla.modules.order.entity.OrderItem;
import com.app.yolla.modules.order.repository.OrderRepository;
import com.app.yolla.modules.product.entity.Product;
import com.app.yolla.modules.product.service.ProductService;
import com.app.yolla.modules.user.dto.UserDTO;
import com.app.yolla.modules.user.service.UserService;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class OrderService {

	@Autowired
	private OrderRepository repository;

	@Autowired
	private ModelMapper mapper;

	@Autowired
	private UserService userService;

	@Autowired
	private ProductService productService;

	public OrderDTO createdOrder(OrderCreateRequest request) {
		String phone = (String) userService.findPhone();
		UserDTO byPhoneNumber = userService.findByPhoneNumber(phone);

		Order order = new Order();
		mapper.map(request, order);
		order.setCreatedAt(LocalDateTime.now());
		order.setUserId(byPhoneNumber.getId());
		

		List<OrderItemRequest> items2 = request.getItems();
		List<OrderItem> items = new ArrayList<>();

		
		for (OrderItemRequest req : items2) {
			Product product = productService.findProduct(req.getProductId());

		    OrderItem orderItem = new OrderItem();
		    orderItem.setOrder(order);
		    orderItem.setProduct(product);
			orderItem.setQuantity(req.getQuantity());
			orderItem.setQuantity(product.getStockQuantity());

		    items.add(orderItem);
		}

		order.setItems(items);

		repository.save(order);

		return convertToDTO(order);
	}

	private OrderDTO convertToDTO(Order order) {
		return new OrderDTO(order.getId(), order.getUserId(), order.getStatus(), order.getTotalAmount(),
				order.getCreatedAt(), order.getNotes());
	}

	public OrderResponse getAll(Integer begin, Integer length) {
		String phone = (String) userService.findPhone();
		UserDTO en = userService.findByPhoneNumber(phone);

		OrderResponse response = new OrderResponse();
		List<Order> all = repository.getAll(en.getId(), begin, length);
		List<OrderDTO> list = new ArrayList<OrderDTO>();

		for (Order order : all) {
			OrderDTO convertToDTO = convertToDTO(order);
			list.add(convertToDTO);
		}
		response.setList(list);
		return response;
	}

	public List<Order> findOrder(UUID id) {
		List<Order> p=repository.findOrder(id);
		return p;
}

public Order findById(UUID id) {
	Optional<Order> op = repository.findById(id);
	Order o = op.get();
	return o;
}
}