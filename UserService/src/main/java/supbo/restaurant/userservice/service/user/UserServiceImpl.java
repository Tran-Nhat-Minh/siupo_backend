package supbo.restaurant.userservice.service.user;

import supbo.restaurant.userservice.client.OrderClient;
import supbo.restaurant.userservice.dto.request.UserRegisterRequestDTO;
import supbo.restaurant.userservice.dto.request.UserRequest;
import supbo.restaurant.userservice.dto.response.OrderResponse;
import supbo.restaurant.userservice.dto.response.UserResponse;
import supbo.restaurant.userservice.model.User;
import supbo.restaurant.userservice.exception.DuplicateEmailException;
import supbo.restaurant.userservice.exception.UserNotFoundException;
import supbo.restaurant.userservice.repository.UserRepository;
import supbo.restaurant.userservice.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final OrderClient orderClient;

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        log.info("Getting user by id: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Không tìm thấy user với id: " + id));

        return mapToResponse(user, null);
    }
    @Override
    public UserResponse createUserFromAuth(UserRegisterRequestDTO request) {
        log.info("Creating user from auth service with email: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException("Email đã tồn tại: " + request.getEmail());
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .username(request.getUsername())
                .passwordHash(request.getPassword()) // Password đã được mã hóa từ Auth Service
                .email(request.getEmail())
                .phone(request.getPhone()) // phoneNumber -> phone
                .birthDate(null) // Có thể để null hoặc set default
                .build();

        User savedUser = userRepository.save(user);
        log.info("User created successfully from auth service with id: {}", savedUser.getId());

        return mapToResponse(savedUser, null);
    }
    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserWithOrders(Long id) {
        log.info("Getting user with orders by id: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Không tìm thấy user với id: " + id));

        try {
            // Call Order Service to get orders
            List<OrderResponse> orders = orderClient.getOrdersByCustomerId(id);
            log.info("Found {} orders for user {}", orders.size(), id);

            return mapToResponse(user, orders);
        } catch (Exception e) {
            log.error("Error fetching orders for user {}: {}", id, e.getMessage());
            return mapToResponse(user, List.of());
        }
    }


    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        log.info("Getting all users");

        return userRepository.findAll().stream()
                .map(user -> mapToResponse(user, null))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {
        log.info("Getting user by email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Không tìm thấy user với email: " + email));

        // DEBUG: Kiểm tra dữ liệu từ DB
        System.out.println("=== USER FROM DB ===");
        System.out.println("User ID: " + user.getId());
        System.out.println("Username: " + user.getUsername());
        System.out.println("PasswordHash from DB: '" + user.getPasswordHash() + "'");
        System.out.println("PasswordHash is null: " + (user.getPasswordHash() == null));

        UserResponse response = mapToResponse(user, null);

        // DEBUG: Kiểm tra response
        System.out.println("=== RESPONSE TO AUTH SERVICE ===");
        System.out.println("Response PasswordHash: '" + response.getPasswordHash() + "'");
        System.out.println("Response PasswordHash is null: " + (response.getPasswordHash() == null));

        return response;
    }


    private UserResponse mapToResponse(User user, List<OrderResponse> orders) {
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())  // Đổi từ getName()
                .username(user.getUsername())  // Thêm mới
                .email(user.getEmail())
                .phone(user.getPhone())
                .birthDate(user.getBirthDate()) // Thêm mới
                .passwordHash(user.getPasswordHash()) // ← THÊM DÒNG NÀY
                .orders(orders)
                .build();
    }
}