package com.ecommerce.main.repository;

import java.sql.Date;
import java.sql.Time;
import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import com.ecommerce.main.enums.InventoryRole;
import com.ecommerce.main.model.Employee;

import jakarta.transaction.Transactional;


public interface EmployeeRepository extends CrudRepository<Employee, Integer>{
	
	
    public Optional<Employee> findByUsername(String username);


	@Modifying
	@Transactional
    @Query("UPDATE Employee e SET e.name = :name, e.username = :username, e.email = :email, e.password = :password, "
    		+ "e.inventoryRole = :inventoryRole, e.role = :role, e.phoneNumber = :phoneNumber, e.createDate=:date, e.createTime=:time, e.imageFile = :bytes WHERE e.empId = :empId")
	public void updateData(@Param("name")String name, @Param("username")String username, @Param("email")String email, 
			@Param("password")String password, @Param("inventoryRole")InventoryRole inventoryRole, @Param("role")String role,
			@Param("phoneNumber")String phoneNumber, @Param("date")Date date, @Param("time")Time time, @Param("bytes")byte[] bytes, @Param("empId")int empId);

}
