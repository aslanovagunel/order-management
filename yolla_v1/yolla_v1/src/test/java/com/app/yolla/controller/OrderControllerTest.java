package com.app.yolla.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.app.yolla.config.TestAuditingConfig;
import com.app.yolla.modules.order.controller.OrderController;
import com.app.yolla.modules.order.dto.OrderCreateRequest;
import com.app.yolla.modules.order.dto.OrderDTO;
import com.app.yolla.modules.order.dto.OrderItemDTO;
import com.app.yolla.modules.order.dto.OrderItemRequest;
import com.app.yolla.modules.order.entity.OrderStatus;
import com.app.yolla.modules.order.service.OrderService;
import com.app.yolla.shared.security.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(OrderController.class)
@Import(TestAuditingConfig.class)
class OrderControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private OrderService orderService;

	@MockBean
	private JwtUtil jwtUtil;

	@MockBean
	private org.springframework.data.jpa.mapping.JpaMetamodelMappingContext jpaMetamodelMappingContext;

	@WithMockUser
	@Test
	void shouldCreateOrderSuccessfully() throws Exception {
		OrderCreateRequest request = new OrderCreateRequest();

		request.setStatus(OrderStatus.PENDING);
		request.setNotes("Təcili çatdırılma");
		request.setDeliveryAddress("strinf");

		OrderItemRequest item1 = new OrderItemRequest();
		item1.setProductId(UUID.fromString("a3b01a3e-8c2e-4dc5-b8de-0f5b9b7b74e3"));
		item1.setQuantity(2);

		OrderItemRequest item2 = new OrderItemRequest();
		item2.setProductId(UUID.fromString("9fef93e3-3333-44cd-94a3-1f45dbf3ac11"));
		item2.setQuantity(1);

		request.setItems(List.of(item1, item2));

		OrderItemDTO orderItem1 = new OrderItemDTO();
		orderItem1.setProductId(UUID.fromString("a3b01a3e-8c2e-4dc5-b8de-0f5b9b7b74e3"));
		orderItem1.setProductName("Product A");
		orderItem1.setUnitPrice(new BigDecimal("20.00"));
		orderItem1.setQuantity(2);

		OrderItemDTO orderItem2 = new OrderItemDTO();
		orderItem2.setProductId(UUID.fromString("9fef93e3-3333-44cd-94a3-1f45dbf3ac11"));
		orderItem2.setProductName("Product B");
		orderItem2.setUnitPrice(new BigDecimal("10.00"));
		orderItem2.setQuantity(1);


		OrderDTO response = new OrderDTO();
		response.setId(UUID.fromString("d6f1f8f4-72d4-4c33-92c2-0fbe11e53c9a"));
		response.setUserId(UUID.fromString("f0a1b672-4a8b-4e12-b48e-4e23a948c1f9"));
		response.setUserFullName("323");
		response.setUserPhone("+994557894561");
		response.setDeliveryAddress("DWA");
		response.setDeliveryTime(LocalDateTime.now());
		response.setStatus(OrderStatus.PENDING);
		response.setTotalAmount(new BigDecimal("50.00"));
		response.setCreatedAt(LocalDateTime.now());
		response.setNotes("Təcili çatdırılma");
		response.setItems(List.of(orderItem1, orderItem2));

		when(orderService.createdOrder(any(OrderCreateRequest.class))).thenReturn(response);

		mockMvc.perform(post("/orders").with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))).andExpect(status().isCreated());
	}


}