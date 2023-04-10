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
	AUCTION_NOT_FOUND(BAD_REQUEST, "해당 경매를 찾을 수 없습니다."),


	INVALID_TOKEN(UNAUTHORIZED, "토큰이 만료되었습니다."),
	USER_NOT_FOUND(BAD_REQUEST, "유저를 찾을 수 없습니다."),
	ENTERED_THE_WRONG_PASSWORD(BAD_REQUEST, "비밀번호를 확인 해 주세요."),
	EMAIL_FORMAT_ERROR(BAD_REQUEST, "이메일 형식이 올바르지 않습니다."),
	THIS_EMAIL_ALREADY_EXIST(BAD_REQUEST, "해당 이메일은 이미 존재합니다."),
	LOGOUT_USER_ERROR(UNAUTHORIZED, "로그아웃 된 사용자입니다."),
	INVALID_REFRESH_TOKEN(UNAUTHORIZED, "리프레시 토큰이 유효하지 않습니다."),
	NOT_MATCH_REFRESH_TOKEN(UNAUTHORIZED, "리프레시 토큰이 일치하지 않습니다."),
	SEND_ACCESS_TOKEN_ERROR(BAD_REQUEST, "엑세스 토큰 전송에 실패하였습니다."),

	// kakao
	INVALID_PARSE_ERROR(BAD_REQUEST, "JSON 파싱에 실패하였습니다."),

	//auction
	BEFORE_START_TIME (BAD_REQUEST, "경매 시작 시간이 등록 시간보다 이전입니다."),
	END_TIME_EARLIER_THAN_START_TIME (BAD_REQUEST, "경매 종료 시간이 경매 시작 시간보다 이전입니다."),
	NOT_FOUND_AUCTION_LIST(BAD_REQUEST, "등록하신 경매 이력이 없습니다."),
	UNABLE_UPDATE_AUCTION(BAD_REQUEST, "진행 예정인 경매만 수정이 가능합니다.")
	;
	private final HttpStatus httpStatus;
	private final String description;
}
