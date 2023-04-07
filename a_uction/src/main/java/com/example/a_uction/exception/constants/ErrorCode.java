package com.example.a_uction.exception.constants;

import static org.springframework.http.HttpStatus.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"내부 서버 오류가 발생하였습니다."),
	DATABASE_CONSTRAINTS_VIOLATED(BAD_REQUEST, "중복된 데이터가 있거나 데이터 값이 다릅니다."),
	INVALID_REQUEST(BAD_REQUEST, "입력하신 데이터를 확인해 주세요. 잘못 된 요청입니다."),


	INVALID_TOKEN(FORBIDDEN, "토큰이 만료되었습니다."),
	EMAIL_FORMAT_ERROR(BAD_REQUEST, "이메일 형식이 올바르지 않습니다."),
	THIS_EMAIL_ALREADY_EXIST(BAD_REQUEST, "해당 이메일은 이미 존재합니다.");
	private final HttpStatus httpStatus;
	private final String description;
}
