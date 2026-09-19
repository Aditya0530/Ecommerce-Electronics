package com.ecommerce.main.exceptionhandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.catalina.connector.ClientAbortException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestNotUsableException;

import com.ecommerce.main.dto.ErrorDto;
import com.ecommerce.main.dto.MailErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(InvalidCredentialsException.class)
	public ResponseEntity<ErrorDto> handleLoginExceptions(InvalidCredentialsException ex) {

		ErrorDto errDto = new ErrorDto(ex.getMessage());

		return new ResponseEntity<>(errDto, HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorDto> handleNoResourceFoundExceptions(ResourceNotFoundException ex) {

		ErrorDto errDto = new ErrorDto(ex.getMessage());

		return new ResponseEntity<>(errDto, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {

		Map<String, String> errors = new HashMap<>();

		for (FieldError error : ex.getBindingResult().getFieldErrors()) {

			errors.put(error.getField(), error.getDefaultMessage());
		}

		return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(EmailSendingException.class)
	public ResponseEntity<MailErrorResponse> handleEmailSendingException(EmailSendingException ex) {

		MailErrorResponse errorResponse = new MailErrorResponse("EMAIL_SENDING_ERROR", "Failed to send email");

		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(UserIdNotFoundException.class)
	public ResponseEntity<ErrorDto> handleUserIdNotFoundExceptions(UserIdNotFoundException ex) {

		ErrorDto errDto = new ErrorDto(ex.getMessage());

		return new ResponseEntity<>(errDto, HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(DuplicateProductFoundException.class)
	public ResponseEntity<ErrorDto> handleDuplicateIdExceptions(DuplicateProductFoundException ex) {

		ErrorDto errDto = new ErrorDto(ex.getMessage());

		return new ResponseEntity<>(errDto, HttpStatus.CONFLICT);
	}

	// CLIENT DISCONNECTED
	@ExceptionHandler({ ClientAbortException.class, AsyncRequestNotUsableException.class })
	public void handleClientAbortException(Exception ex) {

		// Client/browser disconnected.
		// No response should be written here.

	}

	// GENERIC EXCEPTION
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorDto> handleParentExceptions(Exception ex) {

		ex.printStackTrace();

		ErrorDto errDto = new ErrorDto(ex.getMessage() != null ? ex.getMessage() : "Internal Server Error");

		return new ResponseEntity<>(errDto, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}