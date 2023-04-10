package com.example.a_uction.service.user;

import static com.example.a_uction.exception.constants.ErrorCode.INVALID_REFRESH_TOKEN;
import static com.example.a_uction.exception.constants.ErrorCode.NOT_MATCH_REFRESH_TOKEN;

import com.example.a_uction.exception.AuctionException;
import com.example.a_uction.security.jwt.JwtProvider;
import com.example.a_uction.security.jwt.dto.TokenDto;
import com.example.a_uction.security.jwt.dto.TokenDto.AccessToken;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

	@Value("${jwt.prefix}")
	private String tokenPrefix;
	@Value("${jwt.access.expiration}")
	private String accessExpiresIn;
	private final JwtProvider provider;
	private final RedisTemplate redisTemplate;
	public AccessToken reIssueAccessToken(HttpServletRequest request) {
		String refreshToken = provider.resolveRefreshTokenFromRequest(request);
		if (!provider.validateToken(refreshToken)) {
			throw new AuctionException(INVALID_REFRESH_TOKEN);
		}

		String accessToken = provider.resolveAccessTokenFromRequest(request);
		String userEmail = provider.getUserEmail(accessToken);
		if (refreshToken.equals(redisTemplate.opsForValue().get("RT:"+userEmail))) {
			return TokenDto.AccessToken.builder()
				.accessToken(provider.createAccessToken(userEmail))
				.tokenType(tokenPrefix)
				.expiresIn(accessExpiresIn)
				.build();

		} else {
			throw new AuctionException(NOT_MATCH_REFRESH_TOKEN);
		}
	}
}
