package com.example.a_uction.service.user;

import java.util.HashMap;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class KakaoService {

	@Value("${oauth2.kakao.restApiKey}")
	private String REST_API_KEY;
	@Value("${oauth2.kakao.redirectUrl}")
	private String REDIRECT_URL;

	public String getAccessToken (String authorize_code) {

		String reqURL = "https://kauth.kakao.com/oauth/token";

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "authorization_code");
		params.add("client_id", REST_API_KEY );
		params.add("redirect_url", REDIRECT_URL);
		params.add("code", authorize_code);

		RestTemplate restTemplate = new RestTemplate();
		HttpEntity<MultiValueMap<String,String>> kakaoTokenReq= new HttpEntity<>(params, headers);

		ResponseEntity<String> response = restTemplate.exchange(
			reqURL,
			HttpMethod.POST,
			kakaoTokenReq,
			String.class
		);

		String tokenJson = response.getBody();

		JSONParser jsonParser = new JSONParser();
		JSONObject jsonObject;

		try {
			jsonObject = (JSONObject) jsonParser.parse(tokenJson);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}

		String accessToken = jsonObject.get("access_token").toString();
		return accessToken;
	}

	public HashMap<String, String> getUserInfoByKakao (String accessToken) {
		String reqURL = "https://kapi.kakao.com/v2/user/me";


		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + accessToken);

		RestTemplate restTemplate = new RestTemplate();
		HttpEntity<MultiValueMap<String,String>> kakaoUserReq= new HttpEntity<>(headers);

		ResponseEntity<String> response = restTemplate.exchange(
			reqURL,
			HttpMethod.POST,
			kakaoUserReq,
			String.class
		);

		String userInfoJson = response.getBody();

		JSONParser jsonParser = new JSONParser();
		JSONObject jsonObject;

		try {
			jsonObject = (JSONObject) jsonParser.parse(userInfoJson);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}

		HashMap<String, String> userInfo = new HashMap<>();
		JSONObject properties = (JSONObject) jsonObject.get("properties");
		JSONObject kakao_account = (JSONObject) jsonObject.get("kakao_account");

		userInfo.put("nickname",properties.get("nickname").toString());
		userInfo.put("email", kakao_account.get("email").toString());

		return userInfo;
	}
}
