package com.dag.productservice.dao.schema;

import com.dag.productservice.models.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    Optional<Order> findByOrderNumber(String orderNumber);

    Page<Order> findByCustomerId(String customerId, Pageable pageable);

    List<Order> findByCustomerId(String customerId);

    @Query("SELECT o FROM orders o WHERE o.status = :status")
    List<Order> findByStatus(@Param("status") Order.OrderStatus status);

    @Query("SELECT o FROM orders o WHERE o.customerId = :customerId AND o.status = :status")
    List<Order> findByCustomerIdAndStatus(@Param("customerId") String customerId,
                                         @Param("status") Order.OrderStatus status);

    @Query("SELECT o FROM orders o WHERE o.orderDate BETWEEN :startDate AND :endDate")
    List<Order> findByOrderDateBetween(@Param("startDate") LocalDateTime startDate,
                                      @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(o) FROM orders o WHERE o.customerId = :customerId")
    Long countByCustomerId(@Param("customerId") String customerId);

    @Query("SELECT SUM(o.totalAmount) FROM orders o WHERE o.customerId = :customerId AND o.status = 'DELIVERED'")
    java.math.BigDecimal getTotalSpentByCustomer(@Param("customerId") String customerId);
}
