package com.example.a_uction.controller;

import com.example.a_uction.model.user.dto.LoginUser;
import com.example.a_uction.security.jwt.dto.TokenDto;
import com.example.a_uction.service.UserLoginService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserLoginController {

	private final UserLoginService userLoginService;

	@PostMapping("/login")
	public ResponseEntity<TokenDto> login(@Valid @RequestBody LoginUser user) {

		return ResponseEntity.ok(userLoginService.login(user));
	}

	/**
	 * test용 api
	 */
	@GetMapping("/detail")
	public ResponseEntity<String> test(Authentication authentication) {
		authentication.setAuthenticated(false);
		return ResponseEntity.ok(authentication.getName());
	}
}

