package com.softaliance.employeemanagement.controller;

import com.softaliance.employeemanagement.requests.DepartmentRequest;
import com.softaliance.employeemanagement.responses.ApiResponse;
import com.softaliance.employeemanagement.services.DepartmentService;
import com.softaliance.employeemanagement.utils.Utilities;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/department")
public class DepartmentController {
    private final DepartmentService departmentService;
    private final Utilities utilities;

    public DepartmentController(DepartmentService departmentService, Utilities utilities) {
        this.departmentService = departmentService;
        this.utilities = utilities;
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<ApiResponse> getDepartment(@PathVariable String id) {
        ApiResponse apiResponse;
        apiResponse = departmentService.getDepartment(Long.parseLong(id));
        return utilities.getApiResponseResponseEntity(apiResponse);
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createDepartment(@Valid @RequestBody DepartmentRequest request) {
        ApiResponse apiResponse = departmentService.createDepartment(request);
        if(apiResponse.getCode().equals("00")){
            return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/get/all")
    public ResponseEntity<ApiResponse> getAllDepartments() {
        ApiResponse apiResponse = departmentService.getAllDepartments();
        if(apiResponse.getCode().equals("00")){
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        }
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse> updateDepartment(@RequestBody DepartmentRequest request, @PathVariable String id) {
        ApiResponse apiResponse = departmentService.updateDepartment(Long.parseLong(id), request);
        return utilities.getApiResponseResponseEntity(apiResponse);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse> deleteDepartment(@PathVariable String id) {
        ApiResponse apiResponse = departmentService.deleteDepartment(Long.parseLong(id));
        return utilities.getApiResponseResponseEntity(apiResponse);
    }

//    @GetMapping("/add/manager/{email}")
//    public ResponseEntity<ApiResponse> addManagerToDepartment(@PathVariable String email) {
//        ApiResponse apiResponse = departmentService.addManagerToDepartment(email);
//        return utilities.getApiResponseResponseEntity(apiResponse);
//    }
}
