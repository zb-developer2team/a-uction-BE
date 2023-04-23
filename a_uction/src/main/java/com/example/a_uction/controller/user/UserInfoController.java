package com.example.a_uction.controller.user;

import com.example.a_uction.model.user.dto.InfoUser;
import com.example.a_uction.model.user.dto.ModifyUser;
import com.example.a_uction.service.user.UserInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/detail")
public class UserInfoController {
	private final UserInfoService userInfoService;

	@PutMapping("/modify")
	public ResponseEntity<ModifyUser.Response> modify(Principal principal,
													  @RequestBody ModifyUser.Request request) {

		return ResponseEntity.ok(userInfoService.modifyUserDetail(principal.getName(), request));
	}

	@GetMapping
	public ResponseEntity<InfoUser> userInfo (Principal principal){
		return ResponseEntity.ok(userInfoService.userInfo(principal.getName()));
	}

	@PostMapping("/modify/profile-image")
	public ResponseEntity<InfoUser> modifyProfileImage(@RequestPart MultipartFile file, Principal principal) {
		return ResponseEntity.ok(userInfoService.modifyProfileImage(file, principal.getName()));
	}

	@PutMapping("/delete/profile-image")
	public ResponseEntity<InfoUser> deleteProfileImage(Principal principal) {
		return ResponseEntity.ok(userInfoService.deleteProfileImage(principal.getName()));
	}
}
