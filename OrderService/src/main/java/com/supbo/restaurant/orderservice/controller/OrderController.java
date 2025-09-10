package com.supbo.restaurant.orderservice.controller;

import com.supbo.restaurant.orderservice.dto.request.CreateOrderRequest;
import com.supbo.restaurant.orderservice.dto.request.UpdateOrderRequest;
import com.supbo.restaurant.orderservice.dto.response.OrderResponse;
import com.supbo.restaurant.orderservice.enums.OrderStatus;
import com.supbo.restaurant.orderservice.service.order.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("api/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        log.info("Creating order for customer: {}", request.getCustomerId());
        OrderResponse response = orderService.createOrder(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        log.info("Getting order by id: {}", id);
        OrderResponse response = orderService.getOrderById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/with-customer")
    public ResponseEntity<OrderResponse> getOrderByIdWithCustomer(@PathVariable Long id) {
        log.info("Getting order with customer by id: {}", id);
        OrderResponse response = orderService.getOrderByIdWithCustomer(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderResponse> updateOrder(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrderRequest request) {
        log.info("Updating order with id: {}", id);
        OrderResponse response = orderService.updateOrder(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        log.info("Deleting order with id: {}", id);
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    // Get orders by customer ID (endpoint for User Service to call)
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OrderResponse>> getOrdersByCustomerId(@PathVariable Long customerId) {
        log.info("Getting orders by customer id: {}", customerId);
        List<OrderResponse> response = orderService.getOrdersByCustomerId(customerId);
        return ResponseEntity.ok(response);
    }

    // Get orders by customer ID with customer info
    @GetMapping("/customer/{customerId}/with-customer")
    public ResponseEntity<List<OrderResponse>> getOrdersByCustomerIdWithCustomer(@PathVariable Long customerId) {
        log.info("Getting orders with customer info by customer id: {}", customerId);
        List<OrderResponse> response = orderService.getOrdersByCustomerIdWithCustomer(customerId);
        return ResponseEntity.ok(response);
    }

    // Paginated orders by customer ID
    @GetMapping("/customer/{customerId}/paginated")
    public ResponseEntity<Page<OrderResponse>> getOrdersByCustomerIdPaginated(
            @PathVariable Long customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "orderDateTime") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        log.info("Getting orders by customer id with pagination: {}", customerId);
        Page<OrderResponse> response = orderService.getOrdersByCustomerId(customerId, pageable);
        return ResponseEntity.ok(response);
    }

    // Get orders by status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrderResponse>> getOrdersByStatus(@PathVariable OrderStatus status) {
        log.info("Getting orders by status: {}", status);
        List<OrderResponse> response = orderService.getOrdersByStatus(status);
        return ResponseEntity.ok(response);
    }

    // Paginated orders by status
    @GetMapping("/status/{status}/paginated")
    public ResponseEntity<Page<OrderResponse>> getOrdersByStatusPaginated(
            @PathVariable OrderStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "orderDateTime") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        log.info("Getting orders by status with pagination: {}", status);
        Page<OrderResponse> response = orderService.getOrdersByStatus(status, pageable);
        return ResponseEntity.ok(response);
    }

    // Get orders by date range
    @GetMapping("/date-range")
    public ResponseEntity<List<OrderResponse>> getOrdersByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        log.info("Getting orders by date range: {} to {}", startDate, endDate);
        List<OrderResponse> response = orderService.getOrdersByDateRange(startDate, endDate);
        return ResponseEntity.ok(response);
    }

    // Get all orders with pagination
    @GetMapping
    public ResponseEntity<Page<OrderResponse>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "orderDateTime") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(defaultValue = "false") boolean includeCustomer) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        log.info("Getting all orders with pagination: page={}, size={}, includeCustomer={}",
                page, size, includeCustomer);

        Page<OrderResponse> response = includeCustomer ?
                orderService.getAllOrdersWithCustomers(pageable) :
                orderService.getAllOrders(pageable);

        return ResponseEntity.ok(response);
    }
}