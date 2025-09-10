package com.supbo.restaurant.orderservice.dto.request;

import com.supbo.restaurant.orderservice.enums.OrderStatus;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrderRequest {

    private OrderStatus orderStatus;

    @Positive(message = "Tổng tiền phải lớn hơn 0")
    private BigDecimal totalAmount;

    @Size(max = 500, message = "Ghi chú không được quá 500 ký tự")
    private String notes;

    @Size(max = 255, message = "Địa chỉ giao hàng không được quá 255 ký tự")
    private String deliveryAddress;

    @Size(max = 15, message = "Số điện thoại không được quá 15 ký tự")
    private String phone;
}