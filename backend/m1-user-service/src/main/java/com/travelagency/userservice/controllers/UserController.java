package com.travelagency.userservice.controllers;

import com.travelagency.userservice.Dtos.UserDTO;
import com.travelagency.userservice.entities.UserEntity;
import com.travelagency.userservice.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@CrossOrigin(origins={"http://localhost:5173", "http://localhost:8070","http://18.230.122.69:8070"})
public class UserController {

    private final UserService userService;

    @PutMapping("/update/{userId}")
    public ResponseEntity<String> updateUser(@RequestBody UserDTO userDTO, @PathVariable String userId){
        userService.updateUserDb(userId, userDTO);
        System.out.println("Update succesfully");
        return ResponseEntity.ok("User updated successfully");
    }

    @PostMapping("/sync")
    public ResponseEntity<String> saveUserDatabase(@RequestBody UserDTO userDTO){
        userService.saveUserFromKeycloak(userDTO);
        return ResponseEntity.ok().body("User saved in database successfully");
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserEntity> getUserById(@PathVariable String userId){
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deactivateAccount(@PathVariable String userId){
        userService.deleteAccount(userId);
        return ResponseEntity.noContent().build();
    }
}
