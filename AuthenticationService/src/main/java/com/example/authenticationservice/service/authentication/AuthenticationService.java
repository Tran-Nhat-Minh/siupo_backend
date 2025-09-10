package com.example.authenticationservice.service.authentication;

import com.example.authenticationservice.dto.request.UserLoginRequestDTO;
import com.example.authenticationservice.dto.request.UserRegisterRequestDTO;
import com.example.authenticationservice.dto.response.UserLoginResponseDTO;

public interface AuthenticationService {
    UserLoginResponseDTO login(UserLoginRequestDTO loginRequest);

    void  register(UserRegisterRequestDTO registerRequest);
    void confirmRegistration(String email, String otp);


}
