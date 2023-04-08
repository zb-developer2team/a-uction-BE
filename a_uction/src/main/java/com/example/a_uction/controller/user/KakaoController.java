package com.example.a_uction.controller.user;

import com.example.a_uction.service.user.KakaoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/oauth")
@RequiredArgsConstructor
public class KakaoController {

	private final KakaoService kakaoService;

	@GetMapping("/kakao/callback")
	public ResponseEntity<?> kakaoLogin(@RequestParam(name = "code") String code) {

		log.info("[CODE = {}]", code);
		return ResponseEntity.ok(kakaoService.kakaoLogIn(code));

	}
}
