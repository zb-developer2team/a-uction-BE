package com.example.a_uction.security.jwt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenDto {
	private String accessToken;
	private String refreshToken;
	private long accessTokenExpireTime;
	private long refreshTokenExpireTime;


	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class AccessToken {
		private String accessToken;
		private String expiresIn;
		private String tokenType;
	}
}
