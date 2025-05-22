package org.healeasy.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.healeasy.DTOs.*;
import org.healeasy.Iservices.IUserService;
import org.healeasy.entities.User;
import org.healeasy.exceptions.FailedToUploadImageException;
import org.healeasy.exceptions.InvalidCredentialsException;
import org.healeasy.exceptions.RequestSizeExceededException;
import org.healeasy.exceptions.UserNotFoundException;
import org.healeasy.mappers.UserMapper;
import org.healeasy.repositories.UserRepository;
import org.healeasy.services.CloudinaryServiceImpl;
import org.healeasy.services.JwtService;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@AllArgsConstructor
public class UserController {
    private final IUserService userService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JwtService jwtService;
    private final CloudinaryServiceImpl cloudinaryServiceImpl;

    // Helper method to get authenticated user ID
    private Long getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null ||
                "anonymousUser".equals(authentication.getPrincipal())) {
            throw new InvalidCredentialsException("User not authenticated");
        }
        try {
            return Long.valueOf(authentication.getPrincipal().toString());
        } catch (NumberFormatException e) {
            throw new InvalidCredentialsException("Invalid user ID format");
        }
    }

    // Helper method to get authenticated user
    private User getAuthenticatedUser() {
        Long userId = getAuthenticatedUserId();
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserDTO> register(@Valid @ModelAttribute UserRegisterDTO userRegisterDTO, HttpServletRequest request) {
        if(request.getContentLengthLong() > cloudinaryServiceImpl.extractMBsFromStrSize(
                cloudinaryServiceImpl.getMaxRequestSize()
        )){
            throw new RequestSizeExceededException("Request size exceeded 6MB limit.");
        }
        User registeredUser = userService.register(userRegisterDTO);
        return ResponseEntity.ok(userMapper.toDto(registeredUser));
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody UserLoginDTO userLoginDTO,
                                             HttpServletResponse response) {
        String token = userService.login(userLoginDTO, response);
        return ResponseEntity.ok(new JwtResponse(token));
    }

    @PutMapping(value = "/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserDTO> updateProfile(@Valid @ModelAttribute UserProfileUpdateDTO userProfileUpdateDTO) {
        try {
            User user = getAuthenticatedUser();
            User updatedUser = userService.updateProfile(user.getId(), userProfileUpdateDTO);
            return ResponseEntity.ok(userMapper.toDto(updatedUser));
        } catch (FailedToUploadImageException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/password")
    public ResponseEntity<?> updatePassword(@Valid @RequestBody UserUpdatePasswordDTO userUpdatePasswordDTO) {
        try {
            User user = getAuthenticatedUser();
            userService.updatePassword(user.getId(), userUpdatePasswordDTO);
            return ResponseEntity.ok("User password updated successfully");
        } catch (InvalidCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @GetMapping("/role")
    public ResponseEntity<?> getUserRole() {
        try {
            User user = getAuthenticatedUser();
            String role = userService.getUserRole(user.getId());
            return ResponseEntity.ok(role);
        } catch (InvalidCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> me() {
        try {
            User user = getAuthenticatedUser();
            return ResponseEntity.ok(userMapper.toDto(user));
        } catch (InvalidCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> refresh(@CookieValue(value = "refreshToken") String refreshToken) {
        var jwt = jwtService.parseToken(refreshToken);
        if (jwt == null || jwt.isExpired()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        var user = userRepository.findById(jwt.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        var accessToken = jwtService.generateAccessToken(user);
        return ResponseEntity.ok(new JwtResponse(accessToken.toString()));
    }
}