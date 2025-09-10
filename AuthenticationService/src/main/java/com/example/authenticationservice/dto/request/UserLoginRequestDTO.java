package com.example.authenticationservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserLoginRequestDTO {

    @NotBlank(message = "Email không được để trống")
    private String email;

    @NotBlank(message = "Password không được để trống")
    private String password;
}