package com.ecommerce.main.dto;

import java.util.List;

import com.ecommerce.main.enums.Features;
import com.ecommerce.main.model.Product;
import com.ecommerce.main.model.ProductFeatures;
import com.ecommerce.main.model.ProductImage;
import com.ecommerce.main.model.ProductReview;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {
	
    private int productId;
    private String productName;
    private String description;
    private String brand;
    private String category;
    private double price;
    private int quantityAvailable;
    private String supplierName;
    private boolean available;  
    private List<ProductImage> productImages;
    private List<ProductFeatures> productFeatures;
    private List<ProductReview> productReviews;

    public ProductDto(Product product) {
        this.productId = product.getProductId();
        this.productName = product.getProductName();
        this.description = product.getDescription();
        this.brand = product.getBrand();
        this.category = product.getCategory();
        this.price = product.getPrice();
        this.quantityAvailable = product.getQuantityAvailable();
        this.supplierName = product.getSupplierName();
        this.available=product.isAvailable();
        this.productImages=product.getProductImages();
        this.productFeatures = product.getProductFeatures();
        this.productReviews = product.getProductReviews();
        
    }
}
