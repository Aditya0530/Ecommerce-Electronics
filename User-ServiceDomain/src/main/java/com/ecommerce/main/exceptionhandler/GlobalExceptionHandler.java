package com.ecommerce.main.exceptionhandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.HashMap;
import java.util.Map;

import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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
	public ResponseEntity<MailErrorResponse> handleEmailSendingException(EmailSendingException e) {
		MailErrorResponse errorResponse = new MailErrorResponse("EMAIL_SENDING_ERROR", "Failed to send email");
		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorDto> handleParentExceptions(Exception ex) {
		ErrorDto errDto = new ErrorDto(ex.getMessage());
		return new ResponseEntity<>(errDto, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(UserIdNotFoundException.class)
	public ResponseEntity<ErrorDto> handleUserIdNotFoundExceptions(UserIdNotFoundException ex) {
		ErrorDto errDto = new ErrorDto(ex.getMessage());
		return new ResponseEntity<>(errDto, HttpStatus.UNAUTHORIZED);
	}
	
	@ExceptionHandler(DuplicateProductFoundException.class)
	public ResponseEntity<ErrorDto> handleDuplicateIdExceptions(DuplicateProductFoundException ex) {
		ErrorDto errDto=new ErrorDto(ex.getMessage());
		return new ResponseEntity<>(errDto, HttpStatus.UNAUTHORIZED);
	}

}
