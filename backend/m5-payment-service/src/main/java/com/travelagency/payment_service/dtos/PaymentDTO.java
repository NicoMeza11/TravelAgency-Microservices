package com.travelagency.payment_service.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDTO {
    private Long idReservation;
    private double pricePayment;
    private String cardNumber;
    private String expirationDate;
    private String cvv;
    private String paymentMethod;
    private Long reservationId;
}
