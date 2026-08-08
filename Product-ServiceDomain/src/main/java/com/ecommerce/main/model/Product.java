package com.ecommerce.main.model;

import java.util.List;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "products")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int productId;

	@NotBlank(message = "Product name cannot be empty")
	@Size(max = 100, message = "Product name must not exceed 100 characters")
	private String productName;

	@NotBlank(message = "Description cannot be empty")
	@Size(max = 80, message = "Description must not exceed 50 characters")
	private String description;

	@NotBlank(message = "Brand cannot be empty")
	@Size(max = 15, message = "Brand name must not exceed 15 characters")
	private String brand;
	@NotBlank(message = "Category cannot be empty")
	@Size(max = 50, message = "Category must not exceed 50 characters")
	private String category;

	@Positive(message = "Price must be greater than zero")
	private double price;

	@PositiveOrZero(message = "Quantity available cannot be negative")
	private int quantityAvailable;

	@NotBlank(message = "Supplier name cannot be empty")
	@Size(max = 100, message = "Supplier name must not exceed 100 characters")
	private String supplierName;

	@NotBlank(message = "Supplier contact cannot be empty")
	@Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid supplier contact number")
	private String supplierContact;

	@PositiveOrZero(message = "Warranty period cannot be negative")
	private int warrantyPeriod;

	private boolean available;

	@OneToMany(cascade = CascadeType.ALL,orphanRemoval = true)
	@JoinColumn(name = "product_id")
	private List<ProductImage> productImages;

	@OneToMany(cascade = CascadeType.ALL,orphanRemoval = true)
	@JoinColumn(name = "product_id")
	private List<ProductFeatures> productFeatures;

	@OneToMany(cascade = CascadeType.ALL,orphanRemoval = true)
	@JoinColumn(name = "product_id")
	private List<ProductReview> productReviews;

}
