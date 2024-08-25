package com.softaliance.employeemanagement.services;

import com.softaliance.employeemanagement.models.Department;
import com.softaliance.employeemanagement.models.Employee;
import com.softaliance.employeemanagement.repository.DepartmentRepository;
import com.softaliance.employeemanagement.repository.EmployeeRepository;
import com.softaliance.employeemanagement.requests.DepartmentRequest;
import com.softaliance.employeemanagement.responses.ApiResponse;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


class DepartmentServiceTest {
    // Service to test
    @InjectMocks
    private DepartmentService departmentService;
    // Dependencies
    @Mock
    private DepartmentRepository departmentRepository;
    @Mock
    private EmployeeRepository employeeRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void should_create_department() {
        // Given
        DepartmentRequest request = new DepartmentRequest("HR", "HR Department");
        Department department = Department.builder()
                .name("HR Department")
                .id(1L)
                .description("HR Department Description")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Mockito.when(departmentRepository.save(Mockito.any(Department.class))).thenReturn(department);
        // When
        ApiResponse apiResponse = departmentService.createDepartment(request);
        // Then
        assertNotNull(apiResponse);
        assertNotNull(apiResponse.getData());
        assertEquals("00", apiResponse.getCode());
        assertEquals("Success", apiResponse.getMessage());
        assertEquals(department, apiResponse.getData());
    }

    @Test
    public void should_return_error_when_data_integrity_violation_occurs() {
        // Given
        DepartmentRequest request = new DepartmentRequest("HR", "HR Department");

        Mockito.when(departmentRepository.save(Mockito.any(Department.class)))
                .thenThrow(new DataIntegrityViolationException("Duplicate entry"));

        ApiResponse expectedResponse = ApiResponse.builder()
                .code("99")
                .message("Unable to create department, department details has been used")
                .build();

        // When
        ApiResponse actualResponse = departmentService.createDepartment(request);

        // Then
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getCode(), actualResponse.getCode());
        assertEquals(expectedResponse.getMessage(), actualResponse.getMessage());
        assertNull(actualResponse.getData());
    }

    @Test
    public void should_return_error_when_general_exception_occurs() {
        // Given
        DepartmentRequest request = new DepartmentRequest("HR", "HR Department");

        Mockito.when(departmentRepository.save(Mockito.any(Department.class)))
                .thenThrow(new RuntimeException("Unable to save department"));

        ApiResponse expectedResponse = ApiResponse.builder()
                .code("99")
                .message("Unable to create departments, try again later")
                .build();

        // When
        ApiResponse actualResponse = departmentService.createDepartment(request);

        // Then
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getCode(), actualResponse.getCode());
        assertEquals(expectedResponse.getMessage(), actualResponse.getMessage());
        assertNull(actualResponse.getData());
    }

    @Test
    public void should_return_error_when_null_request_is_passed() {
        // Given
        DepartmentRequest request = null;

        ApiResponse expectedResponse = ApiResponse.builder()
                .code("99")
                .message("Unable to create departments, try again later")
                .build();

        // When
        ApiResponse actualResponse = departmentService.createDepartment(request);

        // Then
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getCode(), actualResponse.getCode());
        assertEquals(expectedResponse.getMessage(), actualResponse.getMessage());
        assertNull(actualResponse.getData());
    }

    @Test
    public void should_return_success_when_department_found() {
        // Given
        Long departmentId = 1L;
        Department department = new Department();
        department.setId(departmentId);
        Mockito.when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(department));

        // When
        ApiResponse response = departmentService.getDepartment(departmentId);

        // Then
        assertNotNull(response);
        assertEquals("00", response.getCode());
        assertEquals("Success", response.getMessage());
        assertEquals(department, response.getData());
    }

    @Test
    public void should_return_error_when_department_not_found() {
        // Given
        Long departmentId = 1L;
        Mockito.when(departmentRepository.findById(departmentId)).thenReturn(Optional.empty());

        // When
        ApiResponse response = departmentService.getDepartment(departmentId);

        // Then
        assertNotNull(response);
        assertEquals("90", response.getCode());
        assertEquals("Unable to retrieve department", response.getMessage());
        assertNull(response.getData());
    }

    @Test
    public void should_return_error_when_exception_is_thrown() {
        // Given
        Long departmentId = 1L;
        Mockito.when(departmentRepository.findById(departmentId)).thenThrow(new RuntimeException("Database error"));

        // When
        ApiResponse response = departmentService.getDepartment(departmentId);

        // Then
        assertNotNull(response);
        assertEquals("99", response.getCode());
        assertEquals("Unable to retrieve department, try again later", response.getMessage());
        assertNull(response.getData());
    }

    @Test
    public void should_return_success_when_departments_are_retrieved() {
        // Given
        List<Department> departments = List.of(
                new Department(1L, "HR", "HR Department", LocalDateTime.now(), LocalDateTime.now(), new HashSet<>()),
                new Department(2L, "Finance", "Finance Department", LocalDateTime.now(), LocalDateTime.now(), new HashSet<>())
        );
        Mockito.when(departmentRepository.findAll()).thenReturn(departments);

        // When
        ApiResponse apiResponse = departmentService.getAllDepartments();

        // Then
        assertNotNull(apiResponse);
        assertEquals("00", apiResponse.getCode());
        assertEquals("Success", apiResponse.getMessage());
        assertEquals(departments, apiResponse.getData());
    }

    @Test
    public void should_return_success_with_empty_list_when_no_departments_found() {
        // Given
        List<Department> departments = Collections.emptyList();
        Mockito.when(departmentRepository.findAll()).thenReturn(departments);

        // When
        ApiResponse apiResponse = departmentService.getAllDepartments();

        // Then
        assertNotNull(apiResponse);
        assertEquals("00", apiResponse.getCode());
        assertEquals("Success", apiResponse.getMessage());
        assertEquals(departments, apiResponse.getData());
    }

    @Test
    public void should_return_error_when_exception_occurs() {
        // Given
        Mockito.when(departmentRepository.findAll()).thenThrow(new RuntimeException("Database is down"));

        // When
        ApiResponse apiResponse = departmentService.getAllDepartments();

        // Then
        assertNotNull(apiResponse);
        assertEquals("99", apiResponse.getCode());
        assertEquals("Unable to retrieve departments, try again later", apiResponse.getMessage());
        assertNull(apiResponse.getData());
    }

    @Test
    public void should_update_department_successfully() {
        // Given
        Long departmentId = 1L;
        DepartmentRequest request = new DepartmentRequest("New HR", "New HR Description");
        Department department = Department.builder()
                .id(departmentId)
                .name("Old HR")
                .description("Old HR Description")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Mockito.when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(department));
        Mockito.when(departmentRepository.save(department)).thenReturn(department);

        // When
        ApiResponse apiResponse = departmentService.updateDepartment(departmentId, request);

        // Then
        assertNotNull(apiResponse);
        assertEquals("00", apiResponse.getCode());
        assertEquals("Success", apiResponse.getMessage());
        assertEquals("New HR", ((Department) apiResponse.getData()).getName());
        assertEquals("New HR Description", ((Department) apiResponse.getData()).getDescription());
    }

    @Test
    public void should_return_error_when_department_not_found_for_update_department() {
        // Given
        Long departmentId = 1L;
        DepartmentRequest request = new DepartmentRequest("New HR", "New HR Description");

        Mockito.when(departmentRepository.findById(departmentId)).thenReturn(Optional.empty());

        // When
        ApiResponse apiResponse = departmentService.updateDepartment(departmentId, request);

        // Then
        assertNotNull(apiResponse);
        assertEquals("90", apiResponse.getCode());
        assertEquals("Unable to retrieve department", apiResponse.getMessage());
        assertNull(apiResponse.getData());
    }

    @Test
    public void should_return_error_when_exception_occurs_during_update() {
        // Given
        Long departmentId = 1L;
        DepartmentRequest request = new DepartmentRequest("New HR", "New HR Description");
        Department department = Department.builder()
                .id(departmentId)
                .name("Old HR")
                .description("Old HR Description")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Mockito.when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(department));
        Mockito.when(departmentRepository.save(department)).thenThrow(new RuntimeException("Database is down"));

        // When
        ApiResponse apiResponse = departmentService.updateDepartment(departmentId, request);

        // Then
        assertNotNull(apiResponse);
        assertEquals("99", apiResponse.getCode());
        assertEquals("Unable to update department, try again later", apiResponse.getMessage());
        assertNull(apiResponse.getData());
    }

    @Test
    public void should_return_success_when_no_update_required() {
        // Given
        Long departmentId = 1L;
        DepartmentRequest request = new DepartmentRequest("HR", "HR Department Description");
        Department department = Department.builder()
                .id(departmentId)
                .name("HR")
                .description("HR Department Description")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Mockito.when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(department));
        Mockito.when(departmentRepository.save(department)).thenReturn(department);

        // When
        ApiResponse apiResponse = departmentService.updateDepartment(departmentId, request);

        // Then
        assertNotNull(apiResponse);
        assertEquals("00", apiResponse.getCode());
        assertEquals("Success", apiResponse.getMessage());
        assertEquals(department, apiResponse.getData());
    }

    @Test
    public void should_return_success_when_department_is_deleted() {
        // Given
        Long departmentId = 1L;
        Department existingDepartment = Department.builder()
                .id(departmentId)
                .name("HR")
                .description("HR Department")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        Mockito.when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(existingDepartment));

        // When
        ApiResponse apiResponse = departmentService.deleteDepartment(departmentId);

        // Then
        assertNotNull(apiResponse);
        assertEquals("00", apiResponse.getCode());
        assertEquals("Success", apiResponse.getMessage());
        Mockito.verify(departmentRepository, Mockito.times(1)).delete(existingDepartment);
    }

    @Test
    public void should_return_error_when_department_not_found_delete() {
        // Given
        Long departmentId = 1L;
        Mockito.when(departmentRepository.findById(departmentId)).thenReturn(Optional.empty());

        // When
        ApiResponse apiResponse = departmentService.deleteDepartment(departmentId);

        // Then
        assertNotNull(apiResponse);
        assertEquals("90", apiResponse.getCode());
        assertEquals("Department not found", apiResponse.getMessage());
        Mockito.verify(departmentRepository, Mockito.never()).delete(Mockito.any(Department.class));
    }

    @Test
    public void should_return_error_when_exception_occurs_during_deletion() {
        // Given
        Long departmentId = 1L;
        Department existingDepartment = Department.builder()
                .id(departmentId)
                .name("HR")
                .description("HR Department")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        Mockito.when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(existingDepartment));
        Mockito.doThrow(new RuntimeException("Database error")).when(departmentRepository).delete(existingDepartment);

        // When
        ApiResponse apiResponse = departmentService.deleteDepartment(departmentId);

        // Then
        assertNotNull(apiResponse);
        assertEquals("99", apiResponse.getCode());
        assertEquals("Unable to delete department, try again later", apiResponse.getMessage());
    }

    @Test
    public void should_return_error_when_employee_not_found() {
        // Given
        String email = "nonexistent@example.com";
        Mockito.when(employeeRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When
        ApiResponse apiResponse = departmentService.viewEmployeesInDepartment(email);

        // Then
        assertNotNull(apiResponse);
        assertEquals("90", apiResponse.getCode());
        assertEquals("Employee not found", apiResponse.getMessage());
        Mockito.verify(departmentRepository, Mockito.never()).findById(Mockito.anyLong());
        Mockito.verify(employeeRepository, Mockito.never()).findEmployeesByDepartment(Mockito.any(Department.class));
    }

    @Test
    public void should_return_error_when_department_not_found_find_employee() {
        // Given
        String email = "employee@example.com";
        Department department = Department.builder().id(1L).build();
        Employee employee = Employee.builder().email(email).department(department).build();

        Mockito.when(employeeRepository.findByEmail(email)).thenReturn(Optional.of(employee));
        Mockito.when(departmentRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        ApiResponse apiResponse = departmentService.viewEmployeesInDepartment(email);

        // Then
        assertNotNull(apiResponse);
        assertEquals("90", apiResponse.getCode());
        assertEquals("Department not found", apiResponse.getMessage());
        Mockito.verify(employeeRepository, Mockito.never()).findEmployeesByDepartment(Mockito.any(Department.class));
    }

    @Test
    public void should_return_employees_when_department_is_found() {
        // Given
        String email = "employee@example.com";
        Department department = Department.builder().id(1L).build();
        Employee employee = Employee.builder().email(email).department(department).build();
        List<Employee> employees = List.of(
                Employee.builder().email("emp1@example.com").build(),
                Employee.builder().email("emp2@example.com").build()
        );

        Mockito.when(employeeRepository.findByEmail(email)).thenReturn(Optional.of(employee));
        Mockito.when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        Mockito.when(employeeRepository.findEmployeesByDepartment(department)).thenReturn(employees);

        // When
        ApiResponse apiResponse = departmentService.viewEmployeesInDepartment(email);

        // Then
        assertNotNull(apiResponse);
        assertEquals("00", apiResponse.getCode());
        assertEquals("Success", apiResponse.getMessage());
        assertEquals(employees, apiResponse.getData());
    }

    @Test
    public void should_return_error_when_exception_occurs_during_retrieval() {
        // Given
        String email = "employee@example.com";
        Mockito.when(employeeRepository.findByEmail(email)).thenThrow(new RuntimeException("Database error"));

        // When
        ApiResponse apiResponse = departmentService.viewEmployeesInDepartment(email);

        // Then
        assertNotNull(apiResponse);
        assertEquals("99", apiResponse.getCode());
        assertEquals("Unable to retrieve employees, try again later", apiResponse.getMessage());
        Mockito.verify(departmentRepository, Mockito.never()).findById(Mockito.anyLong());
        Mockito.verify(employeeRepository, Mockito.never()).findEmployeesByDepartment(Mockito.any(Department.class));
    }
}