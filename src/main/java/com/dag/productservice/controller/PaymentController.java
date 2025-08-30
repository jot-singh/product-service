package com.dag.productservice.controller;

import com.dag.productservice.dto.PaymentRequestDto;
import com.dag.productservice.dto.PaymentResponseDto;
import com.dag.productservice.service.payment.PaymentService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public PaymentResponseDto createPayment(@RequestBody PaymentRequestDto paymentRequestDto) {
        return paymentService.createPayment(paymentRequestDto);
    }
}
