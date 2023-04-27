package com.example.a_uction.exception.constants;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

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

	USER_NOT_FOUND(BAD_REQUEST, "유저를 찾을 수 없습니다."),
	USER_PROFILE_IMAGE_IS_EMPTY(BAD_REQUEST, "삭제할 프로필사진이 없습니다."),
	THIS_PHONE_NUMBER_ALREADY_AUTHENTICATION(BAD_REQUEST, "이 번호는 이미 인증이 완료되었습니다."),
	VERIFICATION_CODE_TIME_OUT(BAD_REQUEST, "인증번호 유효 시간이 만료됐습니다."),
	WRONG_CODE_INPUT(BAD_REQUEST, "코드를 잘못 입력하셨습니다. 처음부터 다시 시도해주세요."),
	ENTERED_THE_WRONG_PASSWORD(BAD_REQUEST, "비밀번호를 확인 해 주세요."),
	EMAIL_FORMAT_ERROR(BAD_REQUEST, "이메일 형식이 올바르지 않습니다."),
	THIS_EMAIL_ALREADY_EXIST(BAD_REQUEST, "해당 이메일은 이미 존재합니다."),
	SEND_ACCESS_TOKEN_ERROR(BAD_REQUEST, "엑세스 토큰 전송에 실패하였습니다."),

	// balance
	NOT_ENOUGH_BALANCE(BAD_REQUEST, "예치금이 충분하지 않습니다."),

	// jwt
	INVALID_TOKEN(UNAUTHORIZED, "토큰이 올바르지 않습니다."),
	UNKNOWN_ERROR(UNAUTHORIZED, "인증 관련 알수없는 에러입니다."),
	EXPIRED_TOKEN(UNAUTHORIZED, "토큰이 만료되었습니다."),
	EMPTY_TOKEN_ERROR(UNAUTHORIZED, "토큰이 비어있습니다."),
	FAILED_VERIFY_SIGNATURE(UNAUTHORIZED, "시그니처 검증에 실패한 토큰입니다."),
	LOGOUT_USER_ERROR(UNAUTHORIZED, "로그아웃 된 사용자입니다."),
	INVALID_REFRESH_TOKEN(UNAUTHORIZED, "리프레시 토큰이 유효하지 않습니다."),
	NOT_MATCH_REFRESH_TOKEN(UNAUTHORIZED, "리프레시 토큰이 일치하지 않습니다."),

	// kakao
	INVALID_PARSE_ERROR(UNAUTHORIZED, "JSON 파싱에 실패하였습니다."),

	// s3
	INVALID_FILE_FORM(BAD_REQUEST, "잘못된 파일 형식입니다."),
	FAILED_FILE_UPLOAD(BAD_REQUEST, "파일 업로드에 실패하였습니다."),

	//auction
	BEFORE_START_TIME (BAD_REQUEST, "경매 시작 시간이 등록 시간보다 이전입니다."),
	END_TIME_EARLIER_THAN_START_TIME (BAD_REQUEST, "경매 종료 시간이 경매 시작 시간보다 이전입니다."),
	NOT_FOUND_AUCTION_LIST(BAD_REQUEST, "등록하신 경매 이력이 없습니다."),
	NOT_FOUND_AUCTION_STATUS_LIST(BAD_REQUEST, "요청하신 상태의 경매 리스트가 없습니다."),
	UNABLE_UPDATE_AUCTION(BAD_REQUEST, "진행 예정인 경매만 수정이 가능합니다."),
	UNABLE_DELETE_AUCTION(BAD_REQUEST, "진행 예정인 경매만 삭제가 가능합니다."),

	//bidding
	REGISTER_CANNOT_BID(BAD_REQUEST, "본인 경매 입찰에 참여할 수 없습니다."),
	AUCTION_NOT_STARTS(BAD_REQUEST, "아직 경매 시작 시간 전입니다."),
	AUCTION_FINISHED(BAD_REQUEST, "이미 종료된 경매 입니다."),
	NOT_BIDDABLE_PRICE(BAD_REQUEST, "입찰 시도가 가능한 금액이 아닙니다."),
	LAST_BIDDER_SAME(BAD_REQUEST, "마지막 입찰자가 본인입니다."),
	UNABLE_CREATE_BID(BAD_REQUEST, "입찰에 실패했습니다."),
	BIDDING_NOT_FOUND(BAD_REQUEST, "입찰이 없습니다."),
	AUCTION_NOT_FINISHED(BAD_REQUEST, "아직 입찰이 진행중입니다."),


	//wish
	WISH_LIST_IS_EMPTY(BAD_REQUEST, "관심 등록한 경매 리스트가 없습니다."),
	NOT_EXIST_WISH_LIST_BY_USER(BAD_REQUEST, "해당 경매를 관심 등록한 유저가 없습니다."),
	ALREADY_EXIST_WISH_ITEM(BAD_REQUEST, "해당 경매를 이미 관심 등록하였습니다."),
	UNABLE_DELETE_WISH_LIST(BAD_REQUEST, "관심 리스트에서 삭제할 수 없습니다."),
	;
	private final HttpStatus httpStatus;
	private final String description;
}
