package com.ecommerce.main.exceptions;

import java.util.Map;

public class ValidationException extends RuntimeException{
	private final Map<String, String> error;
	
	public ValidationException(Map<String, String> error) {
		super("Vlaidation Failed");
		this.error = error;
		
	}
	
	public Map<String, String> getError(){
		return error;
	}
}
