package com.supbo.restaurant.orderservice.client;
import com.supbo.restaurant.orderservice.dto.response.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "user-service",
        url = "http://localhost:8083",
        fallback = UserClientFallback.class
)
@Primary
public interface UserClient {

    @GetMapping("/api/users/{id}")
    UserResponse getUserById(@PathVariable("id") Long id);
}