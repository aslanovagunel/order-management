package com.app.yolla.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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
		request.setTotalAmount(new BigDecimal("50.00"));
		request.setStatus(OrderStatus.PENDING);
		request.setNotes("Təcili çatdırılma");

		OrderItemRequest item1 = new OrderItemRequest();
		item1.setProductId(1L);
		item1.setQuantity(2);

		OrderItemRequest item2 = new OrderItemRequest();
		item2.setProductId(2L);
		item2.setQuantity(1);

		request.setItems(List.of(item1, item2));

		OrderDTO response = new OrderDTO();
		response.setId(100L);
		response.setUserId(10L);
		response.setStatus(OrderStatus.PENDING);
		response.setTotalAmount(new BigDecimal("50.00"));
		response.setCreatedAt(LocalDateTime.now());
		response.setNotes("Təcili çatdırılma");

		when(orderService.createdOrder(any(OrderCreateRequest.class))).thenReturn(response);

		mockMvc.perform(post("/orders").with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))).andExpect(status().isCreated());
	}


}