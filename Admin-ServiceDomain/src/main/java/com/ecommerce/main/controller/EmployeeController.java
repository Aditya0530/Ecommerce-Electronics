package com.ecommerce.main.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.ecommerce.main.dto.EmployeeDto;
import com.ecommerce.main.dto.MailDetailsDto;
import com.ecommerce.main.model.Employee;
import com.ecommerce.main.service.EmailService;
import com.ecommerce.main.service.EmployeeService;

@CrossOrigin("*")
@RestController
@RequestMapping("/employee")
public class EmployeeController {

	@Autowired
	private EmployeeService employeeservice;

	
	@GetMapping("/login/{username}/{password}")
	public ResponseEntity<Object> login(@PathVariable String username, @PathVariable String password) {
	    Object response = employeeservice.loginEmployee(username, password);
	    return new ResponseEntity<>(response, HttpStatus.OK);
	}

	
	@PostMapping("/postEmployee")
	public ResponseEntity<EmployeeDto> saveAdmin(@RequestPart("employeeData") String employee,@RequestPart("imageData") MultipartFile multipartFile){
		EmployeeDto adminDto=employeeservice.saveEmployee(employee, multipartFile);
		return new ResponseEntity<>(adminDto,HttpStatus.CREATED);
	   
	}
	
	@PutMapping("/updateAll/{empId}")
	public ResponseEntity<String> updateProduct(@PathVariable int empId,
			@RequestPart("employee") String employeeJson, @RequestPart("imageData") MultipartFile multipartFile) {
		employeeservice.updateEmployee(empId, employeeJson, multipartFile);
		return new ResponseEntity<String>("Update Successful", HttpStatus.OK);
	}
	@GetMapping("/getEmployees")
	public ResponseEntity<Iterable<Employee>> getAllEmployee() {
		Iterable<Employee> employee=employeeservice.getEmployees();
		return new ResponseEntity<>(employee, HttpStatus.OK);
	}
	
	@DeleteMapping("/deleteEmployee/{empId}")
	public ResponseEntity<String> deleteEmployee(@PathVariable int empId) {
	  employeeservice.deleteEmployeeCode(empId);
	  return new ResponseEntity<>("Employee deleted successfully", HttpStatus.OK);
	}
}
