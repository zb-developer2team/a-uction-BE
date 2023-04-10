package com.example.a_uction.controller;

import com.example.a_uction.security.jwt.dto.TokenDto;
import com.example.a_uction.service.user.AuthService;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	@PostMapping("/refresh")
	public ResponseEntity<TokenDto.AccessToken> reIssueAccessToken(HttpServletRequest request) {
		return ResponseEntity.ok(authService.reIssueAccessToken(request));
	}
}
