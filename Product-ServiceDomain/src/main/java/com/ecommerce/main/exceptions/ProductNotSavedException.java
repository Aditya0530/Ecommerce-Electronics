package com.ecommerce.main.exceptions;

public class ProductNotSavedException extends RuntimeException{

	public ProductNotSavedException(String msg) {
		super(msg);
	}
}
