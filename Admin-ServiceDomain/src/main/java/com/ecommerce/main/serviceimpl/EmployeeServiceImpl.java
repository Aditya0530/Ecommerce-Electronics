package com.ecommerce.main.serviceimpl;

import java.io.IOException;
import java.sql.Date;
import java.sql.Time;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ecommerce.main.dto.EmployeeDto;
import com.ecommerce.main.dto.MailDetailsDto;
import com.ecommerce.main.enums.InventoryRole;
import com.ecommerce.main.exceptions.ImageNotUpdateException;
import com.ecommerce.main.exceptions.InvalidCredentialsException;
import com.ecommerce.main.exceptions.EmployeeNotFoundException;
import com.ecommerce.main.exceptions.FileNotSavedException;
import com.ecommerce.main.exceptions.ValidationException;
import com.ecommerce.main.model.Employee;
import com.ecommerce.main.repository.EmployeeRepository;
import com.ecommerce.main.service.EmailService;
import com.ecommerce.main.service.EmployeeService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {

	@Autowired
	private EmployeeRepository employeeRepository;
	
	@Autowired
	private Validator validator;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private EmailService emailservice;
	
	private static final Logger log = LoggerFactory.getLogger(EmployeeServiceImpl.class);
	
	 @Override
	 public Object loginEmployee(String username, String password) {
		    
		    // If "admin" logs in, return all employees
		    if ("admin".equalsIgnoreCase(username) && "admin".equalsIgnoreCase(password)) {
		    	log.info("Admin login successful, fetching all employees.");
		        return employeeRepository.findAll();  // Return list of all employees
		    }

		    // Find employee by username
		    Employee employee = employeeRepository.findByUsername(username)
		            .orElseThrow(() -> new InvalidCredentialsException("Invalid username or password"));

		    // Check if password is correct
		    if (!employee.getPassword().equals(password)) {
		        throw new InvalidCredentialsException("Invalid username or password");
		    }

		    return new EmployeeDto(employee);  // Return only the logged-in employee's details
		}

	
	
	@Override
	public EmployeeDto saveEmployee(String json,MultipartFile multipartFile) {
		
		Employee employee=new Employee();
		
		try {
			employee =objectMapper.readValue(json, Employee.class);
		} catch (JsonMappingException e) {
			
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			
			e.printStackTrace();
		}
		validateEmployee(employee);
		
		if ("admin".equalsIgnoreCase(employee.getRole())) {
		    employee.setInventoryRole(InventoryRole.ADMIN);  
		} else if ("delivery".equalsIgnoreCase(employee.getRole())) {
			employee.setInventoryRole(InventoryRole.ORDERDELIVERY_HEAD);    
		} else if ("employee".equalsIgnoreCase(employee.getRole())) {
			employee.setInventoryRole(InventoryRole.HEAD);
		} else {
		    throw new IllegalArgumentException("Invalid role specified");  
		}
		Random randomNo=new Random();
		String[] namePart=employee.getName().split(" ");
		String firstName = namePart[0]; 
		String generateduserName=firstName.toLowerCase()+randomNo.nextInt(1000);
		String passWord=firstName.toLowerCase()+"@"+randomNo.nextInt(1000);	
		
		if(multipartFile.isEmpty()) {
			log.error("File upload failed: file is empty.");
			throw new FileNotSavedException("File Not Be Empty");
		}
		
		if(multipartFile!=null) {
			try {
				employee.setImageFile(multipartFile.getBytes());
			} catch (IOException e) {
				log.error("Error processing uploaded file: {}", e.getMessage(), e);
				e.printStackTrace();
			}
		}
		employee.setUsername(generateduserName);
		employee.setPassword(passWord);
		Employee savedAdmin = employeeRepository.save(employee);
		log.info("Employees Saved To Database Successfully {}"+savedAdmin);
		emailservice.sendEmailMessage(employee);
		log.info("Welcome email sent to {}", employee.getEmail());
		return new EmployeeDto(savedAdmin);
	}

	@Override
	public void updateEmployee(int empId, String employeeJson, MultipartFile multipartFile) {

          Employee employee=null;
          
          try {
  			employee =objectMapper.readValue(employeeJson, Employee.class);
  		}
  		catch(Exception e) {
  				e.printStackTrace();
  				System.out.println(e.getMessage());
  			}
          if(multipartFile.isEmpty()) {
        	  throw new ImageNotUpdateException("Image can't be Empty..");
  		  }
          else {
		try {
			Date date=new Date(System.currentTimeMillis());
			Time time=new Time(System.currentTimeMillis());
		employeeRepository.updateData(employee.getName(),employee.getUsername(),employee.getEmail(),employee.getPassword(),
				employee.getInventoryRole(),employee.getRole(),employee.getPhoneNumber(),date,time,multipartFile.getBytes(),empId);

		}
		catch (IOException e) {
			e.printStackTrace();
           System.out.println(e.getMessage());
		}	
          }		
	}

	
	private void validateEmployee(Employee employee) {
		Set<ConstraintViolation<Employee>> violations = validator.validate(employee);
		if (!violations.isEmpty()) {
			Map<String, String> errors = new HashMap<>();
			for (ConstraintViolation<Employee> violation : violations) {
				errors.put(violation.getPropertyPath().toString(), violation.getMessage());
			}
			log.error("Product validation failed: {}", errors);
			throw new ValidationException(errors);
		}
	}



	@Override
	public Iterable<Employee> getEmployees() {
		// TODO Auto-generated method stub
		return employeeRepository.findAll();
	}



	@Override
	public void deleteEmployeeCode(int empId) {
		 try {
			    if (employeeRepository.existsById(empId)) {
			      employeeRepository.deleteById(empId);
			      log.info("Employee with ID {} deleted successfully", empId);
			    } else {
			      log.error("Employee with ID {} not found", empId);
			      throw new EmployeeNotFoundException("Employee not found with ID " + empId);
			    }
			  } catch (Exception e) {
			    log.error("Error deleting employee with ID {}", empId, e);
			    throw new RuntimeException("Error deleting employee", e);
			  }
	}



	




	
}
