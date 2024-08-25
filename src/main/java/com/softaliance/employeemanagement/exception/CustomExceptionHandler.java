package com.softaliance.employeemanagement.exception;

import com.softaliance.employeemanagement.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> Exception(Exception ex) {
        ApiResponse apiResponse = new ApiResponse("99",
                "An unexpected error occurred, try again later",
                null);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
    }

    @ExceptionHandler(InsufficientAuthenticationException.class)
    public ResponseEntity<ApiResponse> handleInsufficientAuthenticationException(InsufficientAuthenticationException ex) {
        ApiResponse apiResponse = new ApiResponse("99", "Access Denied", null);

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        String fieldName;
        String errorMessage;
        for (int i = 0; i < ex.getBindingResult().getAllErrors().size(); i++) {
            fieldName = ((FieldError) ex.getBindingResult().getAllErrors().get(i)).getField();
            errorMessage = ex.getBindingResult().getAllErrors().get(i).getDefaultMessage();
            errors.put(fieldName, errorMessage);
        }
        ApiResponse apiResponse = ApiResponse.builder()
                .code("90")
                .message("Validation Failed")
                .data(errors)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);

    }
}
