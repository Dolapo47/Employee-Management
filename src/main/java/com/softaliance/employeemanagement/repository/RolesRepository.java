package com.softaliance.employeemanagement.repository;

import com.softaliance.employeemanagement.models.Employee;
import com.softaliance.employeemanagement.models.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RolesRepository extends JpaRepository<Roles, Long> {
}
