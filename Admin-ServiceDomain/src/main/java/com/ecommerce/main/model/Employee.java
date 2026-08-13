package com.ecommerce.main.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.ecommerce.main.enums.InventoryRole;
import java.sql.Date;
import java.sql.Time;

@Entity
@Table(name = "employee")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Employee {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int empId;

	@NotBlank(message = "Name cannot be blank")
	@Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters")
	@NotNull(message = "Name Cannot Be Null")
	private String name;

	private String username;

	@NotBlank(message = "Email cannot be blank")
	@Email(message = "Invalid email format")
	private String email;

	private String password;

	@Enumerated(EnumType.STRING)
	private InventoryRole inventoryRole;

	@NotBlank(message = "Role cannot be blank")
	private String role; // "admin", "employee", etc.

	@NotBlank(message = "Phone number cannot be blank")
	@Pattern(regexp = "\\d{10}", message = "Phone number must be exactly 10 digits")
	private String phoneNumber;

	private Date createDate; // Stores the current date (yyyy-MM-dd)
	private Time createTime; // Stores the current time (HH:mm:ss)

	@Lob
	@Column(length = 999999999)
	private byte[] imageFile;

	@PrePersist
	public void onCreate() {
	    this.createDate = new Date(System.currentTimeMillis());
	    this.createTime = new Time(System.currentTimeMillis());
	}

	@PreUpdate
	public void onUpdate() {
	    this.createDate = new Date(System.currentTimeMillis());
	    this.createTime = new Time(System.currentTimeMillis());
	}

}
