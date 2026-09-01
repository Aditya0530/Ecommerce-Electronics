package com.ecommerce.main.servicei;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.repository.query.Param;

import java.util.List;

import com.ecommerce.main.dto.ProductDto;

import com.ecommerce.main.dto.UserDto;
import com.ecommerce.main.enums.StatusOrder;
import com.ecommerce.main.model.Order;
import com.ecommerce.main.model.Product;
import com.ecommerce.main.model.User;

public interface UserService {

	public UserDto saveUser(User user);

	public User loginUser(String username, String password);

	public Object loginAdmin(String username, String password);

	public User getUser(int userId);

	public Iterable<Product> getAll();

	public Iterable<Product> getByName(String productName);

	public void addToCart(int userId, int productId);

	public String placeOrder(int userId, int productId, Order order);

	public void removeFromCart(int userId, int productId);

	public List<Order> getByUserIdProductId(int userId, int productId);

	public void orderStatus(int orderId, StatusOrder orderStatus);

	public Map<String, Object> viewCart(int userId);

	public List<Order> getOrderByUserId(int userId);

	public List<Product> getProductByUserId(int userId);
	

}
