package com.ecommerce.main.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ecommerce.main.model.Product;

import jakarta.transaction.Transactional;

@Repository
public interface ProductRepository extends CrudRepository<Product, Integer> {

    @Query(
        value = "SELECT * FROM products WHERE user_id = :userId",
        nativeQuery = true
    )
    List<Product> getProductsByUserId(
        @Param("userId") int userId
    );

    @Modifying
    @Transactional
    @Query(
        value = "UPDATE products SET user_id = NULL " +
                "WHERE product_id = :productId " +
                "AND user_id = :userId",
        nativeQuery = true
    )
    int removeProductFromCart(
        @Param("userId") int userId,
        @Param("productId") int productId
    );
}