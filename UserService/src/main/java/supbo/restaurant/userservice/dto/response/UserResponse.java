package supbo.restaurant.userservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String fullName;
    private String username;
    private String email;
    private String phone;
    private LocalDate birthDate;
    private List<OrderResponse> orders;
    private String passwordHash; // ← THÊM DÒNG NÀY
}