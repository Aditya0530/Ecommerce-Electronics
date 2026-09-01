package com.ecommerce.main.serviceimpl;

import org.springframework.stereotype.Service;

import com.ecommerce.main.model.Order;
import com.ecommerce.main.servicei.PaymentService;

@Service
public class PaymentServiceImpl implements PaymentService {

	public String processPayment(int userId, int productId, double totalAmount) {

		String message = "Payment Successfull With Amount Of ₹" + totalAmount;
		return message;

	}

}
