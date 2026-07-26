package com.travelagency.bookingservice.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.IdGeneratorType;

import java.util.Date;

@Entity
@Data
@Table(name = "reservations")
public class ReservationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idReservation;

    private Long packageId;
    private String customerId;
    private int passengerQuantity;
    private Date date;
    private double basePrice;
    private double discount;
    private double finalAmount;
    private String reservationStatus;
}
