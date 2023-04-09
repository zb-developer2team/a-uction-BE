package com.example.a_uction.service;

import static com.example.a_uction.exception.constants.ErrorCode.ENTERED_THE_WRONG_PASSWORD;
import static com.example.a_uction.exception.constants.ErrorCode.USER_NOT_FOUND;

import com.example.a_uction.exception.AuctionException;
import com.example.a_uction.model.user.dto.LoginUser;
import com.example.a_uction.model.user.entity.UserEntity;
import com.example.a_uction.model.user.repository.UserRepository;
import com.example.a_uction.security.jwt.JwtProvider;
import com.example.a_uction.security.jwt.dto.TokenDto;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserLoginService {
	private final UserRepository userRepository;
	private final JwtProvider provider;
	private final BCryptPasswordEncoder encoder;
	private final RedisTemplate redisTemplate;
	public TokenDto login(LoginUser loginUser) {

		UserEntity user = userRepository.findByUserEmail(loginUser.getUserEmail())
			.orElseThrow(
				() -> new AuctionException(USER_NOT_FOUND)
			);

		if (!validationLogin(loginUser.getPassword(), user.getPassword())) {
			throw new AuctionException(ENTERED_THE_WRONG_PASSWORD);
		}

		/**
		 * 기존에 return token -> return tokenDto (accessToken, refreshToken 둘다 리턴 )
		 * 해당 주석은 확인하시고 지우셔도 됩니다.
		 */
		// AccessToken, RefreshToken 생성
		String email = user.getUserEmail();
		TokenDto tokenDto = provider.createToken(email);

		// refresh토큰 redis 저장
		redisTemplate.opsForValue()
			.set("RT:" + email, tokenDto.getRefreshToken(),
				tokenDto.getRefreshTokenExpireTime(), TimeUnit.MILLISECONDS);

		return tokenDto;
	}

	private boolean validationLogin(String formPassword, String encodingPassword) {
		return encoder.matches(formPassword, encodingPassword);
	}
}
