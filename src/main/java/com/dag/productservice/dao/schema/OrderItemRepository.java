package com.dag.productservice.dao.schema;

import com.dag.productservice.models.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {

    List<OrderItem> findByOrderId(UUID orderId);

    @Query("SELECT oi FROM order_items oi WHERE oi.product.Id = :productId")
    List<OrderItem> findByProductId(@Param("productId") UUID productId);

    @Query("SELECT SUM(oi.totalPrice) FROM order_items oi WHERE oi.order.id = :orderId")
    java.math.BigDecimal getTotalAmountByOrderId(@Param("orderId") UUID orderId);
}
