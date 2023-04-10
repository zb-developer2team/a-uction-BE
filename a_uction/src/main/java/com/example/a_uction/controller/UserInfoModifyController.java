package com.example.a_uction.controller;

import com.example.a_uction.model.user.dto.ModifyUser;
import com.example.a_uction.service.UserModifyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserInfoModifyController {
	private final UserModifyService userModifyService;

	@PostMapping("/detail/modify")
	public ResponseEntity<ModifyUser.Response> modify(Principal principal,
													  @RequestBody ModifyUser.Request request) {

		return ResponseEntity.ok(userModifyService.modifyUserDetail(principal.getName(), request));
	}
}
