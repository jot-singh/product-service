package com.dag.productservice.dao.schema;

import com.dag.productservice.models.OrderStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderStatusHistoryRepository extends JpaRepository<OrderStatusHistory, UUID> {

    List<OrderStatusHistory> findByOrderId(UUID orderId);

    List<OrderStatusHistory> findByOrderIdOrderByChangedOnDesc(UUID orderId);
}
