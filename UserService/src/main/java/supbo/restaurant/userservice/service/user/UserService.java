package supbo.restaurant.userservice.service.user;

import supbo.restaurant.userservice.dto.request.UserRegisterRequestDTO;
import supbo.restaurant.userservice.dto.request.UserRequest;
import supbo.restaurant.userservice.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    UserResponse getUserById(Long id);
    UserResponse getUserWithOrders(Long id);
    List<UserResponse> getAllUsers();
    UserResponse getUserByEmail(String email);
    UserResponse createUserFromAuth(UserRegisterRequestDTO request); // Thêm method mới


}