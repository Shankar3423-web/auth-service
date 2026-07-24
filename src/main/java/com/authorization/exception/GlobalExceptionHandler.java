package com.authorization.exception;

import com.authorization.loggers.Loggers;
import com.authorization.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ForbiddenExcpetion.class)
	public ResponseEntity<ErrorResponse> handleForbidden(ForbiddenExcpetion ex) {
		return (ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.toErrorResponse()));
	}

	@ExceptionHandler(InvalidClientException.class)
	public ResponseEntity<ErrorResponse> handleInvalidClient(InvalidClientException ex) {
		return (ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.toErrorResponse()));
	}

	@ExceptionHandler(UnauthorizedException.class)
	public ResponseEntity<ErrorResponse> handleUnauthorized(UnauthorizedException ex) {
		return (ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.toErrorResponse()));
	}

	@ExceptionHandler(JwtKeyInitializationException.class)
	public ResponseEntity<ErrorResponse> handleJwtKeyInit(JwtKeyInitializationException ex) {
		return (ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.toErrorResponse()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {

		Loggers.AUTHORIZATION_LOGGER.error("<<handleValidationException>> ", ex);

		String errorMessage = "Request contains invalid or missing parameters";

		ErrorResponse errorResponse = new ErrorResponse(
				"validation_error",
				errorMessage
		);

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}


	@ExceptionHandler(AuthenticationFailedException.class)
	public ResponseEntity<ErrorResponse> handleAuthenticationFailure(AuthenticationFailedException ex) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.toErrorResponse());
	}

	@ExceptionHandler(AccessCheckFailedException.class)
	public ResponseEntity<ErrorResponse> handleAccessDenied(AccessCheckFailedException ex) {
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.toErrorResponse());
	}

	@ExceptionHandler(TokenProcessingFailedException.class)
	public ResponseEntity<ErrorResponse> handleRefreshFailure(TokenProcessingFailedException ex) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.toErrorResponse());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
		ErrorResponse error = new ErrorResponse( "internal_error",
				ex.getMessage());
		return (ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error));
	}

}
