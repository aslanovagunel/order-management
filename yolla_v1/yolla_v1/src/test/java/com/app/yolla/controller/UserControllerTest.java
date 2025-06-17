package com.app.yolla.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.app.yolla.config.TestAuditingConfig;
import com.app.yolla.modules.user.controller.UserController;
import com.app.yolla.modules.user.dto.UserCreateRequest;
import com.app.yolla.modules.user.dto.UserDTO;
import com.app.yolla.modules.user.entity.UserRole;
import com.app.yolla.modules.user.service.UserService;
import com.app.yolla.shared.security.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(UserController.class)
@Import(TestAuditingConfig.class)
class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private UserService userService;

	@MockBean
	private JwtUtil jwtUtil;

	@MockBean
	private org.springframework.data.jpa.mapping.JpaMetamodelMappingContext jpaMetamodelMappingContext;

	@Test
	@WithMockUser(roles = "ADMIN")
	void testCreateUserSuccess() throws Exception {
		UserCreateRequest request = new UserCreateRequest();
		request.setPhoneNumber("+994501234567");
		request.setFullName("Test User");
		request.setEmail("test@example.com");
		request.setRole(UserRole.CUSTOMER);

		UserDTO createdUser = new UserDTO();
		createdUser.setId(1L);
		createdUser.setPhoneNumber(request.getPhoneNumber());
		createdUser.setFullName(request.getFullName());
		createdUser.setEmail(request.getEmail());
		createdUser.setRole(request.getRole());
		createdUser.setIsActive(true);
		createdUser.setCreatedAt(LocalDateTime.now());
		createdUser.setUpdatedAt(LocalDateTime.now());

		when(userService.createUser(any(UserCreateRequest.class))).thenReturn(createdUser);

		mockMvc.perform(post("/users").with(csrf()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))).andExpect(status().isCreated())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value("İstifadəçi uğurla yaradıldı"))
				.andExpect(jsonPath("$.data.id").value(1))
				.andExpect(jsonPath("$.data.phoneNumber").value("+994501234567"))
				.andExpect(jsonPath("$.data.fullName").value("Test User"))
				.andExpect(jsonPath("$.data.email").value("test@example.com"))
				.andExpect(jsonPath("$.data.role").value("CUSTOMER"))
				.andExpect(jsonPath("$.data.isActive").value(true));
	}
}
