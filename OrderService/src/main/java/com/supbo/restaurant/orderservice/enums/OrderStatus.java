package com.supbo.restaurant.orderservice.enums;

import lombok.Getter;

@Getter
public enum OrderStatus {
    PENDING("Đang chờ xử lý"),
    CONFIRMED("Đã xác nhận"),
    PREPARING("Đang chuẩn bị"),
    READY("Sẵn sàng"),
    OUT_FOR_DELIVERY("Đang giao hàng"),
    DELIVERED("Đã giao"),
    CANCELLED("Đã hủy"),
    REFUNDED("Đã hoàn tiền");

    private final String displayName;

    OrderStatus(String displayName) {
        this.displayName = displayName;
    }
}