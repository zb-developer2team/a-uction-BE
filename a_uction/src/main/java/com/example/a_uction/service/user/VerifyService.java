package com.example.a_uction.service.user;

import static com.example.a_uction.exception.constants.ErrorCode.THIS_PHONE_NUMBER_ALREADY_AUTHENTICATION;
import static com.example.a_uction.exception.constants.ErrorCode.WRONG_CODE_INPUT;

import com.example.a_uction.exception.AuctionException;
import com.example.a_uction.model.user.dto.Verify;
import com.example.a_uction.model.user.entity.UserVerificationEntity;
import com.example.a_uction.model.user.repository.UserRepository;
import com.example.a_uction.model.user.repository.UserVerificationEntityRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
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
	private final UserVerificationEntityRepository verificationEntityRepository;

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
				.type("SMS")
				.contentType("COMM")
				.countryCode("82")
				.from("01033538090")
				.messages(messages)
				.content(sendMessage)
				.build();

		String jsonBody = objectMapper.writeValueAsString(request);

		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("x-ncp-apigw-timestamp", time.toString());
		headers.set("x-ncp-iam-access-key", accessKey);
		String signature = makeSignature(time);
		headers.set("x-ncp-apigw-signature-v2", signature);

		HttpEntity<String> body = new HttpEntity<>(jsonBody, headers);

		restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
		restTemplate.setErrorHandler(new DefaultResponseErrorHandler());

		return restTemplate.postForObject(
			new URI("https://sens.apigw.ntruss.com/sms/v2/services/" + this.serviceId + "/messages"), body, Verify.Response.class
		);
	}
	public boolean verifyCode(String code) {
		if (verificationEntityRepository.findByCode(code).isEmpty()) {
			throw new AuctionException(WRONG_CODE_INPUT);
		}
		deleteVerification(code);
		return true;
	}
	private void validatePhoneNumber(String phoneNumber) {
		if (userRepository.findByPhoneNumber(phoneNumber).isPresent()){
			throw new AuctionException(THIS_PHONE_NUMBER_ALREADY_AUTHENTICATION);
		}
	}

	private String generateVerificationCode(String phoneNumber) {
		String verifyCode = RandomStringUtils.random(6, true, true);
		verificationEntityRepository.save(UserVerificationEntity.builder()
			.code(verifyCode)
			.phoneNumber(phoneNumber)
			.build());

		return verifyCode;
	}

	private void deleteVerification(String code) {
		Optional<UserVerificationEntity> user = verificationEntityRepository.findByCode(code);
		user.ifPresent(verificationEntityRepository::delete);
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
