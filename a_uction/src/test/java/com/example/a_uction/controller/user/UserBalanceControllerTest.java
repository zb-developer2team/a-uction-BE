package com.example.a_uction.controller.user;

import static com.example.a_uction.exception.constants.ErrorCode.NOT_ENOUGH_BALANCE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.a_uction.exception.AuctionException;
import com.example.a_uction.model.user.dto.Balance;
import com.example.a_uction.security.jwt.JwtProvider;
import com.example.a_uction.service.user.UserBalanceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;


@WebMvcTest(UserBalanceController.class)
class UserBalanceControllerTest {

	@MockBean
	private UserBalanceService userBalanceService;
	@MockBean
	private JwtProvider jwtProvider;
	@MockBean
	private JpaMetamodelMappingContext jpaMetamodelMappingContext;
	@MockBean
	private RedisTemplate<String, Object> redisTemplate;
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@WithMockUser
	@DisplayName("예치금 충전 - 성공")
	void charge_SUCCESS() throws Exception {

		//given
		Balance.Request request = Balance.Request.builder()
			.from("카카오")
			.money(10000)
			.build();

		Balance.Response response = Balance.Response.builder()
			.from("카카오")
			.changeMoney(10000)
			.currentMoney(20000)
			.userEmail("zerobase@gmail.com")
			.build();

		given(userBalanceService.charge(any(), any()))
			.willReturn(response);

		// when
		// then
		mockMvc.perform(put("/users/balance/charge").with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(jsonPath("$.from").value("카카오"))
			.andExpect(jsonPath("$.changeMoney").value("10000"))
			.andExpect(jsonPath("$.currentMoney").value("20000"))
			.andExpect(jsonPath("$.userEmail").value("zerobase@gmail.com"))
			.andExpect(status().isOk());
	}

	@Test
	@WithMockUser
	@DisplayName("예치금 충전 - 실패")
	void chargeFail() throws Exception {
		// given
		Balance.Request request = Balance.Request.builder()
			.from("카카오")
			.money(10000)
			.build();

		given(userBalanceService.charge(any(), any()))
			.willThrow(new AuctionException(NOT_ENOUGH_BALANCE));

		// when
		// then
		mockMvc.perform(put("/users/balance/charge").with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(jsonPath("$.errorCode").value("NOT_ENOUGH_BALANCE"));
	}
}