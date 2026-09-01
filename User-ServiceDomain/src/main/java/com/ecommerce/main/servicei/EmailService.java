package com.ecommerce.main.servicei;

import com.ecommerce.main.model.Order;
import com.ecommerce.main.model.User;

public interface EmailService { 
	
	public void sendEmail(String to, String subject, String body);
    public void sendOrderConfirmationEmail(User user,Order order);
    
}
