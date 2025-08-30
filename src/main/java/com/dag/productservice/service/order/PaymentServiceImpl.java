package com.dag.productservice.service.order;

import com.dag.productservice.dto.PaymentRequestDto;
import com.dag.productservice.dto.PaymentResponseDto;
import com.dag.productservice.service.payment.PaymentService;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Override
    public PaymentResponseDto createPayment(PaymentRequestDto paymentRequestDto) {
        // This is a mock payment service. In a real application, you would integrate with a real payment gateway.
        // For now, we'll just generate a fake payment URL.
        PaymentResponseDto paymentResponseDto = new PaymentResponseDto();
        paymentResponseDto.setPaymentUrl("https://fake-payment-gateway.com/pay?product_id=" + paymentRequestDto.getProductId());
        return paymentResponseDto;
    }
}
