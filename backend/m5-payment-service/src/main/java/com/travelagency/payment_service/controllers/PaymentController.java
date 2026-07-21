package com.travelagency.payment_service.controllers;

import com.travelagency.payment_service.dtos.PaymentDTO;
import com.travelagency.payment_service.entities.PaymentEntity;
import com.travelagency.payment_service.services.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    @Autowired
    private final PaymentService paymentService;

    @PostMapping("/save")
    public ResponseEntity<PaymentEntity> savePayment(@RequestBody PaymentDTO paymentDTO){

        PaymentEntity payment = new PaymentEntity();
        payment.setPricePayment(paymentDTO.getPricePayment());
        payment.setPaymentMethod(paymentDTO.getPaymentMethod());
        payment.setReservationId(paymentDTO.getReservationId());

        PaymentEntity newPayment = paymentService.processPayment(payment);
        return ResponseEntity.status(HttpStatus.CREATED).body(newPayment);
    }


}
