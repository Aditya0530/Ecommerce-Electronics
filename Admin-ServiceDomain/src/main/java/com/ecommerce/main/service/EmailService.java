package com.ecommerce.main.service;

import com.ecommerce.main.model.Employee;

public interface EmailService {
	public void sendSimpleMail(String to, String subject, String text);
    public void sendEmailMessage(Employee employeeEmail);
}
