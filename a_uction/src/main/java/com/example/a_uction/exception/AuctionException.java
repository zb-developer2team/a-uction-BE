package com.example.a_uction.exception;

import com.example.a_uction.exception.constants.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AuctionException extends RuntimeException {

	private ErrorCode errorCode;
	private String message;

	public AuctionException(ErrorCode errorCode) {
		this.errorCode = errorCode;
		this.message = errorCode.getDescription();
	}
}
