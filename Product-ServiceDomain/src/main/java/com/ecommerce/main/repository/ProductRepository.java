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

	@Query("SELECT p FROM Product p WHERE p.productId = :productId")
	Product getById(@Param("productId") int productId);

	@Modifying
	@Transactional
	@Query("update Product p set p.available= :available where p.productId= :productId")
	void patchUpdate(@Param("available") boolean isAvailable, @Param("productId") int productId);

	@Modifying
	@Transactional
	@Query("update Product p set p.quantityAvailable= :quantityAvailable where p.productId= :productId")
	void quantityUpdate(@Param("quantityAvailable") int quantityAvailable, @Param("productId") int productId);

	List<Product> findAllByProductName(String productName);

	@Query("Select p from Product p where p.productName = :productName")
	Product getByName(@Param("productName") String productName);

	Iterable<Product> findByProductName(String productName);

//	@Modifying
//	@Transactional
//	@Query("update ProductFeatures f set f.feature=: RAM ,f.featureDescription=: fecdes  Where f.featuresId =: fid")
//	

	@Modifying
	@Transactional
	@Query("update ProductReview p set p.reviewbyCustomername= :reviewbyCustomername , p.starRating= :starRating,p.reviewMessage= :reviewMessage where p.reviewId= :reviewId")
	void updatereview(@Param("reviewbyCustomername") String reviewbyCustomername, @Param("starRating") int starRating,
			@Param("reviewMessage") String reviewMessage, @Param("reviewId") int reviewId);
}
