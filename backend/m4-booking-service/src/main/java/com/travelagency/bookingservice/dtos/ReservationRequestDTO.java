package com.travelagency.bookingservice.dtos;

import lombok.Data;

@Data
public class ReservationRequestDTO {

    private String idCustomer;
    private Long idPackage;
    private int passengerQuantity;
}
