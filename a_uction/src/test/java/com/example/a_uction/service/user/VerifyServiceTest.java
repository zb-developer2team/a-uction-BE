package com.example.a_uction.service.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.example.a_uction.exception.AuctionException;
import com.example.a_uction.exception.constants.ErrorCode;
import com.example.a_uction.model.user.dto.Verify.Form;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@ExtendWith(MockitoExtension.class)
class VerifyServiceTest{
	@Mock
	private RedisTemplate<String, Object> redisTemplate;
	@InjectMocks
	private VerifyService verifyService;

	@Test
	@DisplayName("verifyService - 코드체크 성공")
	void CODE_CHECK_SUCCESS() {
		//given
		given(redisTemplate.opsForValue()).willReturn(mock(ValueOperations.class));
		given(redisTemplate.opsForValue().get(anyString()))
			.willReturn("1234");
		//when
		boolean codeCheck = verifyService.verifyCode(
			new Form("01012345678", "1234"));
	    //then

		assertTrue(codeCheck);
	}
	@Test
	@DisplayName("verifyService_TIMEOUT - 코드체크 실패- 시간초과")
	void CODE_CHECK_FAIL_TIME_OUT() {
		//given
		given(redisTemplate.opsForValue()).willReturn(mock(ValueOperations.class));
		given(redisTemplate.opsForValue().get(anyString()))
			.willThrow(new AuctionException(ErrorCode.VERIFICATION_CODE_TIME_OUT));
		//when
		AuctionException auctionException =
			assertThrows(AuctionException.class,
				() -> verifyService.verifyCode(new Form("01011111111", "qwe123")));
		//then

		assertEquals(ErrorCode.VERIFICATION_CODE_TIME_OUT, auctionException.getErrorCode());
	}

	@Test
	@DisplayName("verifyService_UNMATCH_CODE - 코드체크 실패 - 코드 불일치")
	void CODE_CHECK_FAIL_UN_MATCH_CODE() {
		//given
		given(redisTemplate.opsForValue()).willReturn(mock(ValueOperations.class));
		given(redisTemplate.opsForValue().get(anyString()))
			.willReturn("1234");
		//when
		AuctionException auctionException =
			assertThrows(AuctionException.class,
				() -> verifyService.verifyCode(new Form("01011111111", "qwe123")));
		//then

		assertEquals(ErrorCode.WRONG_CODE_INPUT, auctionException.getErrorCode());
	}
}