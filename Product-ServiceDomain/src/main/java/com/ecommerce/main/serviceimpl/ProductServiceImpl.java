package com.ecommerce.main.serviceimpl;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.ecommerce.main.dto.ProductDto;
import com.ecommerce.main.enums.Features;
import com.ecommerce.main.exceptions.ProductException;
import com.ecommerce.main.exceptions.ProductNotSavedException;
import com.ecommerce.main.exceptions.ValidationException;
import com.ecommerce.main.model.Product;
import com.ecommerce.main.model.ProductFeatures;
import com.ecommerce.main.model.ProductImage;
import com.ecommerce.main.model.ProductReview;
import com.ecommerce.main.repository.ProductRepository;
import com.ecommerce.main.servicei.ProductService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class ProductServiceImpl implements ProductService {

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private Validator validator;

	@Autowired
	private ObjectMapper mapper;

	private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);

	@Override
	public ProductDto saveProduct(String productJson, List<MultipartFile> files) {
		log.info("Received request to save product: {}", productJson);

		Product product;
		try {
			product = mapper.readValue(productJson, Product.class);
		} catch (JsonProcessingException e) {
			log.error("Error parsing JSON: {}", e.getMessage(), e);
			throw new ProductException("Invalid JSON format: " + e.getMessage());
		}
		validateProduct(product);

		if (product == null) {
			log.error("Product is empty or invalid");
			throw new ProductException("At least one product must be provided.");
		}

		try {
			List<ProductImage> productImages = new ArrayList<>();
			for (MultipartFile file : files) {
				if (file == null || file.isEmpty()) {
					throw new ProductException("At least one product image file must be provided.");
				}
				if (!file.isEmpty()) {
					ProductImage productImage = new ProductImage();
					productImage.setImageData(file.getBytes());
					productImages.add(productImage);
				}
			}

			product.setProductImages(productImages);

			if (product.getProductFeatures() != null && !product.getProductFeatures().isEmpty()) {
				for (int i = 0; i < product.getProductFeatures().size(); i++) {
					ProductFeatures feature = product.getProductFeatures().get(i);
					if (feature.getFeature() == null) {
						feature.setFeature(Features.RAM);
					}
				}
			}
		} catch (IOException e) {
			log.error("Error processing the file: {}", e.getMessage(), e);
			throw new ProductException("Failed to process product images.");
		}

		Product savedProducts = productRepository.save(product);
		log.info("Products saved successfully to Database: {}", savedProducts);

		return new ProductDto(product);
	}

	private void validateProduct(Product product) {
		Set<ConstraintViolation<Product>> violations = validator.validate(product);
		if (!violations.isEmpty()) {
			Map<String, String> errors = new HashMap<>();
			for (ConstraintViolation<Product> violation : violations) {
				errors.put(violation.getPropertyPath().toString(), violation.getMessage());
			}
			log.error("Product validation failed: {}", errors);
			throw new ValidationException(errors);
		}
	}

	public Iterable<Product> getAll() {

		return productRepository.findAll();
	}

	@Override
	public Product getById(int productId) {
		return productRepository.getById(productId);
	}

	@Override
	public void patchProduct(boolean isAvailable, int productId) {
		Product p = productRepository.getById(productId);
		if (p == null) {
			throw new ProductNotSavedException("Id Not Found For Partial Update...!");
		}
		productRepository.patchUpdate(isAvailable, productId);
		log.info("Partial Update Successfull To Database...{}");

	}

	@Override
	public void deleteById(int productId) {
		productRepository.deleteById(productId);
	}

	@Override
	public void quantityAvailable(int quantity, int productId) {
		Product p = productRepository.getById(productId);
		if (p == null) {
			throw new ProductNotSavedException("Id Not Found For Partial Update...!");
		}

		p.setQuantityAvailable(quantity);
		productRepository.save(p);
		log.info("Partial Update Successfull To Database...{}");

	}

	@Override
	public Product getOneProductByName(String productName) {

		return productRepository.getByName(productName);
	}

	public Iterable<Product> getByName(String productName) {
		return productRepository.findByProductName(productName);
	}

	@Override
	public void updateProduct(int productId, ProductDto productDto, List<MultipartFile> images) throws IOException {
		Product product = productRepository.findById(productId).orElseThrow();

		// Update product reviews
		product.getProductReviews().clear();
		productDto.getProductReviews().forEach(reviewDto -> {
			ProductReview review = new ProductReview();
			review.setReviewbyCustomername(reviewDto.getReviewbyCustomername());
			review.setReviewMessage(reviewDto.getReviewMessage());
			review.setStarRating(reviewDto.getStarRating());
			product.getProductReviews().add(review);
		});

		// Update product features
		product.getProductFeatures().clear();
		productDto.getProductFeatures().forEach(featureDto -> {
			ProductFeatures feature = new ProductFeatures();
			feature.setFeature(featureDto.getFeature());
			feature.setFeatureDescription(featureDto.getFeatureDescription());
			product.getProductFeatures().add(feature);
		});

		// Update product images
		product.getProductImages().clear();
		if (images != null) {
			images.forEach(image -> {
				try {
					ProductImage productImage = new ProductImage();
					productImage.setImageData(image.getBytes());
					product.getProductImages().add(productImage);
				} catch (IOException e) {

				}
			});
		}
		productRepository.save(product);
	}
}
