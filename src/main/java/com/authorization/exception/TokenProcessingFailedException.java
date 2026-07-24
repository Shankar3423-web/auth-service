package com.authorization.exception;


import com.authorization.model.ErrorResponse;
import org.springframework.http.HttpStatus;

public class TokenProcessingFailedException extends RuntimeException {

    private final String error = "access_token_refresh_failed";

    public TokenProcessingFailedException(String message) {
        super(message);
    }

    public ErrorResponse toErrorResponse() {
        return new ErrorResponse(// or 400 depending on your design
                error,
                getMessage()
        );
    }
}

