# EmployeeManagementSystem-CRUD

This report will document the approach to create a full stack web application for the Employee Management System (EMS) using Spring Boot and Angular which allows end user to perform CRUD operations.

## End-user expectation
### 1) Read all employees
https://user-images.githubusercontent.com/78957509/171724772-c35365ce-6018-4fcf-8978-b3b06a7e6896.mp4
### 2) Create a new employee
https://user-images.githubusercontent.com/78957509/171724862-5f6a5e64-0762-49ef-b9de-124ae50c113d.mp4
### 3) Update an existing employee
https://user-images.githubusercontent.com/78957509/171724887-686a39ef-da92-4b47-ab4f-272ad023996c.mp4
### 4) Delete an existing employee
https://user-images.githubusercontent.com/78957509/171724906-bc702522-420d-41d5-ab11-790d3a1c015f.mp4

## Architecture overview

![EMS-architecture drawio (1)](https://user-images.githubusercontent.com/78957509/170812107-53de61b5-cf2b-4d9c-b4de-645089f41969.png)


Angular app contains templates which render the single page web displaying to clients. The templates are associated with components which contain HTML templates that declares what renders on the page, a TypeScript class that defines behavior, and a CSS selector that defines how the component is used in a template. The services are responsible for communicating with Spring Boot Controller via HTTP. 

Spring Boot app contains the controller which routes to a desired function based on given URI path. Spring data JPA defines repository interfaces which simply performs CRUD operation, sorting, and pagination on MYSQL database 

## Technology and versioning

| Front-end         | Back-end                   |
| ----------------- | -----------------          |
| Angular 10        | Spring Boot 2.7.0          |
| TypeScript        | Java SE 17                 |
| NodeJS 14.7       | Maven 3.2+                 |
| NPM 8.0+          | Embedded Tomcat            |
| VSCode 1.6+       | MySQL Database (Workbench) |
| BootStrap 4       | Eclipse STS 4.4+           |

## I) Setup Spring and MySql

### Initialize Spring Starter project in Eclipse STS

Add necessary dependencies like below:
![spring-dependencies](https://user-images.githubusercontent.com/78957509/170806195-f2af2f73-a2fe-4bb4-8b53-914244503a16.PNG)

### Configure MYSQL database in application.properties
In order to connect Spring Boot app to our local mySQL database, we need to configure like below:
```
spring.datasource.url = jdbc:mysql://{your_mysql_url}/employee_mangement_system?useSSL=false
spring.datasource.username=root
spring.datasource.password={password}

spring.jpa.properties.hibernate.dialect = org.hibernate.dialect.MySQL5InnoDBDialect
spring.jpa.hibernate.ddl-auto=update
```

## Detail instructions on creating the app (dev)

### Create JPA Entity (Employee.java)
Employee is an entity which will be persisted in MYSQL database. Create a package named "model" under src/main/java. Under model package, create a new Employee model class like below:
##### 1) Create model class with setters, getters, and constructors
##### 2) Annotate @Entity and @Table with name of the table to map into database
##### 3) Annotate @Id for primary key and specify @Column name 
```
package com.example.demo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "employees")
public class Employee {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(name = "first_name")
	private String first_name;
	
	@Column(name = "last_name")
	private String last_name;
	
	@Column(name = "email_id")
	private String email_id;
	
	public Employee() {
		
	}
	
	public Employee(String first_name, String last_name, String email_id) {
		super();
		this.first_name = first_name;
		this.last_name = last_name;
		this.email_id = email_id;
	}

	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public String getFirst_name() {
		return first_name;
	}
	
	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}
	
	public String getLast_name() {
		return last_name;
	}
	
	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}
	
	public String getEmail_id() {
		return email_id;
	}
	
	public void setEmail_id(String email_id) {
		this.email_id = email_id;
	}
	
}
```

### Create JPA Repository (employeeRepository.java)
JPA repository helps handling CRUD+L operations on MySQL database without us writing any SQL statement. 

##### 1) Create EmployeeRepository class which extends JPA repository Interface of <model, primary key type>
##### 2) Annotate @Repository

```
package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>{
	
}

```

### Create custom exception (Resourcenotfoundexception.java)
This will handle bad request from client
##### 1) Create custom exception constructor and call super() method
##### 2) Annotate @ResponseStatus to specify HTTP status of the exception
##### 3) RuntimeException implements serializable interface so we need to define serial version

```
package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	public ResourceNotFoundException(String message) {
		super(message);
	}
}
```
## II) Create rest API for listing employee and connect the API to Angular

### Create Rest Controller for listing employees API (EmployeeController.java)

##### 1) Annotate @RestController and @RequestMapping (endpoint of the main controller)
##### 2) Autowired repository entity
##### 3) If Angular request end point matches with @GetMapping path, getAllEmployees method will be performed
##### 4) Simply use employee_repo instance to call findAll() method.

Note: @CrossOrigin() annotation is for connecting to Angular server later on

```
package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
	
}

```

### Run Spring Boot app and test response with Postman

Add some metadata to MYSQL workbench before testing the endpoint so you see data being returned

![image](https://user-images.githubusercontent.com/78957509/170807527-b3123121-4988-4608-8c26-0588ee1e5950.png)

### Create Angular App

##### 1) Prereqs: Install Node and npm 
##### 2) Follow terminal instructions on [AngularCLI](https://angular.io/cli)
```
$ npm install -g @angular/cli
```
##### 3) Create new angular folder project and run the server
```
ng new my-first-project
cd my-first-project
ng serve
```

### Add Bootstrap 4 in Angular

Bootstrap allows CSS styling in HTML files. import Bootstrap inside style.css like below:
```
@import "~bootstrap/dist/css/bootstrap.min.css";
```
### Create Angular Components for listing employees
App component will route to specific component (we need to specify in app.component.ts) which will call its corresponding services

##### 1) Create Typescript class for Employee to hold response data (Employee.ts)
```
$ ng g class employee
```
Then add Employee atrributes similar to Employee Model in Spring Boot
```
export class Employee {
    id!: number;
    first_name!: string;
    last_name!: string;
    email_id!: string;
}

```
##### 2) Generate list employee component via ng
```
$ ng g c employee-list
```
##### 3) Provides a html template code for Employee component 
```
<h2>Employee List</h2>
<table class = "table table-striped">
    <thead>
        <tr>
            <th> First Name</th>
            <th> Last Name </th>
            <th> email Id </th>
        </tr>
    </thead>

    <tbody>
    	// loop over each employee inside employee array per table row 
        <tr *ngFor = "let employee of employees"> 
            <td> {{employee.first_name}}</td>
            <td> {{employee.last_name}}</td>
            <td> {{employee.email_id}}</td>
        </tr>
    </tbody>
</table>
```
##### 4) Initialize Employee array instance in employee-list.components.ts
```
employees!: Employee[];
```

### Connect Angular with List Employee Rest API via HTTPClient
##### 1) Generate employee service via ng
The employee service is responsible for communicating with Rest APIs in Spring Boot/Tomcat server
```
ng g s employee
```
##### 2) Import HTTPClient in app.module.ts
```
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http'
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { EmployeeListComponent } from './employee-list/employee-list.component';

@NgModule({
  declarations: [
    AppComponent,
    EmployeeListComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
```
##### 3) Dependency Inject HTTPClient in employee.service.ts
##### 4) define base URL to establish request to in employee.service.ts
##### 5) Create a getEmployeesList method that returns observerable type of employee list in employee.service.ts
```
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http'
import { Observable } from 'rxjs';
import { Employee } from './employee';

@Injectable({
  providedIn: 'root'
})
export class EmployeeService {

  private baseURL = "http://localhost:8080/api/v1/employees";

  constructor(private httpClient: HttpClient) { }
  
  getEmployeesList(): Observable<Employee[]>{
    return this.httpClient.get<Employee[]>(`${this.baseURL}`);
  }
}
```
##### 6) Dependency Injected Employee Service in employee-list.component.ts
##### 7) Create a method to get employees via asynchronous call to employee service
```
import { Component, OnInit } from '@angular/core';
import {Employee} from '../employee'
import { EmployeeService } from '../employee.service';

@Component({
  selector: 'app-employee-list',
  templateUrl: './employee-list.component.html',
  styleUrls: ['./employee-list.component.css']
})
export class EmployeeListComponent implements OnInit {

  employees!: Employee[];

  constructor(private employeeService: EmployeeService) { }

  ngOnInit(): void {
    this.getEmployees();
  }

  private getEmployees(){
    this.employeeService.getEmployeesList().subscribe(data => {
      this.employees = data;
    });
  }

}

```
##### 7) Connect Angular to Spring Boot
Includes Angular server in the EmployeeController.java 
```
@CrossOrigin(origins = "http://localhost:4200")
```
##### 8) Result:
![image](https://user-images.githubusercontent.com/78957509/170810579-553c0484-c6b0-49fb-96f6-b287f97e19ef.png)

### Routing and Navigation in Angular 
##### 1) Route /employees path to employee-list component in app-routing-module.ts
##### 2) Redirect http://localhost:4200 to http://localhost:4200/employees in app-routing-module.ts
```
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { EmployeeListComponent } from './employee-list/employee-list.component';

const routes: Routes = [
  {path: 'employees', component: EmployeeListComponent},
  {path: '', redirectTo: "employees", pathMatch: "full"}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
```

##### 3) Add [routeroutlet](https://angular.io/api/router/RouterOutlet) in app.component.html
##### 4) Add navigation to Employee List text so when the user clicks the text, they will navigate to /employees path in app.component.html
##### 5) Add simple footer in app.component.html
```
<nav class = "navbar navbar-expand-sm bg-primary navbar-dark">
    <ul class = "navbar-nav">
        <li class = "nav-item">
            <a routerLink = "employees" routerLinkActive = "active" class = "nav-link" > Employee List</a>
        </li>
    </ul>
</nav>
<h1 class = "text-center"> {{title}}</h1>
<div class = "container">
    <router-outlet></router-outlet>
</div>
<footer class = "footer"> 
    <div class = "container">
        <span> Recreated by Kray Nguyen. All rights reserve to Java Guides </span>
    </div>
</footer>
```

##### 6) design footer attributes as we desire in styles.css
```
/* You can add global styles to this file, and also import other style files */
@import "~bootstrap/dist/css/bootstrap.min.css";

.footer {
    position: absolute;
    bottom: 0;
    width: 100%;
    height: 70px;
    background-color: red;
    text-align: center;
    color:white;
}
```
##### 7) Result:
![image](https://user-images.githubusercontent.com/78957509/170811121-59fe0a45-d07a-4096-ae88-f3352e2bbe71.png)

## III) Create rest API for creating an employee and connect the API to Angular

### Create createEmployee Rest API
##### 1) Initiate CreateEmployee method in EmployeeController.java taking in an Employee entity from client using @RequestBody annotation
##### 2) Use @PostMapping annotation to create endpoint for user to perform create employee
```
package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
	
}
```
##### 3) Test the above function with Post HTTP providing the employee (.json) via Postman
##### 4) Result 
200 OK response means the new employee is created in the database. You can go to angular web server to double check the newly created employee as well.
![Screenshot 2022-05-31 173759](https://user-images.githubusercontent.com/78957509/171305598-cfe698e3-74a8-4174-9926-224cf3f9f12b.png)

### Create Angular CreateEmployee Component
For create operation, we use two-way binding to listen for events and update values simultaneously between parent and child components. Property binding sets a specific element property whereas event binding	listens for an element change event.

##### 1) Initiate create-employee component 
```
ng g c create-employee
```
##### 2) Configure pathing route in app-routing.module.ts
```
const routes: Routes = [
  {path: 'employees', component: EmployeeListComponent},
  {path: 'create-employee', component: CreateEmployeeComponent},
  {path: '', redirectTo: "employees", pathMatch: "full"}
];
```
##### 3) Configure a router link for create employeee in app-component.html
```
<nav class = "navbar navbar-expand-sm bg-primary navbar-dark">
    <ul class = "navbar-nav">
        <li class = "nav-item">
            <a routerLink = "employees" routerLinkActive = "active" class = "nav-link" > Employee List</a>
        </li>
        <li class = "nav-item">
            <a routerLink = "create-employee" routerLinkActive = "active" class = "nav-link" > Add Employee</a>
        </li>
    </ul>
</nav>
```
### Form handling for create employee component - Angular
##### 1) Initiate new employee instance in create-employee.component.ts
##### 2) Create Onsubmit() method 
```
import { Component, OnInit } from '@angular/core';
import { Employee } from '../employee';

@Component({
  selector: 'app-create-employee',
  templateUrl: './create-employee.component.html',
  styleUrls: ['./create-employee.component.css']
})
export class CreateEmployeeComponent implements OnInit {
  employee: Employee = new Employee();

  constructor() { }

  ngOnInit(): void {
  }
  onSubmit(){
  console.log(this.employee);
  }

}
```
##### 3) Design HTML form in create-employee.component.html (form with Employee attributes + submit button)
##### 4) Create an event handler for event biding when data is submitted to the form using ngSubmit
##### 5) Use NgModel for property binding 
```
<div class = "col-md-6 offset-md-3">
    <h3> Create Employee </h3>
    <form (ngSubmit) = "onSubmit()">
        <div class = "form-group">
            <label>First Name</label>
            <input type = "text" class = "form-control" id = "first_name"
                [(ngModel)] = "employee.first_name" name = "first_name">
        </div>

        <div class = "form-group">
            <label>Last Name</label>
            <input type = "text" class = "form-control" id = "last_name"
                [(ngModel)] = "employee.last_name" name = "last_name">
        </div>

        <div class = "form-group">
            <label>Email ID</label>
            <input type = "text" class = "form-control" id = "email_id"
                [(ngModel)] = "employee.email_id" name = "email_id">
        </div>

        <div class = "form-group">
            <label>Depatment</label>
            <input type = "text" class = "form-control" id = "department"
                [(ngModel)] = "employee.department" name = "department">
        </div>

        <div class = "form-group">
            <label>Title</label>
            <input type = "text" class = "form-control" id = "title"
                [(ngModel)] = "employee.title" name = "title">
        </div>

        <button class = "btn btn-success" type = "submit">Submit</button>
	<br>
    </form>
</div>

```
##### 6) import form module into app.module.ts
```
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http'
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { EmployeeListComponent } from './employee-list/employee-list.component';
import { CreateEmployeeComponent } from './create-employee/create-employee.component';
import { FormsModule } from '@angular/forms';

@NgModule({
  declarations: [
    AppComponent,
    EmployeeListComponent,
    CreateEmployeeComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }

```
##### 7) Test the submit form with web console
![image](https://user-images.githubusercontent.com/78957509/171310095-1e0855f0-3c34-47ea-894a-d065bf3309dc.png)

### Connect Angular to createEmployee Rest API in Spring Boot
##### 1) Create a service method to HTTP post request to createEmployee API in employee.service.ts
```
createEmployee(employee: Employee): Observable<Object>{
    return this.httpClient.post(`${this.baseURL}`,employee);
}
```
##### 2) Dependency Inject employeeService and router instance in create-employee.component.ts
##### 3) Create a saveEmployee() method to call the employee service method and call goToEmployeeList() method to redirect client to employee listing route in create-employee.component.ts
##### 4) Create goToEmployeeList() method to allow the client to route to after successfully create the Employee instance in create-employee.component.ts
##### 5) Call saveEmployee() method inside onSubmit() method in create-employee.component.ts
```
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Employee } from '../employee';
import { EmployeeService } from '../employee.service';

@Component({
  selector: 'app-create-employee',
  templateUrl: './create-employee.component.html',
  styleUrls: ['./create-employee.component.css']
})
export class CreateEmployeeComponent implements OnInit {
  employee: Employee = new Employee();

  constructor(private employeeService: EmployeeService,
    private router: Router ) { }

  saveEmployee(){
    this.employeeService.createEmployee(this.employee).subscribe(data =>{
      console.log(data);
      this.goToEmployeeList();
    },
    error => console.log(error));
  }

  goToEmployeeList(){
    this.router.navigate(['/employees'])
  }

  ngOnInit(): void {
  }

  onSubmit(){
    console.log(this.employee);
    this.saveEmployee();
  }

}
```
##### 6) Test submit operation in Angular server

## IV) Create rest API for updating an employee and connect the API to Angular

### Create GetEmployeeByID Rest API - Spring
##### 1) Initiate getEmployee method in EmployeeController.java taking in @Path variable long id that returns a ResponseEntity type of Employee
##### 2) Use @PostMapping annotation to create endpoint for user to look up employee by id
##### 3) Use employee repository instance to call findByID() method. If failed, route to exception to display to client
```
@GetMapping("/employees/{id}")
	public ResponseEntity<Employee> getEmployeeByID(@PathVariable Long id) {
		Employee employee = employee_repo.findById(id).
				orElseThrow(()-> new ResourceNotFoundException("Employee resource not found: " + id));
		return ResponseEntity.ok(employee);
	}
```
##### 3) Test response using Postman
![image](https://user-images.githubusercontent.com/78957509/171315378-838fc620-1643-4a7e-9b2e-95b0202fd680.png)

### Create updateEmployee Rest API - Spring
##### 1) Initiate updateEmployee() method in EmployeeController.java taking in @Path variable long id and an Employee instance that returns a ResponseEntity type of Employee
##### 2) Use @PutMapping annotation to create endpoint for user to update employee by id
##### 3) Get employee instance by ID(instance retreived from db) and set that instance attributes (via setters) to the desired attributes provided by client (via getter)
##### 4) Save the updated employee to the database 
```
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
```
##### 4) Test via Postman using Put Method 
![Screenshot 2022-05-31 195437](https://user-images.githubusercontent.com/78957509/171318586-a791f75b-ba93-4e07-98cd-045432f4fd54.png)

### Connect Angular to updateEmployee Rest API in Spring Boot
##### 1) Create a service method to HTTP put request to updateEmployee API in employee.service.ts
```
updateEmployee(id: number, employee: Employee): Observable<Object>{
    return this.httpClient.put(`${this.baseURL}/${id}`, employee);
  }
```
##### 2) Dependency Inject employeeService, router, and activated route instance in create-employee.component.ts
##### 3) Use activated route to inject path parameter and assign local instance in update-employee.component.ts
```
ngOnInit(): void {
    this.id = this.route.snapshot.params['id'];
    this.employeeService.getEmployeeByID(this.id).subscribe(data => {
      this.employee = data;
    },
    error => console.log(error));
  }
```
##### 3) Create onSubmit() that calls update employee service when client click the button 
##### 4) Create goToEmployeeList() that allows client to go back to employee listing view after sucessfully update
```
onSubmit(){
    this.employeeService.updateEmployee(this.id, this.employee).subscribe(data =>{
      console.log(data);
      this.goToEmployeeList();
    }, error => console.log(error));
  }
  
goToEmployeeList(){
    this.router.navigate(['/employees'])
    }
```
## V) Create rest API for updating an employee and connect the API to Angular

### Create deleteEmployee Rest API
##### 1) Initiate deleteEmployee method in EmployeeController.java taking in an employee id from client using @PathVariable annotation
##### 2) Use @DeleteMapping annotation to create endpoint for user to perform delete employee
##### 3) Use repository instance to retreive the employee instance by their id 
##### 4) Use repository instance to delete the retrieved employee instance from the database
##### 5) Return ok response

```
@DeleteMapping("employees/{id}")
	public ResponseEntity<Map<String, Boolean>> deleteEmployee(@PathVariable Long id){
		Employee employee = employee_repo.findById(id).
				orElseThrow(()-> new ResourceNotFoundException("Employee resource not found: " + id));
		employee_repo.delete(employee);
		Map<String, Boolean> response = new HashMap<>();
		response.put("deleted", true);
		return ResponseEntity.ok(response);
	}
```
### Connect Angular to deleteEmployee Rest API in Spring Boot
##### 1) Create a service method to HTTP delete request to deleteEmployee API in employee.service.ts
```
deleteEmployee(id: number): Observable<Object>{
    return this.httpClient.delete(`${this.baseURL}/${id}`);
  }
```
##### 2) create deleteEmployee() that initiate employee service to deleteEmployee() in employee-list.component.ts
```
deleteEmployee(id: number){
    this.employeeService.deleteEmployee(id).subscribe(data => {
      console.log(data);
      this.getEmployees();
    })
  }
```
##### 3) Add a delete button that calls deleteEmployee() in employee-list.component.html
```
<td>
      <button (click) = "updateEmployee(employee.id)" class = "btn btn-info"> Update</button>
      <button (click) = "deleteEmployee(employee.id)" class = "btn btn-danger" style = "margin-left: 1em"> Delete </button>
</td>
```
##### 4) Test result on Angular Server

## VI) Conclusion
Key points that we have achieved:
##### 1) Set up Spring and Angular project
##### 2) Configure MySQL to communicate with Spring (Spring data JPA - Hibernate)
##### 3) Create rest API for listing employees in Spring and connect the API to Angular
##### 4) Create rest API for creating an employee in Spring and connect the API to Angular
##### 5) Create rest API for updating an employee in Spring and connect the API to Angular
##### 6) Create rest API for deleting an employee in Spring and connect the API to Angular
##### 7) Testing interfaces via Angular server and REST APIs via Postman Client







