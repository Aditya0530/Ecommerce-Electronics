package com.ecommerce.main.model;

import com.ecommerce.main.enums.Features;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product_features")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductFeatures {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int featuresId;

	@Enumerated(EnumType.STRING)
	private Features feature; // Example: RAM, STORAGE, etc.

	@NotBlank(message = "Feature description cannot be empty")
	private String featureDescription;

}
