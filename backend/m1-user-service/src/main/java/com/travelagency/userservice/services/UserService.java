package com.travelagency.userservice.services;

import com.travelagency.userservice.Dtos.UserDTO;
import com.travelagency.userservice.entities.UserEntity;
import com.travelagency.userservice.respositories.IKeycloakService;
import com.travelagency.userservice.respositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final IKeycloakService keycloakService;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;

    public UserEntity getUserById(String userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User can't be found on DB"));
    }

    private String cleanString(String input){
        if(input == null || input.isBlank()){
            return null;
        }else{
            return input.trim();
        }
    }

    @Transactional
    public void updateUserDb(String kcUserId, UserDTO userDTO){

        UserEntity userEntity = userRepository.findById(kcUserId)
                .orElseThrow(() -> new RuntimeException("User can't be found on DB"));
        userEntity.setFirstName(userDTO.getFirstName());
        userEntity.setLastName(userDTO.getLastName());
        userEntity.setEmail(userDTO.getEmail());
        userEntity.setPhoneNumber(cleanString(userDTO.getPhoneNumber()));
        userEntity.setDocumentId(cleanString(userDTO.getDocumentId()));
        userEntity.setNationality(cleanString(userDTO.getNationality()));
        userEntity.setUserStatus(true);

        userRepository.save(userEntity);
        keycloakService.updateUser(kcUserId, userDTO);

    }

    public void saveUserFromKeycloak(UserDTO userDTO){
        if(userRepository.existsById(userDTO.getId())){
            log.info("User {} already exists in database, skipping save", userDTO.getId());
            return;
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setId(userDTO.getId());
        userEntity.setFirstName(userDTO.getFirstName());
        userEntity.setLastName(userDTO.getLastName());
        userEntity.setEmail(userDTO.getEmail());
        userEntity.setUserStatus(true);

        userRepository.save(userEntity);
        log.info("User {} synced from Keycloak to database", userDTO.getId());
    }

    @Transactional
    public void deleteAccount(String userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Cannot find user with id: " + userId));

        String url = "http://reservation-service/api/reservations/customer/" + userId + "/count?status=CONFIRMED";

        Integer confirmedReservations = restTemplate.getForObject(url, Integer.class);

        int count = (confirmedReservations != null) ? confirmedReservations : 0;

        log.info("The user has {} paid reservations", count);

        if(count >= 1){
            keycloakService.disableUser(userId);
            user.setUserStatus(false);
            userRepository.save(user);
        } else {
            keycloakService.deleteUser(userId);
            userRepository.deleteById(userId);
        }
    }

}