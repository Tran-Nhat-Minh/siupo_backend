package com.supbo.restaurant.orderservice.repository;

import com.supbo.restaurant.orderservice.model.Order;
import com.supbo.restaurant.orderservice.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByCustomerId(Long customerId);

    Page<Order> findByCustomerId(Long customerId, Pageable pageable);

    List<Order> findByOrderStatus(OrderStatus status);

    Page<Order> findByOrderStatus(OrderStatus status, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.orderDateTime BETWEEN :startDate AND :endDate")
    List<Order> findByOrderDateTimeBetween(@Param("startDate") LocalDateTime startDate,
                                           @Param("endDate") LocalDateTime endDate);

    @Query("SELECT o FROM Order o WHERE o.customerId = :customerId AND o.orderStatus = :status")
    List<Order> findByCustomerIdAndStatus(@Param("customerId") Long customerId,
                                          @Param("status") OrderStatus status);

    @Query("SELECT o FROM Order o WHERE o.customerId = :customerId AND o.orderStatus IN :statuses")
    List<Order> findByCustomerIdAndStatusIn(@Param("customerId") Long customerId,
                                            @Param("statuses") List<OrderStatus> statuses);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.customerId = :customerId")
    Long countByCustomerId(@Param("customerId") Long customerId);

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.customerId = :customerId AND o.orderStatus = 'DELIVERED'")
    java.math.BigDecimal getTotalSpentByCustomer(@Param("customerId") Long customerId);
}