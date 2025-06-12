package com.app.yolla.modules.order.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.yolla.modules.order.dto.OrderItemResponse;
import com.app.yolla.modules.order.service.OrderItemService;
import com.app.yolla.shared.dto.ApiResponse;


@RestController
@RequestMapping(path = "/order-item")
@CrossOrigin(origins = "*") // CORS icazəsi
public class OrderItemController {

    private static final Logger logger = LoggerFactory.getLogger(OrderItemController.class);

    @Autowired
	private OrderItemService service;

	@GetMapping(path = "/{orderId}/begin/{begin}/length/{length}")
	@PreAuthorize("hasRole('CUSTOMER')")
	public ResponseEntity<ApiResponse<OrderItemResponse>> getOrderItems(@PathVariable("orderId") Long orderId,
			@PathVariable("begin") Integer begin,
			@PathVariable("length") Integer length) {
		try {
			OrderItemResponse resp = service.getOrderItems(orderId, begin, length);

			ApiResponse<OrderItemResponse> response = new ApiResponse<>(true, "Sifarisler", resp);

			return ResponseEntity.status(HttpStatus.OK).body(response);

		} catch (Exception e) {

			ApiResponse<OrderItemResponse> response = new ApiResponse<>(false,
					"Sifarisler getirilerken xəta baş verdi: " + e.getMessage(), null);

			return (ResponseEntity<ApiResponse<OrderItemResponse>>) ResponseEntity.badRequest().body(response);
		}
	}
}