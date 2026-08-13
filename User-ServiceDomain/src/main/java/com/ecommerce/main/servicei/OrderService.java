package com.ecommerce.main.servicei;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ecommerce.main.model.Order;
import com.ecommerce.main.model.Product;
import com.ecommerce.main.model.User;


public interface OrderService {
public Order createOrder(Order order); 
}
