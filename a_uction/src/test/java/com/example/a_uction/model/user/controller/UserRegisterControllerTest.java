package com.example.a_uction.model.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.a_uction.model.user.dto.RegisterUser;
import com.example.a_uction.service.UserRegisterService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest
class UserRegisterControllerTest {

	@MockBean
	private UserRegisterService userRegisterService;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private MockMvc mockMvc;
	@Test
	@WithMockUser
	void register_SUCCESS() throws Exception {
	    //given
		given(userRegisterService.register(any()))
			.willReturn(RegisterUser.builder()
				.userEmail("zerobase@gmail.com")
				.username("zerobase")
				.build());
	    //when
	    //then
		mockMvc.perform(post("/register")
				.with(csrf())
				.content(objectMapper.writeValueAsString(
					new RegisterUser.Request(
						"zerobase@gmail.com",
						"1234",
						"zerobase",
						"01012345678")
				))
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.userEmail").value("zerobase@gmail.com"))
			.andExpect(jsonPath("$.username").value("zerobase"));
	}
}