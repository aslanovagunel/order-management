package com.app.yolla.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
import com.app.yolla.modules.product.controller.ProductController;
import com.app.yolla.modules.product.dto.ProductAddRequest;
import com.app.yolla.modules.product.dto.ProductDTO;
import com.app.yolla.modules.product.service.ProductService;
import com.app.yolla.shared.security.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ProductController.class)
@Import(TestAuditingConfig.class)
class ProductControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private ProductService productService;

	@MockBean
	private JwtUtil jwtUtil;

	@MockBean
	private org.springframework.data.jpa.mapping.JpaMetamodelMappingContext jpaMetamodelMappingContext;

	@WithMockUser
	@Test
	void shouldCreateProductSuccessfully() throws Exception {
		ProductAddRequest request = new ProductAddRequest();
		request.setName("canta");
		request.setDescription("tez gelsin");
		request.setPrice(new BigDecimal("19.3"));
		request.setStockQuantity(3);

		ProductDTO response = new ProductDTO();
		response.setId(UUID.fromString("d6f1f8f4-72d4-4c33-92c2-0fbe11e53c9a"));
		response.setName("canta");
		response.setDescription("tez gelsin");
		response.setPrice(new BigDecimal("19.3"));
		response.setStockQuantity(3);
		response.setActive(true);
		response.setCreatedAt(LocalDateTime.now());
		response.setUpdatedAt(LocalDateTime.now());

		when(productService.createdProduct(any(ProductAddRequest.class))).thenReturn(response);

		mockMvc.perform(post("/products").with(csrf())
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isCreated());
	}
}