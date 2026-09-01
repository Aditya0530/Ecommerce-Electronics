package com.ecommerce.main.exceptionhandler;

public class DuplicateProductFoundException extends RuntimeException {
	
	public DuplicateProductFoundException(String message) {
		
		super(message);
	}

}
