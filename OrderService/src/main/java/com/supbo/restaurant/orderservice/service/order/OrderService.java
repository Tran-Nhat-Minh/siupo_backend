package com.supbo.restaurant.orderservice.service.order;

import com.supbo.restaurant.orderservice.dto.request.CreateOrderRequest;
import com.supbo.restaurant.orderservice.dto.request.UpdateOrderRequest;
import com.supbo.restaurant.orderservice.dto.response.OrderResponse;
import com.supbo.restaurant.orderservice.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderService {

    OrderResponse createOrder(CreateOrderRequest request);
    OrderResponse getOrderById(Long id);
    OrderResponse getOrderByIdWithCustomer(Long id);
    OrderResponse updateOrder(Long id, UpdateOrderRequest request);
    void deleteOrder(Long id);

    List<OrderResponse> getOrdersByCustomerId(Long customerId);
    Page<OrderResponse> getOrdersByCustomerId(Long customerId, Pageable pageable);
    List<OrderResponse> getOrdersByCustomerIdWithCustomer(Long customerId);

    List<OrderResponse> getOrdersByStatus(OrderStatus status);
    Page<OrderResponse> getOrdersByStatus(OrderStatus status, Pageable pageable);

    List<OrderResponse> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    Page<OrderResponse> getAllOrders(Pageable pageable);
    Page<OrderResponse> getAllOrdersWithCustomers(Pageable pageable);
}