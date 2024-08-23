package com.softaliance.employeemanagement.repository;

import com.softaliance.employeemanagement.models.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
}
