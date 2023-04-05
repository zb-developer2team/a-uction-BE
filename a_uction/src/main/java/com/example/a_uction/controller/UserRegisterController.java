package com.example.a_uction.controller;

import com.example.a_uction.model.user.dto.RegisterUser;
import com.example.a_uction.service.UserRegisterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/register")
public class UserRegisterController {
	private final UserRegisterService userRegisterService;

	@PostMapping
	public ResponseEntity<RegisterUser> register(
		@RequestBody RegisterUser.Request request) {
		return ResponseEntity.ok(userRegisterService.register(request));
	}
}
