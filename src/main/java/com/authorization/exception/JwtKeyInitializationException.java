package com.authorization.exception;

import com.authorization.model.ErrorResponse;
import org.springframework.http.HttpStatus;

public class JwtKeyInitializationException extends RuntimeException {

	private final String error;

	public JwtKeyInitializationException(String error, String message) {
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