package com.ecommerce.main.serviceimpl;

import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.ecommerce.main.dto.UserDto;
import com.ecommerce.main.enums.StatusOrder;
import com.ecommerce.main.exceptionhandler.DuplicateProductFoundException;
import com.ecommerce.main.exceptionhandler.EmailSendingException;
import com.ecommerce.main.exceptionhandler.InvalidCredentialsException;
import com.ecommerce.main.exceptionhandler.UserIdNotFoundException;
import com.ecommerce.main.model.Order;
import com.ecommerce.main.model.Product;
import com.ecommerce.main.model.User;
import com.ecommerce.main.repository.OrderRepository;
import com.ecommerce.main.repository.ProductRepository;
import com.ecommerce.main.repository.UserRepository;
import com.ecommerce.main.servicei.EmailService;
import com.ecommerce.main.servicei.PaymentService;
import com.ecommerce.main.servicei.UserService;

import jakarta.transaction.Transactional;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	UserRepository userRepository;

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	OrderRepository orderRepository;

	@Autowired
	ProductRepository productRepository;

	@Autowired
	private EmailService emailService;

	@Autowired
	private PaymentService paymentService;

	private final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

	@Override
	public UserDto saveUser(User user) {
		LOGGER.info("Saving user: {}", user.getUsername());
		user.setRole("User");
		userRepository.save(user);
		LOGGER.info("User saved successfully: {}", user.getUsername());
		return new UserDto(user);
	}

	@Override
	public Object loginAdmin(String username, String password) {
		if ("Admin".equals(username) && "Admin".equals(password)) {
			return userRepository.findAll();

		} else {
			User user = userRepository.findAllByUsernameAndPassword(username, password);
			if (user == null) {
				throw new InvalidCredentialsException("Incorrect User Details");
			}
			return user;
		}

	}

	@Override
	public User loginUser(String username, String password) {
		User user = userRepository.findAllByUsernameAndPassword(username, password);

		if (user == null) {
			throw new InvalidCredentialsException("Username And Password Incorrect");
		} else {
			String userEmail = user.getEmail();
			try {
				emailService.sendEmail(userEmail, "Login Successful", "You have successfully logged in.");
			} catch (EmailSendingException e) {
				LOGGER.error("Failed to send email", e);
			}
			return user;
		}
	}

	@Override
	public Iterable<Product> getAll() {
		String url = "http://PRODUCT-SERVICEDOMAIN/product/getAll";
		Iterable<Product> response = restTemplate.getForObject(url, Iterable.class);
		return response;
	}

	@Override
	public User getUser(int userId) {
		User user = userRepository.findAllByUserId(userId);
		return user;
	}

	@Override
	public Iterable<Product> getByName(String productName) {
		String url = "http://PRODUCT-SERVICEDOMAIN/product/getByName/" + productName;
		Iterable<Product> product = restTemplate.getForObject(url, Iterable.class);
		return product;
	}

	@Override
	@Transactional
	public void addToCart(int userId, int productId) {

		LOGGER.info("Adding product {} to cart for user {}", productId, userId);

		// 1. Get product from Product-Service
		String url = "http://PRODUCT-SERVICEDOMAIN/product/getById/" + productId;

		Map<String, Object> productFromProductModule = restTemplate.getForObject(url, Map.class);

		if (productFromProductModule == null) {
			throw new RuntimeException("Product not found");
		}
		LOGGER.info("PRODUCT SERVICE RESPONSE: {}", productFromProductModule);

		LOGGER.info("PRODUCT IMAGES OBJECT: {}", productFromProductModule.get("productImages"));

		// 2. Get user
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new UserIdNotFoundException("Please input correct user id"));

		// 3. Check duplicate product
		List<Product> existingProducts = productRepository.getProductsByUserId(userId);

		for (Product product : existingProducts) {

			if (product.getProductId() == productId) {

				throw new DuplicateProductFoundException("Product id already present in your cart");
			}
		}

		// 4. Check stock
		Integer quantityAvailable = (Integer) productFromProductModule.get("quantityAvailable");

		if (quantityAvailable == null || quantityAvailable <= 0) {
			throw new RuntimeException("Product is out of stock");
		}

		// 5. Decrease quantity
		restTemplate.put(
				"http://PRODUCT-SERVICEDOMAIN/product/updateQuantity/" + (quantityAvailable - 1) + "/" + productId,
				null);

		byte[] imageData = null;

		Object productImagesObject = productFromProductModule.get("productImages");

		if (productImagesObject instanceof List<?>) {

			List<?> productImages = (List<?>) productImagesObject;

			if (!productImages.isEmpty()) {

				Object firstImageObject = productImages.get(0);

				if (firstImageObject instanceof Map<?, ?>) {

					Map<?, ?> imageMap = (Map<?, ?>) firstImageObject;

					Object imageDataObject = imageMap.get("imageData");

					if (imageDataObject != null) {

						String imageDataString = imageDataObject.toString();

						imageData = Base64.getDecoder().decode(imageDataString);

						LOGGER.info("Image successfully decoded. Size: {} bytes", imageData.length);
					}
				}
			}
		}

		if (imageData == null) {

			LOGGER.warn("No image found for product {}", productId);
		}

		// 7. Create cart Product
		Product productToUpdate = new Product();

		productToUpdate.setProductId(((Number) productFromProductModule.get("productId")).intValue());

		productToUpdate.setProductName((String) productFromProductModule.get("productName"));

		productToUpdate.setDescription((String) productFromProductModule.get("description"));

		productToUpdate.setBrand((String) productFromProductModule.get("brand"));

		productToUpdate.setPrice(((Number) productFromProductModule.get("price")).doubleValue());

		productToUpdate.setImage(imageData);

		// 8. Add product to user's cart
		user.getProduct().add(productToUpdate);

		userRepository.save(user);

		LOGGER.info("Product {} successfully added to cart for user {}", productId, userId);
	}

//	@Override
//	@Transactional
//	public String placeOrder(int userId, int productId, Order order) {
//		LOGGER.info("Purchase request by user {} for product {}", userId, productId);
//
//		String productUrl = "http://PRODUCT-SERVICEDOMAIN/product/getById/" + productId;
//		Product productFromService = restTemplate.getForObject(productUrl, Product.class);
//
//		User user = userRepository.findById(userId)
//				.orElseThrow(() -> new UserIdNotFoundException("Please input correct user ID"));
//
//		Product managedProduct = productRepository.findById(productId)
//				.orElseThrow(() -> new RuntimeException("Product not found in database"));
//
//		List<Order> existingOrder = getByUserIdProductId(userId, productId);
//		if (!existingOrder.isEmpty()) {
//			LOGGER.warn("User {} already ordered product {}", userId, productId);
//			Order currentOrder = existingOrder.get(0);
//			currentOrder.setTotalAmount(managedProduct.getPrice());
//			orderRepository.save(currentOrder);
//			return "You Already Ordered This Product";
//		} else {
//
//			double totalAmount = managedProduct.getPrice() * order.getQuantity();
//			double sendAmount = order.getRequestAmount();
//			if (sendAmount == totalAmount) {
//				String paymentMessage = paymentService.processPayment(userId, productId, totalAmount);
//
//				order.setOrderStatus(StatusOrder.CONFIRMED);
//				order.setTotalAmount(totalAmount);
//				order.setDeliverycharges(100);
//				order.setProduct(managedProduct);
//				user.getProduct().add(managedProduct);
//				user.getOrder().add(order);
//
//				userRepository.save(user);
//				LOGGER.info("Order placed successfully by user {} for product {}", userId, productId);
//
//				emailService.sendOrderConfirmationEmail(user, order);
//				LOGGER.info("Order confirmation email sent to user {}", userId);
//
//				return paymentMessage + "Order Placed Successfully";
//			} else {
//				order.setOrderStatus(StatusOrder.PENDING);
//				LOGGER.warn("Amount mismatch: Expected ₹{}, Received ₹{} from user {}", totalAmount, sendAmount,
//						userId);
//				return "Payment amount mismatch. Expected: ₹" + totalAmount + ", but received: ₹" + sendAmount
//						+ ". Order not placed.";
//			}
//		}
//	}

	@Override
	@Transactional
	public String placeOrder(int userId, int productId, Order order) {
		LOGGER.info("Purchase request by user {} for product {}", userId, productId);

		String productUrl = "http://PRODUCT-SERVICEDOMAIN/product/getById/" + productId;
		Product productFromService = restTemplate.getForObject(productUrl, Product.class);

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new UserIdNotFoundException("Please input correct user ID"));
		if (productFromService == null) {
			throw new RuntimeException("Product not found in Product Service");
		}

		List<Order> existingOrder = getByUserIdProductId(userId, productId);
		if (!existingOrder.isEmpty()) {
			LOGGER.warn("User {} already ordered product {}", userId, productId);
			Order currentOrder = existingOrder.get(0);
			currentOrder.setTotalAmount(productFromService.getPrice());
			orderRepository.save(currentOrder);
			return "You Already Ordered This Product";
		} else {

			double totalAmount = productFromService.getPrice() * order.getQuantity();
			double sendAmount = order.getRequestAmount();
			if (sendAmount == totalAmount) {
				String paymentMessage = paymentService.processPayment(userId, productId, totalAmount);

				order.setOrderStatus(StatusOrder.CONFIRMED);
				order.setTotalAmount(totalAmount);
				order.setDeliverycharges(100);
				// GET ADDRESS FROM LOGGED-IN USER
				if (order.getAddress() == null) {
					throw new RuntimeException("Please add address before placing an order");
				}
				order.setProduct(productFromService);
				user.getOrder().add(order);
				userRepository.save(user);
				LOGGER.info("Order placed successfully by user {} for product {}", userId, productId);

				emailService.sendOrderConfirmationEmail(user, order);
				LOGGER.info("Order confirmation email sent to user {}", userId);

				return paymentMessage + "Order Placed Successfully";
			} else {
				order.setOrderStatus(StatusOrder.PENDING);
				LOGGER.warn("Amount mismatch: Expected ₹{}, Received ₹{} from user {}", totalAmount, sendAmount,
						userId);
				return "Payment amount mismatch. Expected: ₹" + totalAmount + ", but received: ₹" + sendAmount
						+ ". Order not placed.";
			}
		}
	}

	@Override
	public List<Order> getByUserIdProductId(int userId, int productId) {

		return orderRepository.findByUserIdAndProductId(userId, productId);
	}

	@Override
	public void orderStatus(int orderId, StatusOrder orderStatus) {

		orderRepository.updateOrderStatus(orderId, orderStatus);
	}

	@Override
	public Map<String, Object> viewCart(int userId) {

		List<Product> productResponse = getProductByUserId(userId);
		List<Order> orderResponse = getOrderByUserId(userId);

		List<Map<String, Object>> listProduct = new ArrayList<>();
		Map<String, Object> responseMap = new LinkedHashMap<>();

		double totalAmount = 0.0;
		double deliveryCharges = 0.0;
		double grandTotal = 0.0;

		for (Order order : orderResponse) {
			Product product = order.getProduct();
			Map<String, Object> orderedProduct = new LinkedHashMap<>();

			orderedProduct.put("productId", product.getProductId());
			orderedProduct.put("productName", product.getProductName());
			orderedProduct.put("productImg", product.getImage());
			orderedProduct.put("productBrand", product.getBrand());
			orderedProduct.put("productQuantity", order.getQuantity());
			orderedProduct.put("deliveryCharges", order.getDeliverycharges());
			orderedProduct.put("productPrice", product.getPrice());
			orderedProduct.put("orderStatus", order.getOrderStatus());
			orderedProduct.put("requestAmount", order.getRequestAmount());
			orderedProduct.put("orderId", order.getOrderId());
			totalAmount += product.getPrice() * order.getQuantity();
			deliveryCharges += order.getDeliverycharges();

			listProduct.add(orderedProduct);
		}

		grandTotal = totalAmount + deliveryCharges;

		// Sum all product prices in cartSummary, assuming you want sum of all products
		// in the cart
		double totalPrice = 0.0;
		for (Product product : productResponse) {
			totalPrice += product.getPrice();
		}

		responseMap.put("orderSummary", Map.of("grandTotal", grandTotal, "orderedProduct", listProduct));
		responseMap.put("cartSummary", Map.of("totalPrice", totalPrice, "cartProduct", productResponse));

		return responseMap;
	}

//	@Override
//	public void removeFromCart(int userId, int productId) {
//
//		User user = userRepository.findById(userId)
//				.orElseThrow(() -> new UserIdNotFoundException("Please input correct user id"));
//		user.getProduct().removeIf(product -> product.getProductId() == productId);
//		productRepository.deleteById(productId);
//
//	}
	@Override
	@Transactional
	public void removeFromCart(int userId, int productId) {

		userRepository.findById(userId).orElseThrow(() -> new UserIdNotFoundException("Please input correct user id"));

		int removed = productRepository.removeProductFromCart(userId, productId);

		if (removed == 0) {
			throw new RuntimeException("Product not found in user's cart");
		}

		LOGGER.info("Product {} removed from cart for user {}", productId, userId);
	}

	@Override
	public List<Order> getOrderByUserId(int userId) {

		return orderRepository.getOrdersByUserId(userId);
	}

	@Override
	public List<Product> getProductByUserId(int userId) {

		return productRepository.getProductsByUserId(userId);
	}

	@Override
	public void deleteUser(int userId) {
		// TODO Auto-generated method stub
		userRepository.deleteUser(userId);

	}

	@Override
	public List<User> getAllUsers() {
		
		return (List<User>) userRepository.findAll();
	}

}
