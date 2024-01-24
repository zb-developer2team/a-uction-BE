package com.example.a_uction.service.user;

import static com.example.a_uction.exception.constants.ErrorCode.THIS_PHONE_NUMBER_ALREADY_AUTHENTICATION;
import static com.example.a_uction.exception.constants.ErrorCode.VERIFICATION_CODE_TIME_OUT;
import static com.example.a_uction.exception.constants.ErrorCode.WRONG_CODE_INPUT;

import com.example.a_uction.exception.AuctionException;
import com.example.a_uction.model.user.dto.Verify;
import com.example.a_uction.model.user.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

@Transactional
@Service
@RequiredArgsConstructor
public class VerifyService {
	private static final String REDIS_PACKAGE = "verification:";

	@Value("${sms.adminPhoneNum}")
	private String ADMIN_PHONE_NUM;
	@Value("${sms.contentType}")
	private String CONTENT_TYPE;
	@Value("${sms.countryCode}")
	private String COUNTRY_CODE;
	@Value("${sms.type}")
	private String TYPE;

	@Value("${sms.timeOut}")
	private Long TIME_FOR_VERIFICATION;
	@Value("${sms.serviceId}")
	private String serviceId;
	@Value("${sms.accessKey}")
	private String accessKey;
	@Value("${sms.secretKey}")
	private String secretKey;


	private final ObjectMapper objectMapper;
	private final HttpHeaders headers;
	private final RestTemplate restTemplate;
	private final UserRepository userRepository;
	private final RedisTemplate<String, Object> redisTemplate;

	public Verify.Response sendVerificationCode(String phoneNumber)
		throws NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException, URISyntaxException {

		validatePhoneNumber(phoneNumber);

		String verifyCode = generateVerificationCode(phoneNumber);

		Long time = System.currentTimeMillis();
		String sendMessage = messageFormat(verifyCode);
		List<Verify.Message> messages = new ArrayList<>();
		messages.add((
			new Verify.Message(phoneNumber, sendMessage)
		));

		Verify.Request request =
			Verify.Request.builder()
				.type(TYPE)
				.contentType(CONTENT_TYPE)
				.countryCode(COUNTRY_CODE)
				.from(ADMIN_PHONE_NUM)
				.messages(messages)
				.content(sendMessage)
				.build();

		String jsonBody = objectMapper.writeValueAsString(request);

		String signature = makeSignature(time);
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("x-ncp-apigw-timestamp", time.toString());
		headers.set("x-ncp-iam-access-key", accessKey);
		headers.set("x-ncp-apigw-signature-v2", signature);

		HttpEntity<String> body = new HttpEntity<>(jsonBody, headers);

		restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
		restTemplate.setErrorHandler(new DefaultResponseErrorHandler());

		return restTemplate.postForObject(
			new URI("https://sens.apigw.ntruss.com/sms/v2/services/" + this.serviceId + "/messages"), body, Verify.Response.class
		);
	}
	public boolean verifyCode(Verify.Form form) {
		String verificationCode = (String) redisTemplate.opsForValue().get(REDIS_PACKAGE + form.getPhoneNumber());
		validateCodeForm(verificationCode, form);
		return true;
	}
	private void validatePhoneNumber(String phoneNumber) {
		if (userRepository.findByPhoneNumber(phoneNumber).isPresent()){
			throw new AuctionException(THIS_PHONE_NUMBER_ALREADY_AUTHENTICATION);
		}
	}
	private void validateCodeForm(String verificationCode, Verify.Form form) {
		if (verificationCode == null) {
			throw new AuctionException(VERIFICATION_CODE_TIME_OUT);
		}
		if (!verificationCode.equals(form.getCode())) {
			throw new AuctionException(WRONG_CODE_INPUT);
		}
		deleteVerificationCode(form.getPhoneNumber());
	}

	private String generateVerificationCode(String phoneNumber) {
		String verifyCode = RandomStringUtils.random(6, true, true);

		redisTemplate.opsForValue().set(REDIS_PACKAGE + phoneNumber,
			verifyCode, TIME_FOR_VERIFICATION, TimeUnit.MINUTES);

		return verifyCode;
	}

	private void deleteVerificationCode(String phoneNumber) {
		redisTemplate.delete(REDIS_PACKAGE + phoneNumber);
	}

	private String messageFormat(String randomCode) {

		return "[A+UCTION] 인증번호:"
			+ randomCode
			+ "\n"
			+ "인증번호를 입력해 주세요.";
	}

	private String makeSignature(Long time)
		throws NoSuchAlgorithmException, InvalidKeyException{


		String message = "POST"
			+ " "
			+ "/sms/v2/services/" + serviceId + "/messages"
			+ "\n"
			+ time.toString()
			+ "\n"
			+ this.accessKey;

		SecretKeySpec signingKey = new SecretKeySpec(
			this.secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
		Mac mac = Mac.getInstance("HmacSHA256");
		mac.init(signingKey);

		byte[] rawHmac = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));

		return Base64.encodeBase64String(rawHmac);
	}
}
