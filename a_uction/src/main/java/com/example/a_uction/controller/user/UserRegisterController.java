package com.example.a_uction.controller.user;

import com.example.a_uction.model.user.dto.RegisterUser;
import com.example.a_uction.model.user.dto.Verify;
import com.example.a_uction.service.user.UserRegisterService;
import com.example.a_uction.service.user.VerifyService;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/register")
public class UserRegisterController {
	private final UserRegisterService userRegisterService;
	private final VerifyService verifyService;

	@PostMapping
	public ResponseEntity<RegisterUser> register(
		@RequestBody RegisterUser.Request request) {

		return ResponseEntity.ok(userRegisterService.register(request));
	}

	@GetMapping("/emailCheck")
	public ResponseEntity<Boolean> emailCheck(@RequestBody String email) {

		return ResponseEntity.ok(userRegisterService.emailCheck(email));
	}

	@PostMapping("/verify/sms")
	public ResponseEntity<Verify.Response> sendVerifyMessage(
		@RequestBody String phoneNumber)
		throws NoSuchAlgorithmException, InvalidKeyException, URISyntaxException, JsonProcessingException {

		return ResponseEntity.ok(verifyService.sendVerificationCode(phoneNumber));
	}

	@PostMapping("/verify/sms/codeCheck")
	public ResponseEntity<Boolean> codeCheck(@RequestBody Verify.Form form) {
		return ResponseEntity.ok(verifyService.verifyCode(form));
	}
}
