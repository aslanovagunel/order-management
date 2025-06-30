package com.app.yolla.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
import com.app.yolla.modules.order.controller.OrderItemController;
import com.app.yolla.modules.order.dto.OrderItemResponse;
import com.app.yolla.modules.order.dto.OrderItemResponseDTO;
import com.app.yolla.modules.order.service.OrderItemService;
import com.app.yolla.shared.security.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(OrderItemController.class)
@Import(TestAuditingConfig.class)
class OrderItemControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private OrderItemService orderItemService;

	@MockBean
	private JwtUtil jwtUtil;

	@MockBean
	private org.springframework.data.jpa.mapping.JpaMetamodelMappingContext jpaMetamodelMappingContext;

	@Test
	@WithMockUser
	void testGetOrderItems() throws Exception {
		UUID orderId = UUID.fromString("3c4b1a2f-8b00-4d55-a6f9-abcdefabcdef");

		OrderItemResponseDTO item1 = new OrderItemResponseDTO();
		item1.setProductId(UUID.fromString("9e90cbbb-75c3-4d23-87ae-123456789100"));
		item1.setProductName("Product A");
		item1.setQuantity(2);
		item1.setPrice(new BigDecimal("10.50"));

		OrderItemResponseDTO item2 = new OrderItemResponseDTO();
		item2.setProductId(UUID.fromString("9e90cbbb-75c3-4d23-87ae-123456789101"));
		item2.setProductName("Product B");
		item2.setQuantity(1);
		item2.setPrice(new BigDecimal("5.00"));

		OrderItemResponse response = new OrderItemResponse();
		response.setOrderId(UUID.fromString("3c4b1a2f-8b00-4d55-a6f9-abcdefabcdef"));
		response.setStatus("PENDING");
		response.setCreatedAt(LocalDateTime.now());
		response.setItems(List.of(item1, item2));

		when(orderItemService.getOrderItems(eq(orderId), eq(0), eq(10))).thenReturn(response);

		mockMvc.perform(get("/order-item/" + orderId + "/begin/0/length/10").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value("Sifarisler"))
				.andExpect(jsonPath("$.data.orderId").value(orderId))
				.andExpect(jsonPath("$.data.totalAmount").value(26.00))
				.andExpect(jsonPath("$.data.status").value("PENDING")).andExpect(jsonPath("$.data.items").isArray())
				.andExpect(jsonPath("$.data.items.length()").value(2))
				.andExpect(jsonPath("$.data.items[0].productId").value(100))
				.andExpect(jsonPath("$.data.items[0].productName").value("Product A"))
				.andExpect(jsonPath("$.data.items[1].productId").value(101))
				.andExpect(jsonPath("$.data.items[1].quantity").value(1));
	}
}