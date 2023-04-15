package com.example.a_uction.service.user;

import static com.example.a_uction.exception.constants.ErrorCode.INVALID_PARSE_ERROR;

import com.example.a_uction.exception.AuctionException;
import com.example.a_uction.model.user.dto.LogoutUser;
import com.example.a_uction.model.user.entity.UserEntity;
import com.example.a_uction.model.user.repository.UserRepository;
import com.example.a_uction.security.jwt.JwtProvider;
import com.example.a_uction.security.jwt.dto.TokenDto;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class KakaoService {

	@Value("${oauth2.kakao.restApiKey}")
	private String REST_API_KEY;
	@Value("${oauth2.kakao.redirectUrl}")
	private String REDIRECT_URL;

	private final RestTemplate restTemplate;
	private final UserRepository userRepository;
	private final JwtProvider provider;
	private final RedisTemplate<String, Object> redisTemplate;

	public TokenDto kakaoLogIn(String code) {
		String kakaoToken = this.getAccessToken(code);
		return this.getTokenByKakao(this.getUserInfoByKakao(kakaoToken));
	}

	public String getAccessToken(String code) {

		String reqURL = "https://kauth.kakao.com/oauth/token";

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "authorization_code");
		params.add("client_id", REST_API_KEY);
		params.add("redirect_url", REDIRECT_URL);
		params.add("code", code);

		HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params,
			headers);

		ResponseEntity<String> response = restTemplate.exchange(
			reqURL,
			HttpMethod.POST,
			kakaoTokenRequest,
			String.class
		);

		String tokenJson = response.getBody();

		JSONParser jsonParser = new JSONParser();
		JSONObject jsonObject;

		try {
			jsonObject = (JSONObject) jsonParser.parse(tokenJson);
		} catch (ParseException e) {
			throw new AuctionException(INVALID_PARSE_ERROR);
		}

		return jsonObject.get("access_token").toString();
	}

	public HashMap<String, String> getUserInfoByKakao(String accessToken) {
		String reqURL = "https://kapi.kakao.com/v2/user/me";

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + accessToken);

		HttpEntity<MultiValueMap<String, String>> kakaoUserRequest = new HttpEntity<>(headers);

		ResponseEntity<String> response = restTemplate.exchange(
			reqURL,
			HttpMethod.POST,
			kakaoUserRequest,
			String.class
		);

		String userInfoJson = response.getBody();

		JSONParser jsonParser = new JSONParser();
		JSONObject jsonObject;

		try {
			jsonObject = (JSONObject) jsonParser.parse(userInfoJson);
		} catch (ParseException e) {
			throw new AuctionException(INVALID_PARSE_ERROR);
		}

		HashMap<String, String> userInfo = new HashMap<>();
		JSONObject properties = (JSONObject) jsonObject.get("properties");
		JSONObject kakao_account = (JSONObject) jsonObject.get("kakao_account");

		userInfo.put("nickname", properties.get("nickname").toString());
		userInfo.put("email", kakao_account.get("email").toString());

		return userInfo;
	}

	public TokenDto getTokenByKakao(Map<String, String> userInfo) {

		String email = userInfo.get("email");

		if (!isExist(email)) {
			userRepository.save(UserEntity.builder()
				.username(userInfo.get("nickname"))
				.userEmail(email)
				.build());
		}

		TokenDto tokenDto = provider.createToken(email);

		redisTemplate.opsForValue()
			.set("RT:" + email, tokenDto.getRefreshToken(),
				tokenDto.getRefreshTokenExpireTime(), TimeUnit.MILLISECONDS);

		return tokenDto;
	}

	public boolean isExist(String email) {
		return userRepository.existsByUserEmail(email);
	}

	public LogoutUser kakaoLogout(HttpServletRequest request) {
		String accessToken = request.getParameter("state");
		String email = provider.getUserEmail(accessToken);

		if (redisTemplate.opsForValue().get("RT:" + email) != null) {
			redisTemplate.delete("RT:" + email);
		}

		long expiration = provider.getExpiration(accessToken);
		redisTemplate.opsForValue()
			.set("BLOCK:" + accessToken, "logout", expiration, TimeUnit.MILLISECONDS);

		return LogoutUser.builder().userEmail(email).build();
	}

}
