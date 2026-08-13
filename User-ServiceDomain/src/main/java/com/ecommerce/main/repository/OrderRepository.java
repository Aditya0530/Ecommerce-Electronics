package com.ecommerce.main.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.ecommerce.main.enums.StatusOrder;
import com.ecommerce.main.model.Order;
import com.ecommerce.main.model.Product;

import jakarta.transaction.Transactional;

public interface OrderRepository extends CrudRepository<Order, Integer>{
	
	@Query(value = "SELECT * FROM orders o WHERE o.product_id = :productId AND o.user_id = :userId", nativeQuery = true)
	List<Order> findByUserIdAndProductId(@Param("userId") int userId, @Param("productId") int productId);

	@Modifying
	@Transactional
	@Query("update Order o Set o.orderStatus = :orderStatus where o.orderId = :orderId")
	public void updateOrderStatus(@Param("orderId") int orderId, @Param("orderStatus")StatusOrder orderStatus);

	@Query(value = "SELECT * FROM orders WHERE user_id = :userId", nativeQuery = true)
	List<Order> getOrdersByUserId(@Param("userId") int userId);
}
