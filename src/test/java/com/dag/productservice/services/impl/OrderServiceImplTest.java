package com.dag.productservice.services.impl;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private OrderStatusHistoryRepository statusHistoryRepository;

    @Mock
    private LocalProductRepository productRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Order testOrder;
    private Product testProduct;
    private OrderRequestDto testOrderRequest;
    private UUID orderId;

    @BeforeEach
    void setUp() {
        orderId = UUID.randomUUID();

        // Setup test product
        Price testPrice = new Price();
        testPrice.setCurrency("USD");
        testPrice.setPrice(100.0);

        testProduct = new Product();
        testProduct.setId(UUID.randomUUID());
        testProduct.setName("Test Product");
        testProduct.setPrice(testPrice);

        // Setup test order
        testOrder = new Order();
        testOrder.setId(orderId);
        testOrder.setOrderNumber("ORD-001");
        testOrder.setCustomerId("customer-123");
        testOrder.setStatus(Order.OrderStatus.PENDING);
        testOrder.setTotalAmount(BigDecimal.valueOf(200.0));
        testOrder.setCurrency("USD");
        testOrder.setOrderDate(LocalDateTime.now());

        // Setup test order request
        testOrderRequest = OrderRequestDto.builder()
                .customerId("customer-123")
                .currency("USD")
                .shippingAddress("123 Test St")
                .billingAddress("123 Test St")
                .items(Arrays.asList(
                        OrderRequestDto.OrderItemRequestDto.builder()
                                .productId(testProduct.getId().toString())
                                .quantity(2)
                                .build()
                ))
                .build();
    }

    @Test
    void createOrder_ShouldCreateOrderSuccessfully() {
        // Given
        when(productRepository.findById(any(UUID.class))).thenReturn(Optional.of(testProduct));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        when(orderItemRepository.saveAll(any())).thenReturn(Arrays.asList());

        // When
        OrderResponseDto result = orderService.createOrder(testOrderRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getOrderNumber()).isEqualTo("ORD-001");
        assertThat(result.getCustomerId()).isEqualTo("customer-123");
        assertThat(result.getStatus()).isEqualTo(Order.OrderStatus.PENDING);
        assertThat(result.getTotalAmount()).isEqualTo(BigDecimal.valueOf(200.0));

        verify(orderRepository, times(1)).save(any(Order.class));
        verify(orderItemRepository, times(1)).saveAll(any());
        verify(statusHistoryRepository, times(1)).save(any(OrderStatusHistory.class));
    }

    @Test
    void createOrder_WithNonExistentProduct_ShouldThrowException() {
        // Given
        when(productRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> orderService.createOrder(testOrderRequest))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("Product not found");

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void getOrderById_ShouldReturnOrder() {
        // Given
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(testOrder));

        // When
        OrderResponseDto result = orderService.getOrderById(orderId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(orderId.toString());
        assertThat(result.getOrderNumber()).isEqualTo("ORD-001");

        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    void getOrderById_WithNonExistentOrder_ShouldThrowException() {
        // Given
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> orderService.getOrderById(orderId))
                .isInstanceOf(OrderNotFoundException.class)
                .hasMessageContaining("Order not found");

        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    void getOrderByOrderNumber_ShouldReturnOrder() {
        // Given
        when(orderRepository.findByOrderNumber("ORD-001")).thenReturn(Optional.of(testOrder));

        // When
        OrderResponseDto result = orderService.getOrderByOrderNumber("ORD-001");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getOrderNumber()).isEqualTo("ORD-001");

        verify(orderRepository, times(1)).findByOrderNumber("ORD-001");
    }

    @Test
    void getCustomerOrders_ShouldReturnPagedOrders() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<Order> orders = Arrays.asList(testOrder);
        Page<Order> orderPage = new PageImpl<>(orders, pageable, 1);

        when(orderRepository.findByCustomerId("customer-123", pageable)).thenReturn(orderPage);

        // When
        Page<OrderResponseDto> result = orderService.getCustomerOrders("customer-123", pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getCustomerId()).isEqualTo("customer-123");

        verify(orderRepository, times(1)).findByCustomerId("customer-123", pageable);
    }

    @Test
    void updateOrderStatus_ShouldUpdateStatusAndCreateHistory() {
        // Given
        OrderStatusUpdateRequestDto updateRequest = OrderStatusUpdateRequestDto.builder()
                .status(Order.OrderStatus.CONFIRMED)
                .notes("Order confirmed by customer")
                .build();

        Order updatedOrder = new Order();
        updatedOrder.setId(orderId);
        updatedOrder.setOrderNumber("ORD-001");
        updatedOrder.setCustomerId("customer-123");
        updatedOrder.setStatus(Order.OrderStatus.CONFIRMED);
        updatedOrder.setTotalAmount(BigDecimal.valueOf(200.0));

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(updatedOrder);

        // When
        OrderResponseDto result = orderService.updateOrderStatus(orderId, updateRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(Order.OrderStatus.CONFIRMED);

        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(statusHistoryRepository, times(1)).save(any(OrderStatusHistory.class));
    }

    @Test
    void updateOrderStatus_WithShippedStatus_ShouldSetShippedDate() {
        // Given
        OrderStatusUpdateRequestDto updateRequest = OrderStatusUpdateRequestDto.builder()
                .status(Order.OrderStatus.SHIPPED)
                .build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // When
        orderService.updateOrderStatus(orderId, updateRequest);

        // Then
        verify(orderRepository, times(1)).save(argThat(order ->
            order.getStatus() == Order.OrderStatus.SHIPPED &&
            order.getShippedDate() != null
        ));
    }

    @Test
    void cancelOrder_ShouldCancelOrderAndCreateHistory() {
        // Given
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // When
        orderService.cancelOrder(orderId, "Customer requested cancellation");

        // Then
        verify(orderRepository, times(1)).save(argThat(order ->
            order.getStatus() == Order.OrderStatus.CANCELLED
        ));
        verify(statusHistoryRepository, times(1)).save(any(OrderStatusHistory.class));
    }

    @Test
    void cancelOrder_WithDeliveredOrder_ShouldThrowException() {
        // Given
        Order deliveredOrder = new Order();
        deliveredOrder.setId(orderId);
        deliveredOrder.setStatus(Order.OrderStatus.DELIVERED);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(deliveredOrder));

        // When & Then
        assertThatThrownBy(() -> orderService.cancelOrder(orderId, "Test"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot cancel order");

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void getOrdersByStatus_ShouldReturnOrdersWithStatus() {
        // Given
        List<Order> orders = Arrays.asList(testOrder);
        when(orderRepository.findByStatus(Order.OrderStatus.PENDING)).thenReturn(orders);

        // When
        List<OrderResponseDto> result = orderService.getOrdersByStatus(Order.OrderStatus.PENDING);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(Order.OrderStatus.PENDING);

        verify(orderRepository, times(1)).findByStatus(Order.OrderStatus.PENDING);
    }

    @Test
    void getOrderStatusHistory_ShouldReturnStatusHistory() {
        // Given
        OrderStatusHistory historyItem = new OrderStatusHistory();
        historyItem.setId(UUID.randomUUID());
        historyItem.setOldStatus(null);
        historyItem.setNewStatus("PENDING");
        historyItem.setChangedOn(LocalDateTime.now());

        List<OrderStatusHistory> history = Arrays.asList(historyItem);

        when(statusHistoryRepository.findByOrderIdOrderByChangedOnDesc(orderId)).thenReturn(history);

        // When
        List<OrderResponseDto.OrderStatusHistoryDto> result = orderService.getOrderStatusHistory(orderId);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNewStatus()).isEqualTo("PENDING");

        verify(statusHistoryRepository, times(1)).findByOrderIdOrderByChangedOnDesc(orderId);
    }
}
