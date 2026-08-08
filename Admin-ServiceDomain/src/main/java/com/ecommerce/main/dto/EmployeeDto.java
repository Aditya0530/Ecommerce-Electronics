package com.ecommerce.main.dto;

import java.sql.Date;
import java.sql.Time;

import com.ecommerce.main.enums.InventoryRole;
import com.ecommerce.main.model.Employee;

import jakarta.persistence.PreUpdate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDto {

	private int empId;
	private String name;
	private String username;
	private InventoryRole inventoryRole;
	private String role;
	private String email;
	private String password;
	private String phoneNumber;
	private byte[] imageFile;
	private Date createDate;
	private Time createTime;

	public EmployeeDto(Employee employee) {
		this.empId = employee.getEmpId();
		this.name = employee.getName();
		this.username = employee.getUsername();
		this.inventoryRole = employee.getInventoryRole();
		this.role = employee.getRole();
		this.email = employee.getEmail();
		this.password = employee.getPassword();
		this.phoneNumber = employee.getPhoneNumber();
		this.imageFile = employee.getImageFile();
		this.createDate = employee.getCreateDate();
		this.createTime = employee.getCreateTime();

	}

}
