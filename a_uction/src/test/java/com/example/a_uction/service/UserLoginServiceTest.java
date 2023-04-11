package com.example.a_uction.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.example.a_uction.exception.AuctionException;
import com.example.a_uction.exception.constants.ErrorCode;
import com.example.a_uction.model.user.dto.LoginUser;
import com.example.a_uction.model.user.entity.UserEntity;
import com.example.a_uction.model.user.repository.UserRepository;
import com.example.a_uction.security.jwt.JwtProvider;
import com.example.a_uction.security.jwt.dto.TokenDto;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserLoginServiceTest {

	@Mock
	private JwtProvider provider;

	@Mock
	private UserRepository userRepository;

	@Mock
	private RedisTemplate<String, Object> redisTemplate;

	@Mock
	private BCryptPasswordEncoder passwordEncoder;

	@InjectMocks
	private UserLoginService userLoginService;

	@Test
	@DisplayName("로그인 성공")
	void login_SUCCESS() {
		// given
		UserEntity user = UserEntity.builder()
			.userEmail("zerobase@gmail.com")
			.password("1234")
			.build();
		given(userRepository.findByUserEmail(anyString())).willReturn(Optional.of(user));
		given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);
		given(provider.createToken(anyString())).willReturn(
			new TokenDto("123qwe", "qwe123", 1000L, 1000L));
		given(redisTemplate.opsForValue()).willReturn(mock(ValueOperations.class));
		// when
		TokenDto tokenDto = userLoginService.login(new LoginUser("zerobase@gmail.com", "1234"));

		// then
		assertNotNull(tokenDto);
		assertEquals("123qwe", tokenDto.getAccessToken());
		assertEquals("qwe123", tokenDto.getRefreshToken());
		assertEquals(1000L, tokenDto.getAccessTokenExpireTime());
		assertEquals(1000L, tokenDto.getRefreshTokenExpireTime());
	}

	@Test
	@DisplayName("로그인 실패 - 비밀번호 잘못 입력")
	void login_FAIL_WRONG_PASSWORD() {
		// given
		UserEntity user = UserEntity.builder()
			.userEmail("zerobase@gmail.com")
			.password("1234")
			.build();
		given(userRepository.findByUserEmail(anyString())).willReturn(Optional.of(user));
		given(passwordEncoder.matches(anyString(), anyString())).willReturn(false);
		// when

		AuctionException auctionException =
			assertThrows(AuctionException.class,
				() -> userLoginService.login(new LoginUser("zerobase@gmail.com", "2222")));
		// then
		assertEquals(ErrorCode.ENTERED_THE_WRONG_PASSWORD, auctionException.getErrorCode());
	}

	@Test
	@DisplayName("로그인 실패 - 이메일 잘못 입력")
	void login_FAIL_WRONG_EMAIL() {
		// given
		given(userRepository.findByUserEmail(anyString())).willReturn(Optional.empty());
		// when

		AuctionException auctionException =
			assertThrows(AuctionException.class,
				() -> userLoginService.login(new LoginUser("zerobase@gmail.com", "2222")));
		// then
		assertEquals(ErrorCode.USER_NOT_FOUND, auctionException.getErrorCode());
	}
}