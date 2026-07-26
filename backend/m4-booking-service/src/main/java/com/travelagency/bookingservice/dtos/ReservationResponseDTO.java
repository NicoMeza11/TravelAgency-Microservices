package com.travelagency.bookingservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationResponseDTO {
    private Long idReservation;
    private Long idPackage;
    private int passengerQuantity;
    private Date reservationDate;
    private double basePrice;
    private double discount;
    private double finalAmount;
    private String reservationStatus;
}
