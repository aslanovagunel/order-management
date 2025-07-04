package com.app.yolla.modules.order.controller;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.yolla.modules.order.dto.OrderCreateRequest;
import com.app.yolla.modules.order.dto.OrderDTO;
import com.app.yolla.modules.order.dto.OrderResponse;
import com.app.yolla.modules.order.dto.OrderUpdateRequest;
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
	private OrderService service;


    @PostMapping
	@PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
	public ResponseEntity<ApiResponse<OrderDTO>> createdOrder(@Valid @RequestBody OrderCreateRequest request) {

		// logger.info("Yeni istifadəçi yaratma sorğusu: {}", request.getPhoneNumber());

        try {
			OrderDTO createdOrder = service.createdOrder(request);

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
	@PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
	public ResponseEntity<ApiResponse<OrderResponse>> getAll(@PathVariable("begin") Integer begin,
			@PathVariable("length") Integer length) {
		try {
			OrderResponse resp = service.getAll(begin, length);

			ApiResponse<OrderResponse> response = new ApiResponse<>(true, "Sifarisler", resp);

			return ResponseEntity.status(HttpStatus.OK).body(response);

		} catch (Exception e) {

			ApiResponse<OrderResponse> response = new ApiResponse<>(false,
					"Sifarisler getirilerken xəta baş verdi: " + e.getMessage(), null);

			return (ResponseEntity<ApiResponse<OrderResponse>>) ResponseEntity.badRequest().body(response);
		}
	}

	@DeleteMapping(path = "/{id}")
	@PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
	public ResponseEntity<?> deleteById(@PathVariable("id") UUID id) {

		try {
			service.deleteById(id);

			ApiResponse<String> response = new ApiResponse<>(true, "Sifariş uğurla silindi", null);

			return ResponseEntity.ok(response);

		} catch (Exception e) {
			ApiResponse<String> response = new ApiResponse<>(false,
					"Məhsul dəyişdirilərkən xəta baş verdi: " + e.getMessage(), null);

			return ResponseEntity.badRequest().body(response);
		}
	}

	@PutMapping(path = "/{id}")
	@PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
	public ResponseEntity<ApiResponse<OrderDTO>> update(@PathVariable("id") UUID id,
			@Valid @RequestBody OrderUpdateRequest req, BindingResult result) {

		try {
			OrderDTO updateOrder = service.updateOrder(id, req);

			ApiResponse<OrderDTO> response = new ApiResponse<>(true, "Sifariş uğurla dəyişdirildi", updateOrder);

			return ResponseEntity.ok(response);

		} catch (Exception e) {
			ApiResponse<OrderDTO> response = new ApiResponse<>(false,
					"Sifariş dəyişdirilərkən xəta baş verdi: " + e.getMessage(), null);

			return ResponseEntity.badRequest().body(response);
		}
	}

	@PutMapping("/{id}/confirm")
	@PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
	public ResponseEntity<ApiResponse<OrderDTO>> confirmOrder(@PathVariable("id") UUID id,
			Authentication authentication) {

		String currentUserPhone = authentication.getName();
		try {
			OrderDTO updatedOrder = service.confirmOrder(id, currentUserPhone);
			ApiResponse<OrderDTO> response = new ApiResponse<>(true, "Sifariş təsdiqləndi", updatedOrder);
			return ResponseEntity.ok(response);

		} catch (Exception e) {
			ApiResponse<OrderDTO> response = new ApiResponse<>(false,
					"Sifariş təsdiqlənərkən xəta baş verdi: " + e.getMessage(), null);
			return ResponseEntity.badRequest().body(response);
		}
	}

	@PutMapping("/{id}/ship")
	@PreAuthorize("hasRole('ADMIN') or hasRole('PREPARER')")
	public ResponseEntity<ApiResponse<OrderDTO>> shipOrder(@PathVariable("id") UUID id, Authentication authentication) {

		String currentUserPhone = authentication.getName();
		try {
			OrderDTO updatedOrder = service.shipOrder(id, currentUserPhone);
			ApiResponse<OrderDTO> response = new ApiResponse<>(true, "Sifariş göndərildi (shipped)", updatedOrder);
			return ResponseEntity.ok(response);

		} catch (Exception e) {
			ApiResponse<OrderDTO> response = new ApiResponse<>(false,
					"Sifariş göndərilərkən xəta baş verdi: " + e.getMessage(), null);
			return ResponseEntity.badRequest().body(response);
		}
	}

	@PutMapping("/{id}/deliver")
	@PreAuthorize("hasRole('ADMIN') or hasRole('PREPARER')")
	public ResponseEntity<ApiResponse<OrderDTO>> deliverOrder(@PathVariable("id") UUID id,
			Authentication authentication) {

		String currentUserPhone = authentication.getName();
		try {
			OrderDTO updatedOrder = service.deliverOrder(id, currentUserPhone);
			ApiResponse<OrderDTO> response = new ApiResponse<>(true, "Sifariş çatdırıldı (delivered)", updatedOrder);
			return ResponseEntity.ok(response);

		} catch (Exception e) {
			ApiResponse<OrderDTO> response = new ApiResponse<>(false,
					"Sifariş çatdırılarkən xəta baş verdi: " + e.getMessage(), null);
			return ResponseEntity.badRequest().body(response);
		}
	}

	@PutMapping("/{id}/cancel")
	@PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
	public ResponseEntity<ApiResponse<OrderDTO>> cancelOrder(@PathVariable("id") UUID id,
			Authentication authentication) {

		String currentUserPhone = authentication.getName();
		try {
			OrderDTO updatedOrder = service.cancelOrder(id, currentUserPhone);
			ApiResponse<OrderDTO> response = new ApiResponse<>(true, "Sifariş ləğv olundu (cancelled)", updatedOrder);
			return ResponseEntity.ok(response);

		} catch (Exception e) {
			ApiResponse<OrderDTO> response = new ApiResponse<>(false,
					"Sifariş ləğv olunarkən xəta baş verdi: " + e.getMessage(), null);
			return ResponseEntity.badRequest().body(response);
		}
	}
}