package com.travelagency.userservice.respositories;

import com.travelagency.userservice.Dtos.UserDTO;

public interface IKeycloakService {
    void updateUser(String userId, UserDTO userDTO);
    void deleteUser(String userId);
    void disableUser(String userId);
}
