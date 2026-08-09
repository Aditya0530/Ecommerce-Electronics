package com.ecommerce.main.serviceimpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.ecommerce.main.exceptionhandler.EmailSendingException;
import com.ecommerce.main.model.Order;
import com.ecommerce.main.model.User;
import com.ecommerce.main.servicei.EmailService;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl implements EmailService {
	
	@Value("${spring.mail.username}")
	private String mailFrom;
	
	 @Autowired
	 private JavaMailSender javaMailSender;
	 
	 private static final Logger LOGGER = LoggerFactory.getLogger(EmailServiceImpl.class);

	 
	@Override
	public void sendEmail(String to, String subject, String body) {
		try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(mailFrom);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            javaMailSender.send(message);
        } catch (MailException e) {
        	 // here Logging the error
            LOGGER.error("Failed to send email", e);
            // here Throwing a custom exception
            throw new EmailSendingException("Failed to send email", e);
        }
    
	}

	public void sendOrderConfirmationEmail(User user, Order order) {
	    String subject = "🛍️ Your Order Confirmation - " + user.getName();
	    
	    String message = "<div style='font-family: Arial, sans-serif; padding: 20px;'>" +
	    	    "<h2 style='color: #008CBA;'>Your Order is Confirmed! 🎉</h2>" +
	    	    "<p>Dear " + user.getName() + ",</p>" +
	    	    "<p>Thank you for shopping with <b>YourStore</b>. Your order has been successfully placed.</p>" +
	    	    "<h3>Order Summary:</h3>" +
	    	    "<table style='width: 100%; border-collapse: collapse; border: 1px solid #ddd;'>" +
	    	    "<tr style='background-color: #f2f2f2;'><th>Product Name</th><th>Quantity</th><th>Price</th><th>Total</th></tr>" +
	    	    "<tr>" +
	    	    "<td>" + order.getProduct().getProductName() + "</td>" +
	    	    "<td>" + order.getQuantity() + "</td>" +
	    	    "<td>₹" + order.getProduct().getPrice() + "</td>" +
	    	    "<td>₹" + (order.getQuantity() * order.getProduct().getPrice()) + "</td>" +
	    	    "</tr>" +
	    	    "</table>" +
	    	    "<p><b>Delivery Charges:</b> ₹" + order.getDeliverycharges() + "</p>" +
	    	    "<p><b>Grand Total:</b> ₹" + (order.getTotalAmount() + order.getDeliverycharges()) + "</p>" +
	    	    "<hr>" +
	    	    "<p>Your order will be delivered to the following address:</p>" +
	    	    "<p><b>" + order.getAddress().getArea() + ", " + order.getAddress().getCity() + " - " + order.getAddress().getPincode() + "</b></p>" +
	    	    "<p>📞 Contact Number: " + order.getAddress().getContactNo() + "</p>" +
	    	    "<p>We will notify you once your order is shipped.</p>" +
	    	    "<hr>" +
	    	    "<p>📞 Need help? Contact us at <a href='mailto:support@yourstore.com'>support@yourstore.com</a></p>" +
	    	    "</div>";


	    try {
	        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
	        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
	        helper.setFrom(mailFrom);
	        helper.setTo(user.getEmail());
	        helper.setSubject(subject);
	        helper.setText(message, true); 

	        javaMailSender.send(mimeMessage);
	    } catch (Exception e) {
	        LOGGER.error("Failed to send email", e);
	        throw new EmailSendingException("Failed to send email", e);
	    }
	}
}

