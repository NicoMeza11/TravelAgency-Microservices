package com.travelagency.payment_service.services;

import com.travelagency.payment_service.entities.PaymentEntity;
import com.travelagency.payment_service.repositories.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepo;

    public PaymentEntity processPayment(PaymentEntity payment) {

        //we save the date and status of the payment
        payment.setPaymentDate(new Date());
        payment.setPaymentStatus("COMPLETED");

        PaymentEntity savedPayment = paymentRepo.save(payment);

        // 3. COMUNICACIÓN INTER-SERVICIO PENDIENTE

        return savedPayment;
    }
}
