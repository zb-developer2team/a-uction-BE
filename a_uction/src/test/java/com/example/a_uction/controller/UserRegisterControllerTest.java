package com.example.a_uction.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.a_uction.exception.AuctionException;
import com.example.a_uction.exception.constants.ErrorCode;
import com.example.a_uction.model.user.dto.RegisterUser;
import com.example.a_uction.security.jwt.JwtProvider;
import com.example.a_uction.service.UserRegisterService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserRegisterController.class)
class UserRegisterControllerTest {

	@MockBean
	private UserRegisterService userRegisterService;
	@MockBean
	private JwtProvider jwtProvider;
	@MockBean
	private JpaMetamodelMappingContext context;

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
	@Test
	@WithMockUser
	@DisplayName("회원가입 실패 - 컨트롤러 테스트")
	void register_FAIL() throws Exception {
		//given
		given(userRegisterService.register(any()))
			.willThrow(new AuctionException(ErrorCode.THIS_EMAIL_ALREADY_EXIST));
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
			.andExpect(jsonPath("$.errorCode").value("THIS_EMAIL_ALREADY_EXIST"))
			.andExpect(jsonPath("$.message").value("해당 이메일은 이미 존재합니다."));
	}
}