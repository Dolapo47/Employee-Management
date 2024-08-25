package com.softaliance.employeemanagement.seeder;

import com.softaliance.employeemanagement.models.Department;
import com.softaliance.employeemanagement.models.Employee;
import com.softaliance.employeemanagement.models.Roles;
import com.softaliance.employeemanagement.repository.DepartmentRepository;
import com.softaliance.employeemanagement.repository.EmployeeRepository;
import com.softaliance.employeemanagement.repository.RolesRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Configuration
@AllArgsConstructor
public class DataSeeder {

    private final RolesRepository rolesRepository;

    private final DepartmentRepository departmentRepository;

    private final EmployeeRepository employeeRepository;

    private final PasswordEncoder passwordEncoder;

    @Bean
    public ApplicationRunner seedData() {
        return args -> {
            // Seed Roles
            Roles adminRole = Roles.builder()
                    .name("Admin")
                    .description("Administrator role")
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            Roles employeeRole = Roles.builder()
                    .name("Employee")
                    .description("Employee role")
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            Roles managerRole = Roles.builder()
                    .name("Manager")
                    .description("Manager role")
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            if(rolesRepository.count() == 0) {
                rolesRepository.save(adminRole);
                rolesRepository.save(employeeRole);
                rolesRepository.save(managerRole);
            }


            // Seed Departments
            Department hrDepartment = Department.builder()
                    .name("HR")
                    .description("Human Resources Department")
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            Department itDepartment = Department.builder()
                    .name("IT")
                    .description("Information Technology Department")
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            if(departmentRepository.count() == 0) {
                departmentRepository.save(hrDepartment);
                departmentRepository.save(itDepartment);
            }

            // Seed Employees
            Employee adminEmployee = Employee.builder()
                    .firstName("John")
                    .lastName("Doe")
                    .phone("1234567890")
                    .email("admin@example.com")
                    .password(passwordEncoder.encode("Password@123"))
                    .address("123 Admin St")
                    .status(true)
                    .roles(adminRole)
                    .department(itDepartment)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            Employee userEmployee = Employee.builder()
                    .firstName("Jane")
                    .lastName("Doe")
                    .phone("0987654321")
                    .email("user@example.com")
                    .password(passwordEncoder.encode("Password@123"))
                    .address("456 User Ave")
                    .status(true)
                    .roles(employeeRole)
                    .department(hrDepartment)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            Employee managerEmployee = Employee.builder()
                    .firstName("Frank")
                    .lastName("Thomas")
                    .phone("1234567890")
                    .email("manager@example.com")
                    .password(passwordEncoder.encode("Password@123"))
                    .address("123 Admin St")
                    .status(true)
                    .roles(managerRole)
                    .department(hrDepartment)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            if(employeeRepository.count() == 0) {
                employeeRepository.save(adminEmployee);
                employeeRepository.save(managerEmployee);
                employeeRepository.save(userEmployee);
            }

            System.out.println("Data seeding completed!");
        };
    }
}

