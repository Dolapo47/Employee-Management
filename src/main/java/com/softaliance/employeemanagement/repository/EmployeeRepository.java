package com.softaliance.employeemanagement.repository;

import com.softaliance.employeemanagement.models.Department;
import com.softaliance.employeemanagement.models.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByEmail(String email);
    @Query("SELECT e FROM Employee e WHERE e.roles.name = :roleName")
    List<Employee> findEmployeesByRoleName(@Param("roleName") String roleName);
    List<Employee> findEmployeesByDepartment(Department department);
}
