package com.example.a_uction.exception.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
	INTERNAL_SERVER_ERROR("내부 서버 오류가 발생하였습니다."),
	DATABASE_CONSTRAINTS_VIOLATED("데이터베이스 제약조건 위반!!"),
	INVALID_REQUEST("입력하신 데이터를 확인해 주세요. 잘못 된 요청입니다."),


	EMAIL_FORMAT_ERROR("이메일 형식이 올바르지 않습니다."),
	THIS_EMAIL_ALREADY_EXIST("해당 이메일은 이미 존재합니다.");
	private final String description;
}
