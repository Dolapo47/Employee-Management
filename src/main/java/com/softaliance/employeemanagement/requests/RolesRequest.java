package com.softaliance.employeemanagement.requests;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RolesRequest {
    @NotNull(message = "name cannot be null")
    @NotEmpty(message = "Enter name")
    private String name;
    private String description;
}
