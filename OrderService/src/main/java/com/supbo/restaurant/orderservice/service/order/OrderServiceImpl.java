package com.supbo.restaurant.orderservice.service.order;

import com.supbo.restaurant.orderservice.client.UserClient;
import com.supbo.restaurant.orderservice.dto.request.CreateOrderRequest;
import com.supbo.restaurant.orderservice.dto.request.UpdateOrderRequest;
import com.supbo.restaurant.orderservice.dto.response.OrderResponse;
import com.supbo.restaurant.orderservice.dto.response.UserResponse;
import com.supbo.restaurant.orderservice.model.Order;
import com.supbo.restaurant.orderservice.enums.OrderStatus;
import com.supbo.restaurant.orderservice.exception.CustomerNotFoundException;
import com.supbo.restaurant.orderservice.exception.InvalidOrderStatusException;
import com.supbo.restaurant.orderservice.exception.OrderNotFoundException;
import com.supbo.restaurant.orderservice.repository.OrderRepository;
import com.supbo.restaurant.orderservice.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserClient userClient;

    @Override
    public OrderResponse createOrder(CreateOrderRequest request) {
        log.info("Creating order for customer: {}", request.getCustomerId());

        // Validate customer exists by calling User Service
        UserResponse customer = validateCustomerExists(request.getCustomerId());

        Order order = Order.builder()
                .customerId(request.getCustomerId())
                .totalAmount(request.getTotalAmount())
                .notes(request.getNotes())
                .deliveryAddress(request.getDeliveryAddress())
                .phone(request.getPhone())
                .build();

        Order savedOrder = orderRepository.save(order);
        log.info("Order created successfully with id: {} for customer: {} - {}",
                savedOrder.getId(), customer.getId(), customer.getName());

        return mapToResponse(savedOrder, customer);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {
        log.info("Getting order by id: {}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Không tìm thấy order với id: " + id));

        return mapToResponse(order, null);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderByIdWithCustomer(Long id) {
        log.info("Getting order with customer by id: {}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Không tìm thấy order với id: " + id));

        UserResponse customer = getCustomerInfo(order.getCustomerId());
        return mapToResponse(order, customer);
    }

    @Override
    public OrderResponse updateOrder(Long id, UpdateOrderRequest request) {
        log.info("Updating order with id: {}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Không tìm thấy order với id: " + id));

        // Validate order status transition
        if (request.getOrderStatus() != null) {
            validateStatusTransition(order.getOrderStatus(), request.getOrderStatus());
            order.setOrderStatus(request.getOrderStatus());
        }

        if (request.getTotalAmount() != null) {
            order.setTotalAmount(request.getTotalAmount());
        }
        if (request.getNotes() != null) {
            order.setNotes(request.getNotes());
        }
        if (request.getDeliveryAddress() != null) {
            order.setDeliveryAddress(request.getDeliveryAddress());
        }
        if (request.getPhone() != null) {
            order.setPhone(request.getPhone());
        }

        Order updatedOrder = orderRepository.save(order);
        log.info("Order updated successfully with id: {}", updatedOrder.getId());

        return mapToResponse(updatedOrder, null);
    }

    @Override
    public void deleteOrder(Long id) {
        log.info("Deleting order with id: {}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Không tìm thấy order với id: " + id));

        // Only allow deletion of PENDING or CANCELLED orders
        if (order.getOrderStatus() != OrderStatus.PENDING &&
                order.getOrderStatus() != OrderStatus.CANCELLED) {
            throw new InvalidOrderStatusException(
                    "Chỉ có thể xóa order ở trạng thái PENDING hoặc CANCELLED");
        }

        orderRepository.deleteById(id);
        log.info("Order deleted successfully with id: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByCustomerId(Long customerId) {
        log.info("Getting orders by customer id: {}", customerId);

        List<Order> orders = orderRepository.findByCustomerId(customerId);
        return orders.stream()
                .map(order -> mapToResponse(order, null))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getOrdersByCustomerId(Long customerId, Pageable pageable) {
        log.info("Getting orders by customer id with pagination: {}", customerId);

        Page<Order> orders = orderRepository.findByCustomerId(customerId, pageable);
        return orders.map(order -> mapToResponse(order, null));
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByCustomerIdWithCustomer(Long customerId) {
        log.info("Getting orders with customer info by customer id: {}", customerId);

        UserResponse customer = getCustomerInfo(customerId);
        List<Order> orders = orderRepository.findByCustomerId(customerId);

        return orders.stream()
                .map(order -> mapToResponse(order, customer))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByStatus(OrderStatus status) {
        log.info("Getting orders by status: {}", status);

        List<Order> orders = orderRepository.findByOrderStatus(status);
        return orders.stream()
                .map(order -> mapToResponse(order, null))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getOrdersByStatus(OrderStatus status, Pageable pageable) {
        log.info("Getting orders by status with pagination: {}", status);

        Page<Order> orders = orderRepository.findByOrderStatus(status, pageable);
        return orders.map(order -> mapToResponse(order, null));
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Getting orders by date range: {} to {}", startDate, endDate);

        List<Order> orders = orderRepository.findByOrderDateTimeBetween(startDate, endDate);
        return orders.stream()
                .map(order -> mapToResponse(order, null))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getAllOrders(Pageable pageable) {
        log.info("Getting all orders with pagination");

        Page<Order> orders = orderRepository.findAll(pageable);
        return orders.map(order -> mapToResponse(order, null));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getAllOrdersWithCustomers(Pageable pageable) {
        log.info("Getting all orders with customer info and pagination");

        Page<Order> orders = orderRepository.findAll(pageable);
        return orders.map(order -> {
            UserResponse customer = getCustomerInfo(order.getCustomerId());
            return mapToResponse(order, customer);
        });
    }

    private UserResponse validateCustomerExists(Long customerId) {
        try {
            UserResponse customer = userClient.getUserById(customerId);
            log.info("Customer validated: {} - {}", customer.getId(), customer.getName());
            return customer;
        } catch (Exception e) {
            log.error("Customer validation failed for id: {}", customerId);
            throw new CustomerNotFoundException("Không tìm thấy customer với id: " + customerId);
        }
    }

    private UserResponse getCustomerInfo(Long customerId) {
        try {
            return userClient.getUserById(customerId);
        } catch (Exception e) {
            log.warn("Could not get customer info for id: {}", customerId);
            return null;
        }
    }

    private void validateStatusTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        // Define valid status transitions
        boolean validTransition = switch (currentStatus) {
            case PENDING -> newStatus == OrderStatus.CONFIRMED || newStatus == OrderStatus.CANCELLED;
            case CONFIRMED -> newStatus == OrderStatus.PREPARING || newStatus == OrderStatus.CANCELLED;
            case PREPARING -> newStatus == OrderStatus.READY || newStatus == OrderStatus.CANCELLED;
            case READY -> newStatus == OrderStatus.OUT_FOR_DELIVERY;
            case OUT_FOR_DELIVERY -> newStatus == OrderStatus.DELIVERED;
            case DELIVERED -> newStatus == OrderStatus.REFUNDED;
            case CANCELLED -> false; // Cannot change from cancelled
            case REFUNDED -> false;  // Cannot change from refunded
        };

        if (!validTransition) {
            throw new InvalidOrderStatusException(
                    String.format("Không thể chuyển từ trạng thái %s sang %s",
                            currentStatus.getDisplayName(), newStatus.getDisplayName()));
        }
    }

    private OrderResponse mapToResponse(Order order, UserResponse customer) {
        return OrderResponse.builder()
                .id(order.getId())
                .customerId(order.getCustomerId())
                .orderDateTime(order.getOrderDateTime())
                .orderStatus(order.getOrderStatus().name())
                .orderStatusDisplay(order.getOrderStatus().getDisplayName())
                .totalAmount(order.getTotalAmount())
                .notes(order.getNotes())
                .deliveryAddress(order.getDeliveryAddress())
                .phone(order.getPhone())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .customer(customer)
                .build();
    }
}