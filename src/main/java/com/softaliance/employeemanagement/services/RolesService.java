package com.softaliance.employeemanagement.services;

import com.softaliance.employeemanagement.models.Roles;
import com.softaliance.employeemanagement.repository.RolesRepository;
import com.softaliance.employeemanagement.requests.DepartmentRequest;
import com.softaliance.employeemanagement.responses.ApiResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RolesService {

    private final RolesRepository rolesRepository;

    public RolesService(RolesRepository rolesRepository) {
        this.rolesRepository = rolesRepository;
    }

    public ApiResponse getDepartment(Long id) {
        Optional<Roles> roles;
        try{
            roles = rolesRepository.findById(id);
            if (roles.isPresent()) {
                return ApiResponse.builder()
                        .code("00")
                        .message("Success")
                        .data(roles.get())
                        .build();
            }else{
                return ApiResponse.builder()
                        .code("90")
                        .message("Unable to retrieve department")
                        .build();
            }
        }catch (Exception e){
            return ApiResponse.builder()
                    .code("99")
                    .message("Unable to retrieve department, try again later")
                    .build();
        }
    }

    public ApiResponse getAllDepartments() {
        List<Roles> roles;
        try {
            roles = rolesRepository.findAll();
            return ApiResponse.builder()
                    .code("00")
                    .message("Success")
                    .data(roles)
                    .build();
        }catch (Exception e){
            return ApiResponse.builder()
                    .code("99")
                    .message("Unable to retrieve departments, try again later")
                    .build();
        }
    }

    public ApiResponse createDepartment(DepartmentRequest request) {
        Roles roles = new Roles();
        try{
            roles.setName(request.getName());
            roles.setDescription(request.getDescription());
            roles = rolesRepository.save(roles);
        } catch (Exception e){
            return ApiResponse.builder()
                    .code("99")
                    .message("Unable to create departments, try again later")
                    .build();
        }
        return ApiResponse.builder()
                .code("00")
                .message("Success")
                .data(roles)
                .build();
    }

    public ApiResponse updateDepartment(Long id, DepartmentRequest request) {
        Optional<Roles> savedRoles;
        try{
            savedRoles = rolesRepository.findById(id);
            if (savedRoles.isPresent()) {
                savedRoles.get().setName(request.getName());
                savedRoles.get().setDescription(request.getDescription());
                rolesRepository.save(savedRoles.get());
            }else{
                return ApiResponse.builder()
                        .code("90")
                        .message("Unable to retrieve department")
                        .build();
            }
        }catch(Exception e){
            return ApiResponse.builder()
                    .code("99")
                    .message("Unable to update department, try again later")
                    .build();
        }
        return ApiResponse.builder()
                .code("00")
                .message("Success")
                .data(savedRoles.get())
                .build();
    }

    public ApiResponse deleteDepartment(Long id) {
        Optional<Roles> savedRoles;
        try {
            savedRoles = rolesRepository.findById(id);
            if (savedRoles.isPresent()) {
                rolesRepository.delete(savedRoles.get());
            }else {
                return ApiResponse.builder()
                        .code("90")
                        .message("Department not found")
                        .build();
            }
        }catch (Exception e){
            return ApiResponse.builder()
                    .code("99")
                    .message("Unable to delete department, try again later")
                    .build();
        }
        return ApiResponse.builder()
                .code("00")
                .message("Success")
                .build();
    }
}
