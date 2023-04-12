package com.example.a_uction.service.user;

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

@Service
@RequiredArgsConstructor
@Slf4j
public class UserLoginService {
	private final UserRepository userRepository;
	private final JwtProvider provider;
	private final BCryptPasswordEncoder encoder;
	private final RedisTemplate<String,Object> redisTemplate;
	public TokenDto login(LoginUser loginUser) {

		UserEntity user = userRepository.findByUserEmail(loginUser.getUserEmail())
			.orElseThrow(
				() -> new AuctionException(USER_NOT_FOUND)
			);

		validationLogin(loginUser.getPassword(), user.getPassword());

		String email = user.getUserEmail();
		TokenDto tokenDto = provider.createToken(email);

		redisSetup(email, tokenDto);

		return tokenDto;
	}

	private void validationLogin(String formPassword, String encodingPassword) {
		if (!encoder.matches(formPassword, encodingPassword)) {
			throw new AuctionException(ENTERED_THE_WRONG_PASSWORD);
		}
	}
	private void redisSetup(String email, TokenDto tokenDto) {
		redisTemplate.opsForValue()
			.set("RT:" + email, tokenDto.getRefreshToken(),
				tokenDto.getRefreshTokenExpireTime(), TimeUnit.MILLISECONDS);
	}
}
