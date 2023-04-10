package com.example.a_uction.model.user.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class Verify {

	@AllArgsConstructor
	@NoArgsConstructor
	@Setter
	@Getter
	public static class Message {

		private String to;
		private String content;
	}
	@AllArgsConstructor
	@NoArgsConstructor
	@Setter
	@Getter
	@Builder
	public static class Request {

		private String type;
		private String contentType;
		private String countryCode;
		private String from;
		private String content;
		private List<Verify.Message> messages;
	}

	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	@Getter
	public static class Response {

		private String requestId;
		private LocalDateTime requestTime;
		private String statusCode;
		private String statusName;

	}
	@AllArgsConstructor
	@NoArgsConstructor
	@Setter
	@Getter
	public static class TestMessage {
		private String phoneNumber;
		private String content;
	}
}
