package com.dag.productservice.dto;

import com.dag.productservice.models.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusUpdateRequestDto {

    private Order.OrderStatus status;
    private String notes;
}
