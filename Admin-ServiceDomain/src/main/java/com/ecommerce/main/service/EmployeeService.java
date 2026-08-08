package com.ecommerce.main.service;
import org.springframework.web.multipart.MultipartFile;
import com.ecommerce.main.dto.EmployeeDto;
import com.ecommerce.main.dto.MailDetailsDto;
import com.ecommerce.main.model.Employee;


public interface EmployeeService {

	public EmployeeDto saveEmployee(String employee,MultipartFile multipartFile);

	public void updateEmployee(int empId, String employeeJson, MultipartFile multipartFile);

	public Object loginEmployee(String username, String password);
	
    public Iterable<Employee> getEmployees();

	public void deleteEmployeeCode(int empId);


}
