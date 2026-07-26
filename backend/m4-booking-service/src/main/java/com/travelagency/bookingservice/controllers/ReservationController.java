package com.travelagency.bookingservice.controllers;

import com.travelagency.bookingservice.dtos.ReservationRequestDTO;
import com.travelagency.bookingservice.dtos.ReservationResponseDTO;
import com.travelagency.bookingservice.entities.ReservationEntity;
import com.travelagency.bookingservice.services.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservation")
@RequiredArgsConstructor
@CrossOrigin(origins={"http://localhost:5173", "http://localhost:8070","http://18.230.122.69:8070"})
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping("/save")
    public ResponseEntity<ReservationResponseDTO> createReservation(@RequestBody ReservationRequestDTO requestDTO){
        System.out.println("Entro al endpoint");
        ReservationResponseDTO response =  reservationService.createReservation(requestDTO);
        System.out.println("Recibio el responseDTO");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/allReservation")
    public ResponseEntity<List<ReservationEntity>> getAllReservations(){
        return ResponseEntity.ok(reservationService.getAllReservation());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReservationEntity>> getUserReservations(@PathVariable String userId){
        return ResponseEntity.ok(reservationService.getAllReservationUser(userId));
    }

    @PutMapping("/cancel/{id}")
    public ResponseEntity<Void> cancelReservation(@PathVariable Long id){
        reservationService.cancelReservation(id);
        return ResponseEntity.ok().build();
    }
}
