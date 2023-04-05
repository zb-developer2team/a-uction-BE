package com.example.a_uction.controller.user;

import com.example.a_uction.service.user.KakaoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/oauth")
@RequiredArgsConstructor
public class KakaoController {

	private final KakaoService kakaoService;

	@RequestMapping("/kakao/callback")
	public ResponseEntity<?> kakaoCallback(@RequestParam(name = "code") String code) {
		log.info("[CODE = {}]", code);

		String accessToken = kakaoService.getAccessToken(code);
		return ResponseEntity.ok(accessToken);
		// 현재 테스트 위해서 토큰 return , 추후에 리턴 값은 변경 할 것임
	}
}
