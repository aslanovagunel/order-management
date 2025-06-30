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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.yolla.modules.order.dto.OrderCreateRequest;
import com.app.yolla.modules.order.dto.OrderDTO;
import com.app.yolla.modules.order.dto.OrderResponse;
import com.app.yolla.modules.order.service.OrderService;
import com.app.yolla.shared.dto.ApiResponse;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Order", description = "Sifarişlərə dair əməliyyatlar")
@RestController
@RequestMapping(path = "/orders")
@CrossOrigin(origins = "*") // CORS icazəsi
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
	private OrderService orderService;


    @PostMapping
	@PreAuthorize("hasRole('CUSTOMER')")
	public ResponseEntity<ApiResponse<OrderDTO>> createdOrder(@Valid @RequestBody OrderCreateRequest request) {

		// logger.info("Yeni istifadəçi yaratma sorğusu: {}", request.getPhoneNumber());

        try {
			OrderDTO createdOrder = orderService.createdOrder(request);

			ApiResponse<OrderDTO> response = new ApiResponse<>(
                    true,
					"Sifaris uğurla yaradıldı", createdOrder
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
			// logger.error("İstifadəçi yaratma xətası: ", e);

			ApiResponse<OrderDTO> response = new ApiResponse<>(
                    false,
					"Sifaris yaradılarkən xəta baş verdi: " + e.getMessage(),
                    null
            );

            return ResponseEntity.badRequest().body(response);
        }
    }

	@GetMapping(path = "/begin/{begin}/length/{length}")
	@PreAuthorize("hasRole('CUSTOMER')")
	public ResponseEntity<ApiResponse<OrderResponse>> getAll(@PathVariable("begin") Integer begin,
			@PathVariable("length") Integer length) {
		try {
			OrderResponse resp = orderService.getAll(begin, length);

			ApiResponse<OrderResponse> response = new ApiResponse<>(true, "Sifarisler", resp);

			return ResponseEntity.status(HttpStatus.OK).body(response);

		} catch (Exception e) {

			ApiResponse<OrderResponse> response = new ApiResponse<>(false,
					"Sifarisler getirilerken xəta baş verdi: " + e.getMessage(), null);

			return (ResponseEntity<ApiResponse<OrderResponse>>) ResponseEntity.badRequest().body(response);
		}
	}
}