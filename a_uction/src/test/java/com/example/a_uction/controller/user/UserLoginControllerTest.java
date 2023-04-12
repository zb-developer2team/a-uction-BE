package com.example.a_uction.controller.user;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.a_uction.exception.AuctionException;
import com.example.a_uction.exception.constants.ErrorCode;
import com.example.a_uction.model.user.dto.LoginUser;
import com.example.a_uction.security.jwt.JwtProvider;
import com.example.a_uction.security.jwt.dto.TokenDto;
import com.example.a_uction.service.user.UserLoginService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class UserLoginControllerTest {

	@MockBean
	private UserLoginService userLoginService;

	@MockBean
	private JwtProvider jwtProvider;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private MockMvc mockMvc;

	@Test
	@DisplayName("로그인 성공")
	void login_SUCCESS() throws Exception {
		given(userLoginService.login(any()))
			.willReturn(TokenDto.builder()
				.accessToken("tokentoken")
				.refreshToken("refreshToken")
				.build());

		mockMvc.perform(post("/login")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(
					new LoginUser("zerobase@gmail.com", "1234")
				)))
			.andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("로그인실패 - 이메일 잘못 입력")
	void LOGIN_FAIL_EMAIL() throws Exception {
		//given
		given(userLoginService.login(any()))
			.willThrow(new AuctionException(ErrorCode.USER_NOT_FOUND));
		//when
		//then
		mockMvc.perform(post("/login")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(
					new LoginUser("zerobase@gmail.com", "1234")
				)))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.errorCode").value("USER_NOT_FOUND"))
			.andExpect(jsonPath("$.message").value("유저를 찾을 수 없습니다."));
	}

	@Test
	@DisplayName("로그인실패 - 비밀번호 잘못 입력")
	void LOGIN_FAIL_PASSWORD() throws Exception {
		//given
		given(userLoginService.login(any()))
			.willThrow(new AuctionException(ErrorCode.ENTERED_THE_WRONG_PASSWORD));
		//when
		//then
		mockMvc.perform(post("/login")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(
					new LoginUser("zerobase@gmail.com", "1234")
				)))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.errorCode").value("ENTERED_THE_WRONG_PASSWORD"))
			.andExpect(jsonPath("$.message").value("비밀번호를 확인 해 주세요."));
	}
}