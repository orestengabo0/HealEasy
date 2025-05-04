package org.healeasy.controllers;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.healeasy.DTOs.*;
import org.healeasy.Iservices.IUserService;
import org.healeasy.mappers.UserMapper;
import org.healeasy.repositories.UserRepository;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
public class UserController {
    private final IUserService userService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> register(@Valid @ModelAttribute UserRegisterDTO userRegisterDTO) {
        userService.register(userRegisterDTO);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody UserLoginDTO userLoginDTO, HttpServletResponse response) {
        String token = userService.login(userLoginDTO, response);
        return ResponseEntity.ok(new JwtResponse(token));
    }

    @PutMapping(value = "/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserDTO> updateProfile(@Valid @ModelAttribute UserProfileUpdateDTO userProfileUpdateDTO) {
        Long userId = userService.getAuthenticatedUserId();
        var user = userService.updateProfile(userId, userProfileUpdateDTO);
        var userDto = userMapper.toDto(user);
        return ResponseEntity.ok(userDto);
    }

    @PutMapping("/password")
    public ResponseEntity<?> updatePassword(@Valid @RequestBody UserUpdatePasswordDTO userUpdatePasswordDTO) {
        Long userId = userService.getAuthenticatedUserId();
        userService.updatePassword(userId, userUpdatePasswordDTO);
        return ResponseEntity.ok("User password updated successfully");
    }

    @GetMapping("/{userId}/role")
    public ResponseEntity<?> getUserRole(@PathVariable Long userId){
        String role = userService.getUserRole(userId);
        return ResponseEntity.ok(role);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> me(){
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var userId = (Long) authentication.getPrincipal();
        var user = userRepository.findById(userId).orElseThrow();
        var userDto = userMapper.toDto(user);
        return ResponseEntity.ok(userDto);
    }
}
