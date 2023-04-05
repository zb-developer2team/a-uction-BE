package com.example.a_uction.exception;

import static com.example.a_uction.exception.constants.ErrorCode.DATABASE_CONSTRAINTS_VIOLATED;
import static com.example.a_uction.exception.constants.ErrorCode.INTERNAL_SERVER_ERROR;
import static com.example.a_uction.exception.constants.ErrorCode.INVALID_REQUEST;

import com.example.a_uction.exception.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(AuctionException.class)
	public ErrorResponse handleAuctionException(AuctionException e) {
		log.error("{} is occurred", e.getErrorCode());

		return new ErrorResponse(e.getErrorCode(), e.getMessage());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
		log.error("MethodArgumentNotValidException is occurred.", e);

		return new ErrorResponse(INVALID_REQUEST, INVALID_REQUEST.getDescription());
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ErrorResponse handleDataIntegrityViolationException(DataIntegrityViolationException e) {
		log.error("DataIntegrityViolationException is occurred.", e);

		return new ErrorResponse(DATABASE_CONSTRAINTS_VIOLATED, DATABASE_CONSTRAINTS_VIOLATED.getDescription());
	}
	@ExceptionHandler(Exception.class)
	public ErrorResponse handleException(Exception e) {
		log.error("Exception is occurred.", e);

		return new ErrorResponse(INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR.getDescription());
	}
}
