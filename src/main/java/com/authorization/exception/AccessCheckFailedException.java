package com.authorization.exception;

import com.authorization.model.ErrorResponse;
import org.springframework.http.HttpStatus;

// ignored warnings
public class AccessCheckFailedException extends RuntimeException {

    private final String error = "access_denied";

    public AccessCheckFailedException(String message) {
        super(message);
    }

    public ErrorResponse toErrorResponse() {
        return ErrorResponse.builder()
                .error(error)
                .message(getMessage())
                .build();
    }
}
