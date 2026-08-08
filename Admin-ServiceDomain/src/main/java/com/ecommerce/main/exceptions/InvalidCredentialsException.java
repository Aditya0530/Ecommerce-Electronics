package com.ecommerce.main.exceptions;

public class InvalidCredentialsException extends RuntimeException {
	
	public InvalidCredentialsException(String message) {
        super(message);
    }
}
