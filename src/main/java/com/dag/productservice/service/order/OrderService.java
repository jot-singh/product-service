package com.dag.productservice.service.order;

import com.dag.productservice.dto.OrderRequestDto;
import com.dag.productservice.dto.OrderResponseDto;
import com.dag.productservice.dto.OrderStatusUpdateRequestDto;
import com.dag.productservice.models.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface OrderService {

    OrderResponseDto createOrder(OrderRequestDto request);

    OrderResponseDto getOrderById(UUID orderId);

    OrderResponseDto getOrderByOrderNumber(String orderNumber);

    Page<OrderResponseDto> getCustomerOrders(String customerId, Pageable pageable);

    List<OrderResponseDto> getCustomerOrders(String customerId);

    OrderResponseDto updateOrderStatus(UUID orderId, OrderStatusUpdateRequestDto request);

    List<OrderResponseDto> getOrdersByStatus(Order.OrderStatus status);

    List<OrderResponseDto> getCustomerOrdersByStatus(String customerId, Order.OrderStatus status);

    void cancelOrder(UUID orderId, String reason);

    List<OrderResponseDto.OrderStatusHistoryDto> getOrderStatusHistory(UUID orderId);
}
