package com.example.a_uction.exception.dto;

import com.example.a_uction.exception.constants.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {

	private ErrorCode errorCode;
	private String message;
}
