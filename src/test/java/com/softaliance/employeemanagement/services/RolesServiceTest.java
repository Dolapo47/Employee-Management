package com.softaliance.employeemanagement.services;

import com.softaliance.employeemanagement.models.Roles;
import com.softaliance.employeemanagement.repository.DepartmentRepository;
import com.softaliance.employeemanagement.repository.RolesRepository;
import com.softaliance.employeemanagement.requests.RolesRequest;
import com.softaliance.employeemanagement.responses.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class RolesServiceTest {

    // Service to test
    @InjectMocks
    private RolesService rolesService;

    @Mock
    private RolesRepository rolesRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateRoles_Success() {
        // Given
        RolesRequest request = new RolesRequest("Admin", "Administrator role");
        Roles roles = new Roles();
        roles.setId(1L);
        roles.setName("Admin");
        roles.setDescription("Administrator role");

        when(rolesRepository.save(any(Roles.class))).thenReturn(roles);

        // When
        ApiResponse response = rolesService.createRoles(request);

        // Then
        assertNotNull(response);
        assertEquals("00", response.getCode());
        assertEquals("Success", response.getMessage());
        assertNotNull(response.getData());
        assertInstanceOf(Roles.class, response.getData());
        Roles savedRoles = (Roles) response.getData();
        assertEquals("Admin", savedRoles.getName());
        assertEquals("Administrator role", savedRoles.getDescription());
    }

    @Test
    void testCreateRoles_Exception() {
        // Given
        RolesRequest request = new RolesRequest("Admin", "Administrator role");
        when(rolesRepository.save(any(Roles.class))).thenThrow(new RuntimeException("Database error"));

        // When
        ApiResponse response = rolesService.createRoles(request);

        // Then
        assertNotNull(response);
        assertEquals("99", response.getCode());
        assertEquals("Unable to create role, try again later", response.getMessage());
        assertNull(response.getData());
    }

    @Test
    void testCreateRoles_UniqueConstraintViolation() {
        // Given
        RolesRequest request = new RolesRequest("Admin", "Administrator role");
        when(rolesRepository.save(any(Roles.class))).thenThrow(new DataIntegrityViolationException("Unique constraint violated"));

        // When
        ApiResponse response = rolesService.createRoles(request);

        // Then
        assertNotNull(response);
        assertEquals("99", response.getCode());
        assertEquals("Unable to create role, role details has been used", response.getMessage());
        assertNull(response.getData());
    }

    @Test
    public void getRole_RoleFound_ReturnsSuccessResponse() {
        // Given
        Long roleId = 1L;
        Roles role = new Roles();
        role.setId(roleId);
        role.setName("Admin");

        Mockito.when(rolesRepository.findById(roleId)).thenReturn(Optional.of(role));

        // When
        ApiResponse response = rolesService.getRole(roleId);

        // Then
        assertNotNull(response);
        assertEquals("00", response.getCode());
        assertEquals("Success", response.getMessage());
        assertEquals(role, response.getData());
        Mockito.verify(rolesRepository, Mockito.times(1)).findById(roleId);
    }

    @Test
    public void getRole_RoleNotFound_ReturnsErrorResponse() {
        // Given
        Long roleId = 1L;

        Mockito.when(rolesRepository.findById(roleId)).thenReturn(Optional.empty());

        // When
        ApiResponse response = rolesService.getRole(roleId);

        // Then
        assertNotNull(response);
        assertEquals("90", response.getCode());
        assertEquals("Unable to retrieve role", response.getMessage());
        assertNull(response.getData());
        Mockito.verify(rolesRepository, Mockito.times(1)).findById(roleId);
    }

    @Test
    public void getRole_ExceptionThrown_ReturnsErrorResponse() {
        // Given
        Long roleId = 1L;

        Mockito.when(rolesRepository.findById(roleId)).thenThrow(new RuntimeException("Database error"));

        // When
        ApiResponse response = rolesService.getRole(roleId);

        // Then
        assertNotNull(response);
        assertEquals("99", response.getCode());
        assertEquals("Unable to retrieve role, try again later", response.getMessage());
        assertNull(response.getData());
        Mockito.verify(rolesRepository, Mockito.times(1)).findById(roleId);
    }

    @Test
    public void getAllRoles_RolesFound_ReturnsSuccessResponse() {
        // Given
        List<Roles> rolesList = new ArrayList<>();
        Roles role1 = new Roles();
        role1.setId(1L);
        role1.setName("Admin");

        Roles role2 = new Roles();
        role2.setId(2L);
        role2.setName("User");

        rolesList.add(role1);
        rolesList.add(role2);

        Mockito.when(rolesRepository.findAll()).thenReturn(rolesList);

        // When
        ApiResponse response = rolesService.getAllRoles();

        // Then
        assertNotNull(response);
        assertEquals("00", response.getCode());
        assertEquals("Success", response.getMessage());
        assertEquals(rolesList, response.getData());
        Mockito.verify(rolesRepository, Mockito.times(1)).findAll();
    }

    @Test
    public void getAllRoles_NoRolesFound_ReturnsSuccessResponseWithEmptyList() {
        // Given
        List<Roles> rolesList = new ArrayList<>();

        Mockito.when(rolesRepository.findAll()).thenReturn(rolesList);

        // When
        ApiResponse response = rolesService.getAllRoles();

        // Then
        assertNotNull(response);
        assertEquals("00", response.getCode());
        assertEquals("Success", response.getMessage());
        assertEquals(rolesList, response.getData());
        Mockito.verify(rolesRepository, Mockito.times(1)).findAll();
    }

    @Test
    public void getAllRoles_ExceptionThrown_ReturnsErrorResponse() {
        // Given
        Mockito.when(rolesRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        // When
        ApiResponse response = rolesService.getAllRoles();

        // Then
        assertNotNull(response);
        assertEquals("99", response.getCode());
        assertEquals("Unable to retrieve roles, try again later", response.getMessage());
        assertNull(response.getData());
        Mockito.verify(rolesRepository, Mockito.times(1)).findAll();
    }

    @Test
    public void updateRole_RoleFoundAndUpdated_ReturnsSuccessResponse() {
        // Given
        Long roleId = 1L;
        RolesRequest request = new RolesRequest();
        request.setName("Updated Role");
        request.setDescription("Updated Description");

        Roles existingRole = new Roles();
        existingRole.setId(roleId);
        existingRole.setName("Old Role");
        existingRole.setDescription("Old Description");

        Mockito.when(rolesRepository.findById(roleId)).thenReturn(Optional.of(existingRole));
        Mockito.when(rolesRepository.save(existingRole)).thenReturn(existingRole);

        // When
        ApiResponse response = rolesService.updateRole(roleId, request);

        // Then
        assertNotNull(response);
        assertEquals("00", response.getCode());
        assertEquals("Success", response.getMessage());
        assertEquals(existingRole, response.getData());
        Mockito.verify(rolesRepository, Mockito.times(1)).findById(roleId);
        Mockito.verify(rolesRepository, Mockito.times(1)).save(existingRole);
    }

    @Test
    public void updateRole_RoleNotFound_ReturnsErrorResponse() {
        // Given
        Long roleId = 1L;
        RolesRequest request = new RolesRequest();
        request.setName("Updated Role");
        request.setDescription("Updated Description");

        Mockito.when(rolesRepository.findById(roleId)).thenReturn(Optional.empty());

        // When
        ApiResponse response = rolesService.updateRole(roleId, request);

        // Then
        assertNotNull(response);
        assertEquals("90", response.getCode());
        assertEquals("Unable to retrieve role", response.getMessage());
        assertNull(response.getData());
        Mockito.verify(rolesRepository, Mockito.times(1)).findById(roleId);
        Mockito.verify(rolesRepository, Mockito.never()).save(Mockito.any(Roles.class));
    }

    @Test
    public void updateRole_ExceptionThrown_ReturnsErrorResponse() {
        // Given
        Long roleId = 1L;
        RolesRequest request = new RolesRequest();
        request.setName("Updated Role");
        request.setDescription("Updated Description");

        Roles existingRole = new Roles();
        existingRole.setId(roleId);
        existingRole.setName("Old Role");
        existingRole.setDescription("Old Description");

        Mockito.when(rolesRepository.findById(roleId)).thenReturn(Optional.of(existingRole));
        Mockito.when(rolesRepository.save(existingRole)).thenThrow(new RuntimeException("Database error"));

        // When
        ApiResponse response = rolesService.updateRole(roleId, request);

        // Then
        assertNotNull(response);
        assertEquals("99", response.getCode());
        assertEquals("Unable to update role, try again later", response.getMessage());
        assertNull(response.getData());
        Mockito.verify(rolesRepository, Mockito.times(1)).findById(roleId);
        Mockito.verify(rolesRepository, Mockito.times(1)).save(existingRole);
    }

    @Test
    public void testDeleteRole_Success() {
        Long roleId = 1L;
        Roles role = new Roles();
        role.setId(roleId);

        Mockito.when(rolesRepository.findById(roleId)).thenReturn(Optional.of(role));

        ApiResponse response = rolesService.deleteRole(roleId);

        assertEquals("00", response.getCode());
        assertEquals("Success", response.getMessage());
        Mockito.verify(rolesRepository, Mockito.times(1)).delete(role);
    }

    @Test
    public void testDeleteRole_RoleNotFound() {
        Long roleId = 1L;

        Mockito.when(rolesRepository.findById(roleId)).thenReturn(Optional.empty());

        ApiResponse response = rolesService.deleteRole(roleId);

        assertEquals("90", response.getCode());
        assertEquals("Role not found", response.getMessage());
        Mockito.verify(rolesRepository, Mockito.never()).delete(Mockito.any(Roles.class));
    }

    @Test
    public void testDeleteRole_ExceptionThrown() {
        Long roleId = 1L;
        Roles role = new Roles();
        role.setId(roleId);

        Mockito.when(rolesRepository.findById(roleId)).thenReturn(Optional.of(role));
        Mockito.doThrow(new RuntimeException("Database error")).when(rolesRepository).delete(role);

        ApiResponse response = rolesService.deleteRole(roleId);

        assertEquals("99", response.getCode());
        assertEquals("Unable to delete role, try again later", response.getMessage());
    }

}
