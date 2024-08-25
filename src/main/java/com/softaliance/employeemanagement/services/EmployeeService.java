package com.softaliance.employeemanagement.services;

import com.softaliance.employeemanagement.models.Department;
import com.softaliance.employeemanagement.models.Employee;
import com.softaliance.employeemanagement.models.Roles;
import com.softaliance.employeemanagement.repository.DepartmentRepository;
import com.softaliance.employeemanagement.repository.EmployeeRepository;
import com.softaliance.employeemanagement.repository.RolesRepository;
import com.softaliance.employeemanagement.requests.EmployeeRequest;
import com.softaliance.employeemanagement.responses.ApiResponse;
import com.softaliance.employeemanagement.responses.AuthResponse;
import com.softaliance.employeemanagement.utils.Utilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {
    private final Logger logger = LoggerFactory.getLogger(EmployeeService.class);
    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final RolesRepository roleRepository;
    private final Utilities utilities;

    @Value("${employee.password}")
    private String employeePassword;

    public EmployeeService(EmployeeRepository employeeRepository, DepartmentRepository departmentRepository, RolesRepository roleRepository, Utilities utilities) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.roleRepository = roleRepository;
        this.utilities = utilities;
    }
    public ApiResponse getEmployee(Long id) {
        Optional<Employee> employee;
        try{
            employee = employeeRepository.findById(id);
            if (employee.isPresent()) {
                employee.get().setPassword(null);
                return ApiResponse.builder()
                        .code("00")
                        .message("Success")
                        .data(employee.get())
                        .build();
            }
            return ApiResponse.builder()
                    .code("90")
                    .message("Employee not found")
                    .build();
        }catch (Exception e){
            logger.error(e.getMessage());
            return ApiResponse.builder()
                    .code("99")
                    .message("Unable to retrieve employee, try again later")
                    .build();
        }
    }

    public ApiResponse getEmployeeByEmail(String email) {
        Optional<Employee> employee;
        try{
            employee = employeeRepository.findByEmail(email);

            if (employee.isPresent()) {
                AuthResponse response = AuthResponse.builder()
                        .email(employee.get().getEmail())
                        .password(employee.get().getPassword())
                        .phone(employee.get().getPhone())
                        .address(employee.get().getAddress())
                        .firstName(employee.get().getFirstName())
                        .lastName(employee.get().getLastName())
                        .build();
                return ApiResponse.builder()
                        .code("00")
                        .message("Success")
                        .data(response)
                        .build();
            }
            return ApiResponse.builder()
                    .code("90")
                    .message("Employee not found")
                    .build();
        }catch (Exception e){
            logger.error(e.getMessage());
            return ApiResponse.builder()
                    .code("99")
                    .message("Unable to retrieve employee, try again later")
                    .build();
        }
    }

    public ApiResponse getAllEmployees() {
        List<Employee> employees;
        try{
            employees = employeeRepository.findAll();
            for (Employee employee : employees) {
                employee.setPassword(null);
            }
            return ApiResponse.builder()
                    .code("00")
                    .message("Success")
                    .data(employees)
                    .build();
        }catch (Exception e){
            logger.error(e.getMessage());
            return ApiResponse.builder()
                    .code("99")
                    .message("Unable to retrieve employee, try again later")
                    .build();
        }
    }

    public ApiResponse addEmployee(EmployeeRequest createEmployeeRequest) {
        Employee employee = new Employee();
        Optional<Department> savedDepartment;
        Optional<Roles> savedRole;
        try{
            if(createEmployeeRequest.getDepartmentId() != null &&
                    !createEmployeeRequest.getDepartmentId().isEmpty()) {
                    savedDepartment = departmentRepository.findById(Long.parseLong(createEmployeeRequest.getDepartmentId()));
                    if(savedDepartment.isPresent()) {
                        savedDepartment.ifPresent(employee::setDepartment);
                    }else{
                        return ApiResponse.builder()
                                .code("99")
                                .message("Unable to create employee, department not found")
                                .build();
                    }
            }
            if(createEmployeeRequest.getRoleId() != null &&
                    !createEmployeeRequest.getRoleId().isEmpty()) {
                savedRole = roleRepository.findById(Long.parseLong(createEmployeeRequest.getRoleId()));
                if(savedRole.isPresent()) {
                    savedRole.ifPresent(employee::setRoles);
                }else{
                    return ApiResponse.builder()
                            .code("99")
                            .message("Unable to create employee, role not found")
                            .build();
                }
            }
            employee.setPhone(createEmployeeRequest.getPhone());
            employee.setFirstName(createEmployeeRequest.getFirstName());
            employee.setLastName(createEmployeeRequest.getLastName());
            employee.setAddress(createEmployeeRequest.getAddress());
            employee.setEmail(createEmployeeRequest.getEmail());
            employee.setPassword(utilities.encodePassword(employeePassword));
            employee = employeeRepository.save(employee);
        }catch (NumberFormatException e) {
            logger.error(e.getMessage());
            return ApiResponse.builder()
                    .code("99")
                    .message("Unable to create employee, pass in correct data")
                    .build();
        }catch (DataIntegrityViolationException e) {
            return ApiResponse.builder()
                    .code("99")
                    .message("Unable to create employee, employee details has been used")
                    .build();
        }
        catch (Exception e) {
            logger.error(e.getMessage());
            return ApiResponse.builder()
                    .code("99")
                    .message("Unable to create employee, try again later")
                    .build();
        }
        return ApiResponse.builder()
                .code("00")
                .message("Success")
                .data(employee)
                .build();
    }

    public ApiResponse updateEmployee(Long id, EmployeeRequest updateEmployeeRequest) {
        Optional<Employee> savedEmployee;
        Optional<Department> savedDepartment;
        try{
            savedEmployee = employeeRepository.findById(id);
            if(savedEmployee.isPresent()){
                if(updateEmployeeRequest.getDepartmentId() != null &&
                        !updateEmployeeRequest.getDepartmentId().isEmpty()) {
                    savedDepartment = departmentRepository.findById(Long.parseLong(updateEmployeeRequest.getDepartmentId()));
                    savedDepartment.ifPresent(department -> savedEmployee.get().setDepartment(department));
                }
                savedEmployee.get().setEmail(updateEmployeeRequest.getEmail());
                savedEmployee.get().setFirstName(updateEmployeeRequest.getFirstName());
                savedEmployee.get().setLastName(updateEmployeeRequest.getLastName());
                savedEmployee.get().setPhone(updateEmployeeRequest.getPhone());
                savedEmployee.get().setStatus(updateEmployeeRequest.getStatus());
                savedEmployee.get().setAddress(updateEmployeeRequest.getAddress());
                employeeRepository.save(savedEmployee.get());
            }else {
                return ApiResponse.builder()
                        .code("90")
                        .message("Employee not found")
                        .build();
            }
        }catch (NumberFormatException e) {
            logger.error(e.getMessage());
            return ApiResponse.builder()
                    .code("99")
                    .message("Unable to update employee, pass in correct data")
                    .build();
        }
        catch (Exception e){
            logger.error(e.getMessage());
            return ApiResponse.builder()
                    .code("99")
                    .message("Unable to update employee, try again later")
                    .build();
        }
        return ApiResponse.builder()
                .code("00")
                .message("Success")
                .data(savedEmployee)
                .build();
    }

    public ApiResponse deleteEmployee(Long id) {
        Optional<Employee> savedEmployee;
        try {
            savedEmployee = employeeRepository.findById(id);
            if (savedEmployee.isPresent()) {
                employeeRepository.delete(savedEmployee.get());
            }else {
                return ApiResponse.builder()
                        .code("90")
                        .message("Employee not found")
                        .build();
            }
        }catch (Exception e){
            return ApiResponse.builder()
                    .code("99")
                    .message("Unable to delete employee, try again later")
                    .build();
        }
        return ApiResponse.builder()
                .code("00")
                .message("Success")
                .build();
    }
}
