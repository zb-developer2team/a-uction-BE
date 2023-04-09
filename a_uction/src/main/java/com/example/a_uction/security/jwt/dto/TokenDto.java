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
	String accessToken;
	String refreshToken;

	long refreshTokenExpireTime;
}
