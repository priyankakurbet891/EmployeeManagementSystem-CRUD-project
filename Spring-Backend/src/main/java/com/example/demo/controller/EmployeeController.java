package com.example.demo.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Employee;
import com.example.demo.repository.EmployeeRepository;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/v1/")
public class EmployeeController {
	
	@Autowired
	private EmployeeRepository employee_repo;
	
	// get all employee
	@GetMapping("/employees")
	public List<Employee> getAllEmployees(){
		return employee_repo.findAll();
	}
	
	// create employees
	@PostMapping("/employees")
	public Employee createEmployee(@RequestBody Employee employee) {
		return employee_repo.save(employee);
	}
	
	// get employee by their id
	@GetMapping("/employees/{id}")
	public ResponseEntity<Employee> getEmployeeByID(@PathVariable Long id) {
		Employee employee = employee_repo.findById(id).
				orElseThrow(()-> new ResourceNotFoundException("Employee resource not found: " + id));
		return ResponseEntity.ok(employee);
	}
	
	// update employee by their id
	@PutMapping("employees/{id}")
	public ResponseEntity<Employee> updateEmployee(@PathVariable Long id, @RequestBody Employee employee_details) {
		Employee employee = employee_repo.findById(id).
				orElseThrow(()-> new ResourceNotFoundException("Employee resource not found: " + id));
		employee.setFirst_name(employee_details.getFirst_name());
		employee.setLast_name(employee_details.getLast_name());
		employee.setEmail_id(employee_details.getEmail_id());
		employee.setDepartment(employee_details.getDepartment());
		employee.setTitle(employee_details.getTitle());
		
		Employee updated_employee = employee_repo.save(employee);
		return ResponseEntity.ok(updated_employee);
		
	}
	
	// delete employee by their id
	@DeleteMapping("employees/{id}")
	public ResponseEntity<Map<String, Boolean>> deleteEmployee(@PathVariable Long id){
		Employee employee = employee_repo.findById(id).
				orElseThrow(()-> new ResourceNotFoundException("Employee resource not found: " + id));
		employee_repo.delete(employee);
		Map<String, Boolean> response = new HashMap<>();
		response.put("deleted", true);
		return ResponseEntity.ok(response);
	}
}
