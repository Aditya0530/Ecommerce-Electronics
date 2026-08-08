package com.ecommerce.main.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.ecommerce.main.enums.InventoryRole;
import com.ecommerce.main.model.Employee;
import com.ecommerce.main.service.EmailService;

@Service
public class EmailServiceImp implements EmailService {

	@Value("${spring.mail.username}")
	private String FROM_MAIL;
	@Autowired
	private JavaMailSender sender;

	@Override
	public void sendSimpleMail(String to, String subject, String text) {
		SimpleMailMessage mail = new SimpleMailMessage();
		mail.setFrom(FROM_MAIL);
		mail.setTo(to);
		mail.setSubject(subject);
		mail.setText(text);
		sender.send(mail);

	}

	@Override
	public void sendEmailMessage(Employee employeeEmail) {
		SimpleMailMessage mail = new SimpleMailMessage();
		mail.setTo(employeeEmail.getEmail());
		mail.setFrom(FROM_MAIL);
		mail.setSubject("Welcome to the Company - Registration Successful");
		String message = "Dear " + employeeEmail.getName() + ",\n\n" + "Welcome to [Your E-Commerce Company]!\n"
				+ "We are excited to have you on board.\n\n";
		if (employeeEmail.getInventoryRole() == InventoryRole.ADMIN) {
			message += """
                    As an *Admin*, you have full access to manage the platform, oversee operations, and ensure smooth workflow. \
                    Your leadership is vital in maintaining efficiency across all departments.
                    
                    """;
		} else if (employeeEmail.getInventoryRole() == InventoryRole.ORDERDELIVERY_HEAD) {
			message += """
                    As the *Order Delivery Head*, you will oversee order dispatch, manage logistics, and ensure timely deliveries. \
                    Your role is essential in providing customers with a seamless shopping experience.
                    
                    """;
		} else if (employeeEmail.getInventoryRole() == InventoryRole.HEAD) {
			message += """
                    As an *Employee*, you play a key role in supporting our e-commerce operations. \
                    Your contributions are valuable in helping us grow and serve our customers better.
                    
                    """;
		} else {
			message += "You have been assigned a role within our company. We look forward to working with you.\n\n";
		}
		message += "Here are your login details:\n" + "Username: " + employeeEmail.getUsername() + "\n" + "Password: "
				+ employeeEmail.getPassword() + "\n\n" + "If you have any questions, feel free to reach out.\n\n"
				+ "Best Regards,\n" + "[Your E-Commerce Team]";
		mail.setText(message);
		try {
			sender.send(mail);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
