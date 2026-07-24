package com.authorization.exception;

import com.authorization.model.ErrorResponse;
import org.springframework.http.HttpStatus;

public class AuthenticationFailedException extends RuntimeException {

    private final String error;

    public AuthenticationFailedException(String message) {
        super(message);
        this.error = "authentication_failed";
    }

    public ErrorResponse toErrorResponse() {
        return ErrorResponse.builder()
                .error(error)
                .message(getMessage())
                .build();
    }
}
