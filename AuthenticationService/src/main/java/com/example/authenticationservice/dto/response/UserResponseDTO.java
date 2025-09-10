package com.example.authenticationservice.dto.response;

import lombok.Data;

@Data
public class UserResponseDTO {
    private Long id;
    private String username;
    private String email;
    private String passwordHash; // dùng cho login check
}
