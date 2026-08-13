package com.ecommerce.main.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.ecommerce.main.model.Product;

public interface ProductRepository extends CrudRepository<Product, Integer> {

	@Query(value = "SELECT * FROM products WHERE user_id = :userId", nativeQuery = true)
	List<Product> getProductsByUserId(@Param("userId") int userId);

}
