package supbo.restaurant.userservice.controller;

import supbo.restaurant.userservice.client.OrderClient;
import supbo.restaurant.userservice.dto.request.UserRegisterRequestDTO;
import supbo.restaurant.userservice.dto.request.UserRequest;
import supbo.restaurant.userservice.dto.response.OrderResponse;
import supbo.restaurant.userservice.dto.response.UserResponse;
import supbo.restaurant.userservice.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final OrderClient orderClient;


    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        UserResponse response = userService.getUserById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> response = userService.getAllUsers();
        return ResponseEntity.ok(response);
    }

    // Endpoint để lấy user cùng với orders
    @GetMapping("/{id}/with-orders")
    public ResponseEntity<UserResponse> getUserWithOrders(@PathVariable Long id) {
        UserResponse response = userService.getUserWithOrders(id);
        return ResponseEntity.ok(response);
    }

    // Endpoint để lấy orders của user (giả sử userId = customerId)
    @GetMapping("/{id}/orders")
    public ResponseEntity<List<OrderResponse>> getUserOrders(@PathVariable Long id) {
        List<OrderResponse> response = orderClient.getOrdersByCustomerId(id);
        return ResponseEntity.ok(response);
    }
    // Thêm endpoint mới cho Auth Service
    @PostMapping("/from-auth") // Endpoint riêng cho Auth Service
    public ResponseEntity<UserResponse> createUserFromAuth(@Valid @RequestBody UserRegisterRequestDTO request) {
        UserResponse response = userService.createUserFromAuth(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    @GetMapping("/by-email/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email) {
        UserResponse response = userService.getUserByEmail(email);
        return ResponseEntity.ok(response);
    }

}