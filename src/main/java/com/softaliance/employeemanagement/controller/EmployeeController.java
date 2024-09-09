package com.softaliance.employeemanagement.controller;

import com.softaliance.employeemanagement.requests.EmployeeRequest;
import com.softaliance.employeemanagement.responses.ApiResponse;
import com.softaliance.employeemanagement.responses.AuthResponse;
import com.softaliance.employeemanagement.services.EmployeeService;
import com.softaliance.employeemanagement.utils.Utilities;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final Utilities utilities;

    public EmployeeController(EmployeeService employeeService, Utilities utilities) {
        this.employeeService = employeeService;
        this.utilities = utilities;
    }

    @GetMapping("/get/id/{id}")
    public ResponseEntity<ApiResponse> getEmployee(@PathVariable String id) {
        ApiResponse apiResponse;
        apiResponse = employeeService.getEmployee(Long.parseLong(id));
        return utilities.getApiResponseResponseEntity(apiResponse);
    }

    @GetMapping("/get/email/{email}")
    public ResponseEntity<ApiResponse> getEmployeeByEmail(@PathVariable String email) {
        ApiResponse apiResponse;
        apiResponse = employeeService.getEmployeeByEmail(email);
        AuthResponse authResponse = (AuthResponse) apiResponse.getData();
        authResponse.setPassword(null);
        apiResponse.setData(authResponse);
        return utilities.getApiResponseResponseEntity(apiResponse);
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createEmployee(@Valid @RequestBody EmployeeRequest request) {
        ApiResponse apiResponse = employeeService.addEmployee(request);
        if(apiResponse.getCode().equals("00")){
            return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/get/all")
    public ResponseEntity<ApiResponse> getAllEmployees() {
        ApiResponse apiResponse = employeeService.getAllEmployees();
        if(apiResponse.getCode().equals("00")){
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        }
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse> updateEmployee(@RequestBody EmployeeRequest request, @PathVariable String id) {
        ApiResponse apiResponse = employeeService.updateEmployee(Long.parseLong(id), request);
        return utilities.getApiResponseResponseEntity(apiResponse);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse> deleteEmployee(@PathVariable String id) {
        ApiResponse apiResponse = employeeService.deleteEmployee(Long.parseLong(id));
        return utilities.getApiResponseResponseEntity(apiResponse);
    }

}
