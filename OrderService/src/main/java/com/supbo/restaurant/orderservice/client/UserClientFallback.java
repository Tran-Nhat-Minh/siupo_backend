package com.supbo.restaurant.orderservice.client;

import com.supbo.restaurant.orderservice.dto.response.UserResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserClientFallback implements UserClient {

    @Override
    public UserResponse getUserById(Long id) {
        log.warn("User Service is not available. Cannot validate user: {}", id);
        throw new RuntimeException("User Service is not available");
    }
}