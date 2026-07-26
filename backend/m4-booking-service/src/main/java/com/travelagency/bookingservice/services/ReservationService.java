package com.travelagency.bookingservice.services;

import com.travelagency.bookingservice.dtos.ReservationRequestDTO;
import com.travelagency.bookingservice.dtos.ReservationResponseDTO;
import com.travelagency.bookingservice.dtos.TourPackageDTO;
import com.travelagency.bookingservice.entities.ReservationEntity;
import com.travelagency.bookingservice.repositories.ReservationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {


    private final ReservationRepository reservationRepo;
    private final RestTemplate restTemplate;

    private static final double  GROUP_DISCOUNT_PERCENTAGE = 0.1; //10% discount for 4+ passengers
    private static final double FREQUENT_CLIENT_DISCOUNT_PERCENTAGE = 0.05; //5% discount for frequent client
    private static final double MAX_DISCOUNT_ALLOWED = 0.20; // 20% = mas discount allowed

    public ReservationResponseDTO createReservation(ReservationRequestDTO requestDTO) {

        // 1. Validar cantidad de pasajeros
        if (requestDTO.getPassengerQuantity() <= 0) {
            throw new IllegalArgumentException("Passengers quantity must be higher than 0");
        }

        // 2. Obtener el paquete desde M2 vía HTTP
        String packageUrl = "http://PACKAGE-SERVICE/api/tourpackage/" + requestDTO.getIdPackage();
        TourPackageDTO tourPackage;
        try {
            tourPackage = restTemplate.getForObject(packageUrl, TourPackageDTO.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Cannot find package or M2 is down");
        }

        if (tourPackage == null) {
            throw new RuntimeException("Cannot find package");
        }

        // 3. Validar estado y cupos
        if (!"ACTIVE".equals(tourPackage.getPackageStatus())) {
            throw new RuntimeException("The package does not have ACTIVE status");
        }
        if (requestDTO.getPassengerQuantity() > tourPackage.getSpotsAvailable()) {
            throw new RuntimeException("There are not enough places for this reservation");
        }

        // 4. Actualizar cupos en M2 (Llamada HTTP PUT)
        // M2 deberá tener un endpoint para esto. Si no hay cupos o falla, lanza excepción y hace rollback.
        String updateSpotsUrl = "http://PACKAGE-SERVICE/api/tourpackage/" + requestDTO.getIdPackage() +
                "/reduce-spots?quantity=" + requestDTO.getPassengerQuantity();
        try {
            restTemplate.put(updateSpotsUrl, null);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update spots in Package Service");
        }

        // 5. Calculate the discount
        double basePrice = tourPackage.getPrice() * requestDTO.getPassengerQuantity();
        double totalDiscount = 0.0;

        if (requestDTO.getPassengerQuantity() >= 4) {
            totalDiscount += GROUP_DISCOUNT_PERCENTAGE;
            System.out.println("Group discount activated");
        }

        if (tourPackage.getPackageDiscount() > 0) {
            totalDiscount += tourPackage.getPackageDiscount();
        }

        long paidReservationsCount = reservationRepo.countByCustomerIdAndReservationStatus(
                requestDTO.getIdCustomer(), "CONFIRMED"
        );

        if (paidReservationsCount >= 3) {
            totalDiscount += FREQUENT_CLIENT_DISCOUNT_PERCENTAGE;
            System.out.println("Frequent client discount activated");
        }

        if (totalDiscount > MAX_DISCOUNT_ALLOWED) {
            totalDiscount = MAX_DISCOUNT_ALLOWED;
            System.out.println("Total discount: " + totalDiscount + " exceeds max, setting discount to 20%");
        }

        //Calculate the final amount
        double moneyDiscounted = (basePrice * totalDiscount);
        double finalAmount = Math.round((basePrice - moneyDiscounted) * 100.0) / 100.0;

        // 6. Build and save
        ReservationEntity reservation = new ReservationEntity();
        reservation.setCustomerId(requestDTO.getIdCustomer());
        reservation.setPackageId(requestDTO.getIdPackage());
        reservation.setPassengerQuantity(requestDTO.getPassengerQuantity());
        reservation.setDate(new Date());
        reservation.setBasePrice(basePrice);
        reservation.setDiscount(totalDiscount);
        reservation.setFinalAmount(finalAmount);
        reservation.setReservationStatus("PENDING_PAYMENT");

        ReservationEntity savedReservation = reservationRepo.save(reservation);

        // 7. Return Response DTO
        return new ReservationResponseDTO(
                savedReservation.getIdReservation(),
                savedReservation.getPackageId(),
                savedReservation.getPassengerQuantity(),
                savedReservation.getDate(),
                savedReservation.getBasePrice(),
                savedReservation.getDiscount(),
                savedReservation.getFinalAmount(),
                savedReservation.getReservationStatus()
        );
    }

    public List<ReservationEntity> getAllReservation(){
        return reservationRepo.findAll();
    }

    public List<ReservationEntity> getAllReservationUser(String userId){
        return reservationRepo.findByCustomerId(userId);
    }

    @Transactional
    public void cancelReservation(Long reservationId){
        ReservationEntity reservation = reservationRepo.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Cannot found reservation with id: " + reservationId));

        if("CANCELLED".equals(reservation.getReservationStatus())){
            throw new RuntimeException("Reservation is already cancelled");
        }

        //Return slots to M2
        String addSpotsUrl = "http://PACKAGE-SERVICE/api/package/" + reservation.getPackageId() +
        "/add-spots?quantity=" + reservation.getPassengerQuantity();
        restTemplate.put(addSpotsUrl, null);

        reservation.setReservationStatus("CANCELLED");
        reservationRepo.save(reservation);

        String refundUrl = "http://PAYMENT-SERVICE/api/payments/refund/" + reservationId;
        try {
            restTemplate.put(refundUrl, null);
            System.out.println("Payment refund requested successfully");
        } catch (Exception e) {
            System.out.println("The reservation did not have a payment associated or refund failed");
        }
        System.out.println("Reservation with id" + reservationId + "cancelled successfully");
    }

}
