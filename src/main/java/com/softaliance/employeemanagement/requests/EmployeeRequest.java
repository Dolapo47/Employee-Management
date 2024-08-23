package com.softaliance.employeemanagement.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class EmployeeRequest {
    @NotNull(message = "First name cannot be null")
    @NotEmpty(message = "Enter your first name")
    @JsonProperty("first_name")
    private String firstName;
    @NotNull(message = "Last name cannot be null")
    @NotEmpty(message = "Enter your last name")
    @JsonProperty("last_name")
    private String lastName;
    @NotNull(message = "Email cannot be null")
    @Email(message = "Enter a valid email")
    private String email;
    @NotNull(message = "Status cannot be null")
    private Boolean status;
    @NotNull(message = "Phone cannot be null")
    private String phone;
    @NotNull(message = "Address cannot be null")
    private String address;
    @JsonProperty("department_id")
    @NotNull(message = "Department cannot be null")
    private String departmentId;
    @NotNull(message = "Role cannot be null")
    private String roleId;
}
