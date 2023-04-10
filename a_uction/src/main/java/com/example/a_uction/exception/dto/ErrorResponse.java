package com.example.a_uction.exception.dto;

import com.example.a_uction.exception.constants.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ErrorResponse {

	private HttpStatus httpStatus;
	private ErrorCode errorCode;
	private String message;

	public static ErrorResponse from (ErrorCode errorCode) {
		return new ErrorResponse(errorCode.getHttpStatus(), errorCode, errorCode.getDescription());
	}
}
