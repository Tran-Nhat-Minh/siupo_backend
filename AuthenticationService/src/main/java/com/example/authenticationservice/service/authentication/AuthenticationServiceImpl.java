package com.example.authenticationservice.service.authentication;

import com.example.authenticationservice.client.UserClient;
import com.example.authenticationservice.dto.request.UserLoginRequestDTO;
import com.example.authenticationservice.dto.request.UserRegisterRequestDTO;
import com.example.authenticationservice.dto.response.UserLoginResponseDTO;
import com.example.authenticationservice.dto.response.UserResponseDTO;
import com.example.authenticationservice.exception.BadRequestException;
import com.example.authenticationservice.security.JwtUtils;
import com.example.authenticationservice.service.mail.EmailService;
import feign.FeignException;
import jakarta.mail.MessagingException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserClient userClient;
    private final JwtUtils jwtUtils;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailService emailService;

    private final Map<String, PendingUser> pendingUsers = new ConcurrentHashMap<>();

    public AuthenticationServiceImpl(UserClient userClient,
                                     JwtUtils jwtUtils,
                                     BCryptPasswordEncoder passwordEncoder,
                                     EmailService emailService) {
        this.userClient = userClient;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Override
    public void register(UserRegisterRequestDTO registerRequest) {
        System.out.println("=== DEBUG REGISTER REQUEST ===");
        System.out.println("Username: " + registerRequest.getUsername());
        System.out.println("Email: " + registerRequest.getEmail());
        System.out.println("FullName: " + registerRequest.getFullName());
        System.out.println("PhoneNumber: " + registerRequest.getPhoneNumber());
        try {
            userClient.getUserByEmail(registerRequest.getEmail());
            // Nếu không ném lỗi => user đã tồn tại
            throw new BadRequestException("Email đã tồn tại!");
        } catch (FeignException.NotFound e) {
            // Email chưa tồn tại -> tiếp tục flow đăng ký
        }

        String otp = generateOTP();
        pendingUsers.put(registerRequest.getEmail(),
                new PendingUser(registerRequest, otp, Instant.now().plusSeconds(300)));

        try {
            emailService.sendOTPToEmail(registerRequest.getEmail(), otp);
        } catch (MessagingException e) {
            throw new BadRequestException("Gửi email OTP thất bại!");
        }
    }


    @Override
    public void confirmRegistration(String email, String otp) {
        PendingUser pendingUser = pendingUsers.get(email);
        if (pendingUser == null || pendingUser.isExpired())
            throw new BadRequestException("Yêu cầu đăng ký không tồn tại hoặc đã hết hạn!");
        if (!pendingUser.getOtp().equals(otp))
            throw new BadRequestException("OTP không đúng!");

        UserRegisterRequestDTO req = pendingUser.getRegisterRequest();

        // Debug: Kiểm tra dữ liệu trước khi gửi
        System.out.println("=== DEBUG BEFORE SENDING ===");
        System.out.println("Username: " + req.getUsername());
        System.out.println("Email: " + req.getEmail());
        System.out.println("FullName: " + req.getFullName());
        System.out.println("PhoneNumber: " + req.getPhoneNumber());
        System.out.println("Password length: " + (req.getPassword() != null ? req.getPassword().length() : "null"));

        // Mã hóa password
        req.setPassword(passwordEncoder.encode(req.getPassword()));

        // Gọi User Service
        userClient.createUser(req);

        pendingUsers.remove(email);
    }

    @Override
    public UserLoginResponseDTO login(UserLoginRequestDTO loginRequest) {
        UserResponseDTO user = userClient.getUserByEmail(loginRequest.getEmail());
        if (user == null)
            throw new com.example.authenticationservice.exception.NotFoundException("Tài khoản không tồn tại!");
        System.out.println("=== LOGIN DEBUG ===");
        System.out.println("Input password: " + loginRequest.getPassword());
        System.out.println("DB password hash: " + user.getPasswordHash());
        System.out.println("Password matches: " + passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash()));
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash()))
            throw new com.example.authenticationservice.exception.UnauthorizedException("Password không đúng!");

        String token = jwtUtils.generateJwtToken(user.getUsername());
        return new UserLoginResponseDTO(
                token,
                user.getId(),
                user.getUsername(),
                user.getEmail()
        );
    }

    private String generateOTP() {
        return String.valueOf((int) (Math.random() * 900000) + 100000);
    }

    @Getter
    @RequiredArgsConstructor
    private static class PendingUser {
        private final UserRegisterRequestDTO registerRequest;
        private final String otp;
        private final Instant expiryTime;

        public boolean isExpired() {
            return Instant.now().isAfter(expiryTime);
        }
    }
}
