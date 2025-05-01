package org.healeasy.controllers;

import jakarta.validation.Valid;
import org.healeasy.DTOs.UserLoginDTO;
import org.healeasy.DTOs.UserProfileUpdateDTO;
import org.healeasy.DTOs.UserRegisterDTO;
import org.healeasy.DTOs.UserUpdatePasswordDTO;
import org.healeasy.Iservices.IUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final IUserService userService;
    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserRegisterDTO userRegisterDTO) {
        userService.register(userRegisterDTO);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserLoginDTO userLoginDTO) {
        String token = userService.login(userLoginDTO);
        return ResponseEntity.ok(token);
    }

    @PutMapping("/{userId}/profile")
    public ResponseEntity<?> updateProfile(@PathVariable Long userId, @Valid @RequestBody UserProfileUpdateDTO userProfileUpdateDTO) {
        userService.updateProfile(userId, userProfileUpdateDTO);
        return ResponseEntity.ok("User profile updated successfully");
    }

    @PutMapping("/{userId}/password")
    public ResponseEntity<?> updatePassword(@PathVariable Long userId, @Valid @RequestBody UserUpdatePasswordDTO userUpdatePasswordDTO) {
        userService.updatePassword(userId, userUpdatePasswordDTO);
        return ResponseEntity.ok("User password updated successfully");
    }

    @GetMapping("/{userId}/role")
    public ResponseEntity<?> getUserRole(@PathVariable Long userId){
        String role = userService.getUserRole(userId);
        return ResponseEntity.ok(role);
    }
}
