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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;


public class EmployeeServiceTest {

    @InjectMocks
    private EmployeeService employeeService;

        @Mock
        private EmployeeRepository employeeRepository;
        @Mock
        private DepartmentRepository departmentRepository;
        @Mock
        private RolesRepository roleRepository;
        @Mock
        private Utilities utilities;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddEmployee_Success() {
        EmployeeRequest request = new EmployeeRequest();
        request.setDepartmentId("1");
        request.setRoleId("1");
        request.setPhone("1234567890");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setAddress("123 Main St");
        request.setEmail("john.doe@example.com");

        Department department = new Department();
        Roles role = new Roles();
        Employee savedEmployee = new Employee();

        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(employeeRepository.save(any(Employee.class))).thenReturn(savedEmployee);

        ApiResponse response = employeeService.addEmployee(request);

        assertEquals("00", response.getCode());
        assertEquals("Success", response.getMessage());
        assertEquals(savedEmployee, response.getData());
    }

    @Test
    public void testAddEmployee_DepartmentNotFound() {
        EmployeeRequest request = new EmployeeRequest();
        request.setDepartmentId("1");
        request.setRoleId("1");

        Mockito.when(departmentRepository.findById(1L)).thenReturn(Optional.empty());

        ApiResponse response = employeeService.addEmployee(request);

        assertEquals("99", response.getCode());
        assertEquals("Unable to create employee, department not found", response.getMessage());
        Mockito.verify(employeeRepository, Mockito.never()).save(Mockito.any(Employee.class));
    }

    @Test
    public void testAddEmployee_RoleNotFound() {
        EmployeeRequest request = new EmployeeRequest();
        request.setDepartmentId("1");
        request.setRoleId("1");

        Department department = new Department();
        department.setId(1L);

        Mockito.when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        Mockito.when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        ApiResponse response = employeeService.addEmployee(request);

        assertEquals("99", response.getCode());
        assertEquals("Unable to create employee, role not found", response.getMessage());
        Mockito.verify(employeeRepository, Mockito.never()).save(Mockito.any(Employee.class));
    }

    @Test
    public void testAddEmployee_InvalidDepartmentIdFormat() {
        EmployeeRequest request = new EmployeeRequest();
        request.setDepartmentId("invalid");
        request.setRoleId("1");

        ApiResponse response = employeeService.addEmployee(request);

        assertEquals("99", response.getCode());
        assertEquals("Unable to create employee, pass in correct data", response.getMessage());
        Mockito.verify(employeeRepository, Mockito.never()).save(Mockito.any(Employee.class));
    }

    @Test
    public void testAddEmployee_GeneralException() {
        EmployeeRequest request = new EmployeeRequest();
        request.setDepartmentId("1");
        request.setRoleId("1");

        Department department = new Department();
        department.setId(1L);

        Roles role = new Roles();
        role.setId(1L);

        Mockito.when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        Mockito.when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        Mockito.when(employeeRepository.save(Mockito.any(Employee.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        ApiResponse response = employeeService.addEmployee(request);

        assertEquals("99", response.getCode());
        assertEquals("Unable to create employee, try again later", response.getMessage());
    }

    @Test
    void testGetEmployee_Success() {
        Long employeeId = 1L;
        Employee employee = new Employee();
        employee.setId(employeeId);

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));

        ApiResponse response = employeeService.getEmployee(employeeId);

        assertEquals("00", response.getCode());
        assertEquals("Success", response.getMessage());
        assertEquals(employee, response.getData());
    }

    @Test
    void testGetEmployee_EmployeeNotFound() {
        Long employeeId = 1L;

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

        ApiResponse response = employeeService.getEmployee(employeeId);

        assertEquals("90", response.getCode());
        assertEquals("Employee not found", response.getMessage());
    }

    @Test
    void testGetEmployee_Exception() {
        Long employeeId = 1L;

        when(employeeRepository.findById(employeeId)).thenThrow(new RuntimeException("Database error"));

        ApiResponse response = employeeService.getEmployee(employeeId);

        assertEquals("99", response.getCode());
        assertEquals("Unable to retrieve employee, try again later", response.getMessage());
    }

    @Test
    void testGetEmployeeByEmail_Success() {
        String email = "test@example.com";
        Employee employee = new Employee();
        employee.setEmail(email);
        employee.setPassword("Password@123");
        employee.setPhone("1234567890");
        employee.setAddress("123 Street");
        employee.setFirstName("John");
        employee.setLastName("Doe");

        when(employeeRepository.findByEmail(email)).thenReturn(Optional.of(employee));

        ApiResponse response = employeeService.getEmployeeByEmail(email);

        assertEquals("00", response.getCode());
        assertEquals("Success", response.getMessage());

        AuthResponse authResponse = (AuthResponse) response.getData();
        assertEquals(email, authResponse.getEmail());
        assertEquals("Password@123", authResponse.getPassword());
        assertEquals("1234567890", authResponse.getPhone());
        assertEquals("123 Street", authResponse.getAddress());
        assertEquals("John", authResponse.getFirstName());
        assertEquals("Doe", authResponse.getLastName());
    }

    @Test
    void testGetEmployeeByEmail_EmployeeNotFound() {
        String email = "test@example.com";

        when(employeeRepository.findByEmail(email)).thenReturn(Optional.empty());

        ApiResponse response = employeeService.getEmployeeByEmail(email);

        assertEquals("90", response.getCode());
        assertEquals("Employee not found", response.getMessage());
    }

    @Test
    void testGetEmployeeByEmail_Exception() {
        String email = "test@example.com";

        when(employeeRepository.findByEmail(email)).thenThrow(new RuntimeException("Database error"));

        ApiResponse response = employeeService.getEmployeeByEmail(email);

        assertEquals("99", response.getCode());
        assertEquals("Unable to retrieve employee, try again later", response.getMessage());
    }

    @Test
    void testGetAllEmployees_Success() {
        Employee employee1 = new Employee();
        employee1.setId(1L);
        employee1.setFirstName("John");
        employee1.setLastName("Doe");

        Employee employee2 = new Employee();
        employee2.setId(2L);
        employee2.setFirstName("Jane");
        employee2.setLastName("Smith");

        List<Employee> employees = Arrays.asList(employee1, employee2);

        when(employeeRepository.findAll()).thenReturn(employees);

        ApiResponse response = employeeService.getAllEmployees();

        assertEquals("00", response.getCode());
        assertEquals("Success", response.getMessage());
        assertEquals(employees, response.getData());
    }

    @Test
    void testGetAllEmployees_EmptyList() {
        List<Employee> employees = new ArrayList<>();

        when(employeeRepository.findAll()).thenReturn(employees);

        ApiResponse response = employeeService.getAllEmployees();

        assertEquals("00", response.getCode());
        assertEquals("Success", response.getMessage());
        assertEquals(employees, response.getData());
    }

    @Test
    void testGetAllEmployees_Exception() {
        when(employeeRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        ApiResponse response = employeeService.getAllEmployees();

        assertEquals("99", response.getCode());
        assertEquals("Unable to retrieve employee, try again later", response.getMessage());
    }

    @Test
    void testUpdateEmployee_Success() {
        Long employeeId = 1L;
        Long departmentId = 2L;

        Employee existingEmployee = new Employee();
        existingEmployee.setId(employeeId);
        existingEmployee.setFirstName("John");
        existingEmployee.setLastName("Doe");
        existingEmployee.setEmail("jphn.smith@example.com");
        existingEmployee.setPhone("1234567891");
        existingEmployee.setStatus(false);
        existingEmployee.setAddress("123 Main St");

        Department existingDepartment = new Department();
        existingDepartment.setId(departmentId);
        existingDepartment.setName("IT");

        EmployeeRequest request = new EmployeeRequest();
        request.setDepartmentId(departmentId.toString());
        request.setFirstName("Jane");
        request.setLastName("Smith");
        request.setEmail("jane.smith@example.com");
        request.setPhone("1234567890");
        request.setStatus(true);
        request.setAddress("123 Main St");

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(existingEmployee));
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(existingDepartment));
        when(employeeRepository.save(existingEmployee)).thenReturn(existingEmployee);

        ApiResponse response = employeeService.updateEmployee(employeeId, request);
        Optional<Employee> optionalEmployee = (Optional<Employee>) response.getData();
        assertEquals("00", response.getCode());
        assertEquals("Success", response.getMessage());
        optionalEmployee.ifPresent(employee -> assertEquals(existingEmployee, employee));
    }

    @Test
    void testUpdateEmployee_EmployeeNotFound() {
        Long employeeId = 1L;

        EmployeeRequest request = new EmployeeRequest();
        request.setFirstName("Jane");

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

        ApiResponse response = employeeService.updateEmployee(employeeId, request);

        assertEquals("90", response.getCode());
        assertEquals("Employee not found", response.getMessage());
    }

    @Test
    void testUpdateEmployee_InvalidDepartmentId() {
        Long employeeId = 1L;

        Employee existingEmployee = new Employee();
        existingEmployee.setId(employeeId);
        existingEmployee.setFirstName("John");
        existingEmployee.setLastName("Doe");

        EmployeeRequest request = new EmployeeRequest();
        request.setDepartmentId("invalid");
        request.setFirstName("Jane");

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(existingEmployee));

        ApiResponse response = employeeService.updateEmployee(employeeId, request);

        assertEquals("99", response.getCode());
        assertEquals("Unable to update employee, pass in correct data", response.getMessage());
    }

    @Test
    void testUpdateEmployee_GeneralException() {
        Long employeeId = 1L;

        Employee existingEmployee = new Employee();
        existingEmployee.setId(employeeId);
        existingEmployee.setFirstName("John");
        existingEmployee.setLastName("Doe");

        EmployeeRequest request = new EmployeeRequest();
        request.setFirstName("Jane");

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(existingEmployee));
        when(employeeRepository.save(existingEmployee)).thenThrow(new RuntimeException("Database error"));

        ApiResponse response = employeeService.updateEmployee(employeeId, request);

        assertEquals("99", response.getCode());
        assertEquals("Unable to update employee, try again later", response.getMessage());
    }

    @Test
    void testDeleteEmployee_Success() {
        Long employeeId = 1L;

        Employee existingEmployee = new Employee();
        existingEmployee.setId(employeeId);

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(existingEmployee));

        ApiResponse response = employeeService.deleteEmployee(employeeId);

        assertEquals("00", response.getCode());
        assertEquals("Success", response.getMessage());

        verify(employeeRepository, times(1)).delete(existingEmployee);
    }

    @Test
    void testDeleteEmployee_EmployeeNotFound() {
        Long employeeId = 1L;

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

        ApiResponse response = employeeService.deleteEmployee(employeeId);

        assertEquals("90", response.getCode());
        assertEquals("Employee not found", response.getMessage());

        verify(employeeRepository, never()).delete(any(Employee.class));
    }

    @Test
    void testDeleteEmployee_GeneralException() {
        Long employeeId = 1L;

        Employee existingEmployee = new Employee();
        existingEmployee.setId(employeeId);

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(existingEmployee));
        doThrow(new RuntimeException("Database error")).when(employeeRepository).delete(existingEmployee);

        ApiResponse response = employeeService.deleteEmployee(employeeId);

        assertEquals("99", response.getCode());
        assertEquals("Unable to delete employee, try again later", response.getMessage());

        verify(employeeRepository, times(1)).delete(existingEmployee);
    }

}
