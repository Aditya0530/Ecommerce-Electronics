package com.ecommerce.main.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.ecommerce.main.dto.ErrorDto;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
	private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(ProductException.class)
	public ResponseEntity<ErrorDto> handleInvalidProductException(ProductException ex) {
		LOG.error("Handling ProductException: {}", ex.getMessage());
		ErrorDto err = new ErrorDto(ex.getMessage());
		return new ResponseEntity<>(err, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String,String>> handleValidationException(ValidationException ex) {
		Map<String,String> errorResponse=new HashMap<>(ex.getErrors());
		return new ResponseEntity<>(errorResponse,HttpStatus.BAD_REQUEST);
		
	}

	@ExceptionHandler(ProductNotSavedException.class)
	public ResponseEntity<ErrorDto> handleProductNotSavedException(ProductNotSavedException ex) {
		LOG.error("Handling ProductNotSavedException: {}", ex.getMessage());
		ErrorDto err = new ErrorDto(ex.getMessage());
		return new ResponseEntity<>(err, HttpStatus.NOT_FOUND); // Return 406
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorDto> handleGenericException(Exception ex) {
		LOG.error("Handling Generic Exception: {}", ex.getMessage(), ex);
		ErrorDto err = new ErrorDto(ex.getMessage());
		return new ResponseEntity<>(err, HttpStatus.INTERNAL_SERVER_ERROR); // Return 500
	}
}
