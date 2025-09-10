package com.example.authenticationservice.client;

import com.example.authenticationservice.dto.request.UserRegisterRequestDTO;
import com.example.authenticationservice.dto.response.UserResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        name = "user-service",
        url = "http://localhost:8083",  // cấu hình trong application.yml/properties
        fallback = UserClientFallback.class
)
@Primary
public interface UserClient {

    @GetMapping("/api/users/by-username/{username}")
    UserResponseDTO getUserByUsername(@PathVariable("username") String username);

    @GetMapping("/api/users/by-email/{email}")
    UserResponseDTO getUserByEmail(@PathVariable("email") String email);

    @PostMapping("/api/users/from-auth") // Đổi endpoint
    UserResponseDTO createUser(@RequestBody UserRegisterRequestDTO request);
}