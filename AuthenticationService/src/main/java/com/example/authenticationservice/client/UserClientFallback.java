package com.example.authenticationservice.client;

import com.example.authenticationservice.dto.request.UserRegisterRequestDTO;
import com.example.authenticationservice.dto.response.UserResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class UserClientFallback implements UserClient {

    @Override
    public UserResponseDTO getUserByUsername(String username) {
        throw new RuntimeException("User Service không khả dụng (getUserByUsername)");
    }

    @Override
    public UserResponseDTO getUserByEmail(String email) {
        throw new RuntimeException("User Service không khả dụng (getUserByEmail)");
    }

    @Override
    public UserResponseDTO createUser(UserRegisterRequestDTO request) {
        throw new RuntimeException("User Service không khả dụng (createUser)");
    }
}
