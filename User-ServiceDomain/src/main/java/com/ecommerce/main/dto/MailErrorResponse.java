package com.ecommerce.main.dto;

public class MailErrorResponse {

	 private String errorCode;
	 private String errorMessage;
	 
	 public MailErrorResponse(String errorCode, String errorMessage) {
		 
		 this.errorCode = errorCode;
		 this.errorMessage = errorMessage;
		 
	    }
	 
	 public String getErrorCode() {		 
		 return errorCode;		
	   }
	 
	 public String getErrorMessage() {
	        return errorMessage;
	    }
}
