package com.supbo.restaurant.orderservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

    private Long id;
    private Long customerId;
    private LocalDateTime orderDateTime;
    private String orderStatus;
    private String orderStatusDisplay;
    private BigDecimal totalAmount;
    private String notes;
    private String deliveryAddress;
    private String phone;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserResponse customer;
}