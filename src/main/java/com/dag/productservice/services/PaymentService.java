package com.dag.productservice.services;

import com.dag.productservice.dto.PaymentRequestDto;
import com.dag.productservice.dto.PaymentResponseDto;

public interface PaymentService {
    PaymentResponseDto createPayment(PaymentRequestDto paymentRequestDto);
}
