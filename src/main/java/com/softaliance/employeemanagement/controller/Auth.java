package com.softaliance.employeemanagement.controller;

import com.softaliance.employeemanagement.responses.ApiResponse;
import com.softaliance.employeemanagement.services.EmployeeService;
import com.softaliance.employeemanagement.utils.Utilities;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class Auth {

    private final EmployeeService employeeService;
    private final Utilities utilities;

    public Auth(EmployeeService employeeService, Utilities utilities) {
        this.employeeService = employeeService;
        this.utilities = utilities;
    }

    @GetMapping("/get/email/{email}")
    public ResponseEntity<ApiResponse> getEmployeeByEmail(@PathVariable String email) {
        ApiResponse apiResponse;
        apiResponse = employeeService.getEmployeeByEmail(email);
        return utilities.getApiResponseResponseEntity(apiResponse);
    }
}
