package com.softaliance.employeemanagement.controller;

import com.softaliance.employeemanagement.responses.ApiResponse;
import com.softaliance.employeemanagement.services.DepartmentService;
import com.softaliance.employeemanagement.services.EmployeeService;
import com.softaliance.employeemanagement.utils.Utilities;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/manager/")
public class ManagerController {
    private final DepartmentService departmentService;
    private final Utilities utilities;

    public ManagerController(DepartmentService departmentService, Utilities utilities) {
        this.departmentService = departmentService;
        this.utilities = utilities;
    }

    @GetMapping("/roles/department/{email}")
    @PreAuthorize("hasAuthority('Manager')")
    public ResponseEntity<ApiResponse> fetchEmployeesByDepartment(@PathVariable String email) {
        ApiResponse apiResponse = departmentService.viewEmployeesInDepartment(email);
        return utilities.getApiResponseResponseEntity(apiResponse);
    }
}
