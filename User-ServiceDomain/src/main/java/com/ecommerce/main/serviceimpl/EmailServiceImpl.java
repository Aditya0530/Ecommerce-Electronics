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

	@Override
	public void sendOrderConfirmationEmail(User user, Order order) {

	    String subject = "🛍️ Your Order Confirmation - " + user.getName();

	    StringBuilder message = new StringBuilder();

	    message.append("<div style='font-family: Arial, sans-serif; padding: 20px;'>");

	    message.append("<h2 style='color: #008CBA;'>Your Order is Confirmed! 🎉</h2>");

	    message.append("<p>Dear ")
	            .append(user.getName())
	            .append(",</p>");

	    message.append("<p>Thank you for shopping with <b>YourStore</b>. ")
	            .append("Your order has been successfully placed.</p>");

	    // ==============================
	    // ORDER SUMMARY
	    // ==============================

	    message.append("<h3>Order Summary:</h3>");

	    message.append(
	            "<table style='width: 100%; border-collapse: collapse; border: 1px solid #ddd;'>"
	    );

	    message.append(
	            "<tr style='background-color: #f2f2f2;'>"
	            + "<th>Product Name</th>"
	            + "<th>Quantity</th>"
	            + "<th>Price</th>"
	            + "<th>Total</th>"
	            + "</tr>"
	    );

	    message.append("<tr>");

	    message.append("<td>")
	            .append(order.getProduct().getProductName())
	            .append("</td>");

	    message.append("<td>")
	            .append(order.getQuantity())
	            .append("</td>");

	    message.append("<td>₹")
	            .append(order.getProduct().getPrice())
	            .append("</td>");

	    message.append("<td>₹")
	            .append(order.getQuantity() * order.getProduct().getPrice())
	            .append("</td>");

	    message.append("</tr>");

	    message.append("</table>");

	    // ==============================
	    // DELIVERY CHARGES
	    // ==============================

	    message.append("<p><b>Delivery Charges:</b> ₹")
	            .append(order.getDeliverycharges())
	            .append("</p>");

	    // ==============================
	    // GRAND TOTAL
	    // ==============================

	    message.append("<p><b>Grand Total:</b> ₹")
	            .append(order.getTotalAmount() + order.getDeliverycharges())
	            .append("</p>");

	    message.append("<hr>");

	    // ==============================
	    // ADDRESS
	    // ==============================

	    if (order.getAddress() != null) {

	        message.append("<p>Your order will be delivered to:</p>");

	        message.append("<p><b>")
	                .append(order.getAddress().getArea())
	                .append(", ")
	                .append(order.getAddress().getCity())
	                .append(" - ")
	                .append(order.getAddress().getPincode())
	                .append("</b></p>");

	        message.append("<p>📞 Contact Number: ")
	                .append(order.getAddress().getContactNo())
	                .append("</p>");

	    } else {

	        message.append(
	                "<p><b>Delivery Address:</b> "
	                + "Address will be updated before delivery.</p>"
	        );
	    }

	    message.append("<p>We will notify you once your order is shipped.</p>");

	    message.append("<hr>");

	    message.append(
	            "<p>📞 Need help? Contact us at "
	            + "<a href='mailto:support@yourstore.com'>"
	            + "support@yourstore.com"
	            + "</a></p>"
	    );

	    message.append("</div>");

	    // ==============================
	    // SEND EMAIL
	    // ==============================

	    try {

	        MimeMessage mimeMessage =
	                javaMailSender.createMimeMessage();

	        MimeMessageHelper helper =
	                new MimeMessageHelper(mimeMessage, true);

	        helper.setFrom(mailFrom);

	        helper.setTo(user.getEmail());

	        helper.setSubject(subject);

	        helper.setText(message.toString(), true);

	        javaMailSender.send(mimeMessage);

	    } catch (Exception e) {

	        LOGGER.error("Failed to send order confirmation email", e);

	        throw new EmailSendingException(
	                "Failed to send order confirmation email",
	                e
	        );
	    }
	}
}

