package com.example.a_uction.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.a_uction.exception.AuctionException;
import com.example.a_uction.exception.constants.ErrorCode;
import com.example.a_uction.model.user.dto.RegisterUser;
import com.example.a_uction.service.UserRegisterService;
import com.example.a_uction.service.user.VerifyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class UserRegisterControllerTest {

	@MockBean
	private UserRegisterService userRegisterService;
	@MockBean
	private VerifyService verifyService;

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
	@DisplayName("이메일중복 확인 - 사용 가능")
	void emailCheck_SUCCESS() throws Exception {
		//given
		given(userRegisterService.emailCheck(anyString()))
			.willReturn(true);
		//when
		//then
		mockMvc.perform(get("/register/emailCheck")
				.with(csrf())
				.content("zerobase@gmail.com"))
			.andDo(print())
			.andExpect(status().isOk())
			.andReturn().getResponse().toString().equals(true);
	}
	@Test
	@WithMockUser
	@DisplayName("이메일중복 확인 - 사용 불가")
	void emailCheck_FAIL() throws Exception {
	    //given
		given(userRegisterService.emailCheck(anyString()))
			.willThrow(new AuctionException(ErrorCode.THIS_EMAIL_ALREADY_EXIST));
	    //when
		//then
		mockMvc.perform(get("/register/emailCheck")
				.with(csrf())
				.content("zerobase@gmail.com"))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.errorCode").value("THIS_EMAIL_ALREADY_EXIST"))
			.andExpect(jsonPath("$.message").value("해당 이메일은 이미 존재합니다."));
	}

	@Test
	@WithMockUser
	@DisplayName("인증완료 - 코드체크 성공")
	void codeCheck_SUCCESS() throws Exception {
	    //given
		given(verifyService.verifyCode(anyString()))
			.willReturn(true);
	    //when
	    //then
		mockMvc.perform(get("/register/verify/sms/codeCheck")
				.with(csrf())
				.content("qwe123"))
			.andDo(print())
			.andExpect(status().isOk())
			.andReturn().getResponse().toString().equals(true);
	}
	@Test
	@WithMockUser
	@DisplayName("인증완료 - 코드체크 실패")
	void codeCheck_FAIL() throws Exception {
		//given
		given(verifyService.verifyCode(anyString()))
			.willThrow(new AuctionException(ErrorCode.WRONG_CODE_INPUT));
		//when
		//then
		mockMvc.perform(get("/register/verify/sms/codeCheck")
				.with(csrf())
				.content("qwe123"))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.errorCode").value("WRONG_CODE_INPUT"))
			.andExpect(jsonPath("$.message")
				.value("코드를 잘못 입력하셨습니다. 처음부터 다시 시도해주세요."));
	}
}