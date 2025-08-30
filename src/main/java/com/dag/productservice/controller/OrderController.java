package com.dag.productservice.controller;

import com.dag.productservice.dto.OrderRequestDto;
import com.dag.productservice.dto.OrderResponseDto;
import com.dag.productservice.dto.OrderStatusUpdateRequestDto;
import com.dag.productservice.models.Order;
import com.dag.productservice.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') or @securityService.isCurrentUser(#request.customerId)")
    public ResponseEntity<OrderResponseDto> createOrder(@RequestBody OrderRequestDto request) {
        log.info("Creating order for customer: {}", request.getCustomerId());
        OrderResponseDto response = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') or @securityService.canAccessOrder(#id)")
    public ResponseEntity<OrderResponseDto> getOrderById(@PathVariable UUID id) {
        log.info("Getting order by ID: {}", id);
        OrderResponseDto response = orderService.getOrderById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/number/{orderNumber}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') or @securityService.canAccessOrderByNumber(#orderNumber)")
    public ResponseEntity<OrderResponseDto> getOrderByOrderNumber(@PathVariable String orderNumber) {
        log.info("Getting order by order number: {}", orderNumber);
        OrderResponseDto response = orderService.getOrderByOrderNumber(orderNumber);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') or @securityService.isCurrentUser(#customerId)")
    public ResponseEntity<Page<OrderResponseDto>> getCustomerOrders(
            @PathVariable String customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Getting orders for customer: {} (page: {}, size: {})", customerId, page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<OrderResponseDto> response = orderService.getCustomerOrders(customerId, pageable);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<OrderResponseDto> updateOrderStatus(
            @PathVariable UUID id,
            @RequestBody OrderStatusUpdateRequestDto request) {
        log.info("Updating order status for order ID: {} to status: {}", id, request.getStatus());
        OrderResponseDto response = orderService.updateOrderStatus(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<List<OrderResponseDto>> getOrdersByStatus(@PathVariable Order.OrderStatus status) {
        log.info("Getting orders by status: {}", status);
        List<OrderResponseDto> response = orderService.getOrdersByStatus(status);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/customer/{customerId}/status/{status}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') or @securityService.isCurrentUser(#customerId)")
    public ResponseEntity<List<OrderResponseDto>> getCustomerOrdersByStatus(
            @PathVariable String customerId,
            @PathVariable Order.OrderStatus status) {
        log.info("Getting orders for customer: {} with status: {}", customerId, status);
        List<OrderResponseDto> response = orderService.getCustomerOrdersByStatus(customerId, status);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Void> cancelOrder(
            @PathVariable UUID id,
            @RequestParam String reason) {
        log.info("Cancelling order with ID: {} for reason: {}", id, reason);
        orderService.cancelOrder(id, reason);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/history")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') or @securityService.canAccessOrder(#id)")
    public ResponseEntity<List<OrderResponseDto.OrderStatusHistoryDto>> getOrderStatusHistory(@PathVariable UUID id) {
        log.info("Getting status history for order ID: {}", id);
        List<OrderResponseDto.OrderStatusHistoryDto> response = orderService.getOrderStatusHistory(id);
        return ResponseEntity.ok(response);
    }
}
