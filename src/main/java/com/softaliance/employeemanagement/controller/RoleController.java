package com.softaliance.employeemanagement.controller;

import com.softaliance.employeemanagement.requests.RolesRequest;
import com.softaliance.employeemanagement.responses.ApiResponse;
import com.softaliance.employeemanagement.services.RolesService;
import com.softaliance.employeemanagement.utils.Utilities;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/roles")
public class RoleController {
    private final RolesService rolesService;
    private final Utilities utilities;

    public RoleController(RolesService rolesService, Utilities utilities) {
        this.rolesService = rolesService;
        this.utilities = utilities;
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<ApiResponse> getRole(@PathVariable String id) {
        ApiResponse apiResponse;
        apiResponse = rolesService.getRole(Long.parseLong(id));
        return utilities.getApiResponseResponseEntity(apiResponse);
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createRole(@Valid @RequestBody RolesRequest request) {
        ApiResponse apiResponse = rolesService.createRoles(request);
        if(apiResponse.getCode().equals("00")){
            return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/get/all")
    public ResponseEntity<ApiResponse> getAllRoles() {
        ApiResponse apiResponse = rolesService.getAllRoles();
        if(apiResponse.getCode().equals("00")){
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        }
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse> updateRole(@RequestBody RolesRequest request, @PathVariable String id) {
        ApiResponse apiResponse = rolesService.updateRole(Long.parseLong(id), request);
        return utilities.getApiResponseResponseEntity(apiResponse);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse> deleteRole(@PathVariable String id) {
        ApiResponse apiResponse = rolesService.deleteRole(Long.parseLong(id));
        return utilities.getApiResponseResponseEntity(apiResponse);
    }
}
