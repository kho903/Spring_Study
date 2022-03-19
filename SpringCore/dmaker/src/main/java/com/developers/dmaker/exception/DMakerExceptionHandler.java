package com.developers.dmaker.exception;

import static com.developers.dmaker.exception.DMakerErrorCode.*;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.developers.dmaker.dto.DMakerErrorResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class DMakerExceptionHandler {

	@ExceptionHandler(DMakerException.class)
	public DMakerErrorResponse handlerException(
		DMakerException e,
		HttpServletRequest request
	) {
		log.error("errorCode : {}, url : {}, message : {}",
			e.getDMakerErrorCode(), request.getRequestURI(), e.getDetailMessage());
		return DMakerErrorResponse.builder()
			.errorCode(e.getDMakerErrorCode())
			.errorMessage(e.getDetailMessage())
			.build();
	}

	@ExceptionHandler(value = {
		HttpRequestMethodNotSupportedException.class,
		MethodArgumentNotValidException.class
	})
	public DMakerErrorResponse handlerBadRequest(
		Exception e,
		HttpServletRequest request
	) {

		log.error("url : {}, message : {}",
			request.getRequestURI(), e.getMessage());
		return DMakerErrorResponse.builder()
			.errorCode(INVALID_REQUEST)
			.errorMessage(INVALID_REQUEST.getMessage())
			.build();
	}

	@ExceptionHandler(value = Exception.class)
	public DMakerErrorResponse handleException(
		Exception e,
		HttpServletRequest request
	) {

		log.error("url : {}, message : {}",
			request.getRequestURI(), e.getMessage());
		return DMakerErrorResponse.builder()
			.errorCode(INTERNAL_SERVER_ERROR)
			.errorMessage(INTERNAL_SERVER_ERROR.getMessage())
			.build();
	}
}
