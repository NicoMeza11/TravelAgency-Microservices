package com.travelagency.bookingservice.dtos;

import lombok.Data;

@Data
public class TourPackageDTO {

    private Long idPackage;
    private String packageName;
    private double price;
    private double packageDiscount;
    private int spotsAvailable;
    private String packageStatus;
}
