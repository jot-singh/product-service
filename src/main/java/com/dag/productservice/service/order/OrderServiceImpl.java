package com.dag.productservice.service.order;

import com.dag.productservice.dao.schema.OrderRepository;
import com.dag.productservice.dao.schema.OrderItemRepository;
import com.dag.productservice.dao.schema.OrderStatusHistoryRepository;
import com.dag.productservice.dao.schema.LocalProductRepository;
import com.dag.productservice.dto.OrderRequestDto;
import com.dag.productservice.dto.OrderResponseDto;
import com.dag.productservice.dto.OrderStatusUpdateRequestDto;
import com.dag.productservice.exception.OrderNotFoundException;
import com.dag.productservice.exception.ProductNotFoundException;
import com.dag.productservice.models.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderStatusHistoryRepository statusHistoryRepository;
    private final LocalProductRepository productRepository;

    @Override
    public OrderResponseDto createOrder(OrderRequestDto request) {
        log.info("Creating order for customer: {}", request.getCustomerId());

        // Validate products and calculate totals
        List<OrderItem> orderItems = createOrderItems(request);
        BigDecimal totalAmount = calculateTotalAmount(orderItems);

        // Create order
        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());
        order.setCustomerId(request.getCustomerId());
        order.setStatus(Order.OrderStatus.PENDING);
        order.setTotalAmount(totalAmount);
        order.setCurrency(request.getCurrency() != null ? request.getCurrency() : "USD");
        order.setShippingAddress(request.getShippingAddress());
        order.setBillingAddress(request.getBillingAddress());
        order.setOrderDate(LocalDateTime.now());
        order.setOrderItems(orderItems);

        // Save order
        Order savedOrder = orderRepository.save(order);

        // Update order items with order reference
        for (OrderItem item : orderItems) {
            item.setOrder(savedOrder);
        }
        orderItemRepository.saveAll(orderItems);

        // Add initial status history
        addOrderStatusHistory(savedOrder, null, Order.OrderStatus.PENDING, "Order created");

        log.info("Order created successfully with ID: {} and order number: {}",
                savedOrder.getId(), savedOrder.getOrderNumber());

        return mapToResponseDto(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponseDto getOrderById(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + orderId));
        return mapToResponseDto(order);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponseDto getOrderByOrderNumber(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with order number: " + orderNumber));
        return mapToResponseDto(order);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponseDto> getCustomerOrders(String customerId, Pageable pageable) {
        Page<Order> orders = orderRepository.findByCustomerId(customerId, pageable);
        return orders.map(this::mapToResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getCustomerOrders(String customerId) {
        List<Order> orders = orderRepository.findByCustomerId(customerId);
        return orders.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponseDto updateOrderStatus(UUID orderId, OrderStatusUpdateRequestDto request) {
        log.info("Updating order status for order ID: {} to status: {}", orderId, request.getStatus());

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + orderId));

        Order.OrderStatus oldStatus = order.getStatus();
        order.setStatus(request.getStatus());

        // Update specific dates based on status
        if (request.getStatus() == Order.OrderStatus.SHIPPED) {
            order.setShippedDate(LocalDateTime.now());
        } else if (request.getStatus() == Order.OrderStatus.DELIVERED) {
            order.setDeliveredDate(LocalDateTime.now());
        }

        Order updatedOrder = orderRepository.save(order);

        // Add status history
        addOrderStatusHistory(updatedOrder, oldStatus, request.getStatus(), request.getNotes());

        log.info("Order status updated successfully for order ID: {}", orderId);

        return mapToResponseDto(updatedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getOrdersByStatus(Order.OrderStatus status) {
        List<Order> orders = orderRepository.findByStatus(status);
        return orders.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getCustomerOrdersByStatus(String customerId, Order.OrderStatus status) {
        List<Order> orders = orderRepository.findByCustomerIdAndStatus(customerId, status);
        return orders.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public void cancelOrder(UUID orderId, String reason) {
        log.info("Cancelling order with ID: {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + orderId));

        if (order.getStatus() == Order.OrderStatus.DELIVERED ||
            order.getStatus() == Order.OrderStatus.CANCELLED) {
            throw new IllegalStateException("Cannot cancel order with status: " + order.getStatus());
        }

        Order.OrderStatus oldStatus = order.getStatus();
        order.setStatus(Order.OrderStatus.CANCELLED);

        orderRepository.save(order);
        addOrderStatusHistory(order, oldStatus, Order.OrderStatus.CANCELLED, reason);

        log.info("Order cancelled successfully with ID: {}", orderId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDto.OrderStatusHistoryDto> getOrderStatusHistory(UUID orderId) {
        List<OrderStatusHistory> history = statusHistoryRepository.findByOrderIdOrderByChangedOnDesc(orderId);
        return history.stream()
                .map(this::mapStatusHistoryToDto)
                .collect(Collectors.toList());
    }

    private List<OrderItem> createOrderItems(OrderRequestDto request) {
        return request.getItems().stream()
                .map(item -> {
                    Product product = productRepository.findById(UUID.fromString(item.getProductId()))
                            .orElseThrow(() -> new ProductNotFoundException(
                                    "Product not found with id: " + item.getProductId()));

                    OrderItem orderItem = new OrderItem();
                    orderItem.setProduct(product);
                    orderItem.setQuantity(item.getQuantity());
                    orderItem.setUnitPrice(BigDecimal.valueOf(product.getPrice().getPrice()));
                    orderItem.setTotalPrice(BigDecimal.valueOf(product.getPrice().getPrice())
                            .multiply(BigDecimal.valueOf(item.getQuantity())));

                    return orderItem;
                })
                .collect(Collectors.toList());
    }

    private BigDecimal calculateTotalAmount(List<OrderItem> orderItems) {
        return orderItems.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private String generateOrderNumber() {
        return "ORD-" + System.currentTimeMillis() + "-" +
               String.format("%04d", (int)(Math.random() * 10000));
    }

    private void addOrderStatusHistory(Order order, Order.OrderStatus oldStatus,
                                     Order.OrderStatus newStatus, String notes) {
        OrderStatusHistory history = new OrderStatusHistory();
        history.setOrder(order);
        history.setOldStatus(oldStatus != null ? oldStatus.name() : null);
        history.setNewStatus(newStatus.name());
        history.setChangedOn(LocalDateTime.now());
        history.setNotes(notes);

        statusHistoryRepository.save(history);
    }

    private OrderResponseDto mapToResponseDto(Order order) {
        return OrderResponseDto.builder()
                .id(order.getId().toString())
                .orderNumber(order.getOrderNumber())
                .customerId(order.getCustomerId())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .currency(order.getCurrency())
                .shippingAddress(order.getShippingAddress())
                .billingAddress(order.getBillingAddress())
                .orderDate(order.getOrderDate())
                .shippedDate(order.getShippedDate())
                .deliveredDate(order.getDeliveredDate())
                .createdOn(order.getCreatedOn())
                .modifiedOn(order.getModifiedOn())
                .items(order.getOrderItems() != null ?
                       order.getOrderItems().stream()
                           .map(this::mapOrderItemToDto)
                           .collect(Collectors.toList()) : null)
                .statusHistory(order.getStatusHistory() != null ?
                              order.getStatusHistory().stream()
                                  .map(this::mapStatusHistoryToDto)
                                  .collect(Collectors.toList()) : null)
                .build();
    }

    private OrderResponseDto.OrderItemResponseDto mapOrderItemToDto(OrderItem item) {
        return OrderResponseDto.OrderItemResponseDto.builder()
                .id(item.getId().toString())
                .productId(item.getProduct().getId().toString())
                .productName(item.getProduct().getName())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .totalPrice(item.getTotalPrice())
                .build();
    }

    private OrderResponseDto.OrderStatusHistoryDto mapStatusHistoryToDto(OrderStatusHistory history) {
        return OrderResponseDto.OrderStatusHistoryDto.builder()
                .id(history.getId().toString())
                .oldStatus(history.getOldStatus())
                .newStatus(history.getNewStatus())
                .changedBy(history.getChangedBy())
                .changedOn(history.getChangedOn())
                .notes(history.getNotes())
                .build();
    }
}
