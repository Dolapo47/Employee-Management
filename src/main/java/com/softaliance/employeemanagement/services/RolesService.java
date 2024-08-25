package com.softaliance.employeemanagement.services;

import com.softaliance.employeemanagement.models.Roles;
import com.softaliance.employeemanagement.repository.RolesRepository;
import com.softaliance.employeemanagement.requests.RolesRequest;
import com.softaliance.employeemanagement.responses.ApiResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RolesService {

    private final RolesRepository rolesRepository;

    public RolesService(RolesRepository rolesRepository) {
        this.rolesRepository = rolesRepository;
    }

    public ApiResponse getRole(Long id) {
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
                        .message("Unable to retrieve role")
                        .build();
            }
        }catch (Exception e){
            return ApiResponse.builder()
                    .code("99")
                    .message("Unable to retrieve role, try again later")
                    .build();
        }
    }

    public ApiResponse getAllRoles() {
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
                    .message("Unable to retrieve roles, try again later")
                    .build();
        }
    }

    public ApiResponse createRoles(RolesRequest request) {
        Roles roles = new Roles();
        try{
            roles.setName(request.getName());
            roles.setDescription(request.getDescription());
            roles = rolesRepository.save(roles);
        } catch (DataIntegrityViolationException e) {
            return ApiResponse.builder()
                    .code("99")
                    .message("Unable to create role, role details has been used")
                    .build();
        }
        catch (Exception e){
            return ApiResponse.builder()
                    .code("99")
                    .message("Unable to create role, try again later")
                    .build();
        }
        return ApiResponse.builder()
                .code("00")
                .message("Success")
                .data(roles)
                .build();
    }

    public ApiResponse updateRole(Long id, RolesRequest request) {
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
                        .message("Unable to retrieve role")
                        .build();
            }
        }catch(Exception e){
            return ApiResponse.builder()
                    .code("99")
                    .message("Unable to update role, try again later")
                    .build();
        }
        return ApiResponse.builder()
                .code("00")
                .message("Success")
                .data(savedRoles.get())
                .build();
    }

    public ApiResponse deleteRole(Long id) {
        Optional<Roles> savedRoles;
        try {
            savedRoles = rolesRepository.findById(id);
            if (savedRoles.isPresent()) {
                rolesRepository.delete(savedRoles.get());
            }else {
                return ApiResponse.builder()
                        .code("90")
                        .message("Role not found")
                        .build();
            }
        }catch (Exception e){
            return ApiResponse.builder()
                    .code("99")
                    .message("Unable to delete role, try again later")
                    .build();
        }
        return ApiResponse.builder()
                .code("00")
                .message("Success")
                .build();
    }
}
