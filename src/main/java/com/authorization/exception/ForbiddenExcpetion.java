package com.authorization.exception;

import com.authorization.model.ErrorResponse;
import org.springframework.http.HttpStatus;


public class ForbiddenExcpetion extends RuntimeException {

	private final String error;

	public ForbiddenExcpetion(String error, String message) {
		super(message);
		this.error = error;
	}

	public ErrorResponse toErrorResponse() {
		return ErrorResponse.builder()
				.error(error)
				.message(getMessage())
				.build();
	}
}
