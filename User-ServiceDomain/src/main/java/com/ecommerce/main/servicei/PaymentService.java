package com.ecommerce.main.servicei;

public interface PaymentService {

	public String processPayment(int userId, int productId, double totalAmount);
}
