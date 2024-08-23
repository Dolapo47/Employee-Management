package com.softaliance.employeemanagement.utils;

import com.softaliance.employeemanagement.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class Utilities {

    private final BCryptPasswordEncoder passwordEncoder;

    public Utilities(BCryptPasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public ResponseEntity<ApiResponse> getApiResponseResponseEntity(ApiResponse apiResponse) {
        if(apiResponse.getCode().equals("00")){
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        }else if(apiResponse.getCode().equals("90")){
            return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    public String encodePassword(String plainPassword){
        return passwordEncoder.encode(plainPassword);
    }
}
