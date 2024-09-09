package com.softaliance.employeemanagement.services;

import com.softaliance.employeemanagement.models.Department;
import com.softaliance.employeemanagement.models.Employee;
import com.softaliance.employeemanagement.repository.DepartmentRepository;
import com.softaliance.employeemanagement.repository.EmployeeRepository;
import com.softaliance.employeemanagement.repository.RolesRepository;
import com.softaliance.employeemanagement.requests.DepartmentRequest;
import com.softaliance.employeemanagement.responses.ApiResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DepartmentService {
    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    public DepartmentService(DepartmentRepository departmentRepository, EmployeeRepository employeeRepository) {
        this.departmentRepository = departmentRepository;
        this.employeeRepository = employeeRepository;
    }

    public ApiResponse getDepartment(Long id) {
        Optional<Department> department;
        try{
            department = departmentRepository.findById(id);
            if (department.isPresent()) {
                return ApiResponse.builder()
                        .code("00")
                        .message("Success")
                        .data(department.get())
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
        List<Department> departments;
        try {
            departments = departmentRepository.findAll();
            return ApiResponse.builder()
                    .code("00")
                    .message("Success")
                    .data(departments)
                    .build();
        }catch (Exception e){
            return ApiResponse.builder()
                    .code("99")
                    .message("Unable to retrieve departments, try again later")
                    .build();
        }
    }

    public ApiResponse createDepartment(DepartmentRequest request) {
        Department department = new Department();
        try{
            department.setName(request.getName());
            department.setDescription(request.getDescription());
            department = departmentRepository.save(department);
        }catch (DataIntegrityViolationException e) {
            return ApiResponse.builder()
                    .code("99")
                    .message("Unable to create department, department details has been used")
                    .build();
        }
        catch (Exception e){
            return ApiResponse.builder()
                    .code("99")
                    .message("Unable to create departments, try again later")
                    .build();
        }
        return ApiResponse.builder()
                .code("00")
                .message("Success")
                .data(department)
                .build();
    }

    public ApiResponse updateDepartment(Long id, DepartmentRequest request) {
        Optional<Department> savedDepartment;
        try{
            savedDepartment = departmentRepository.findById(id);
            if (savedDepartment.isPresent()) {
                savedDepartment.get().setName(request.getName());
                savedDepartment.get().setDescription(request.getDescription());
                departmentRepository.save(savedDepartment.get());
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
                .data(savedDepartment.get())
                .build();
    }

    public ApiResponse deleteDepartment(Long id) {
        Optional<Department> savedDepartment;
        try {
            savedDepartment = departmentRepository.findById(id);
            if (savedDepartment.isPresent()) {
                departmentRepository.delete(savedDepartment.get());
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

    public ApiResponse viewEmployeesInDepartment() {
        Optional<Employee> employee;
        Optional<Department> department;
        List<Employee> employees;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        try{
            employee = employeeRepository.findByEmail(authentication.getName());
            if (employee.isEmpty()) {
                return ApiResponse.builder()
                        .code("90")
                        .message("Employee not found")
                        .build();
            }
            if(employee.get().getRoles().getName().equalsIgnoreCase("manager")
            || employee.get().getRoles().getName().equalsIgnoreCase("admin")) {
                department = departmentRepository.findById(employee.get().getDepartment().getId());
                if (department.isEmpty()) {
                    return ApiResponse.builder()
                            .code("90")
                            .message("Department not found")
                            .build();
                }
                employees = employeeRepository.findEmployeesByDepartment(department.get());
            }else{
                return ApiResponse.builder()
                        .code("99")
                        .message("Employee does not have privileges to access")
                        .build();
            }
        }catch (Exception e){
            return ApiResponse.builder()
                    .code("99")
                    .message("Unable to retrieve employees, try again later")
                    .build();
        }
        return ApiResponse.builder()
                .code("00")
                .message("Success")
                .data(employees)
                .build();
    }
}
