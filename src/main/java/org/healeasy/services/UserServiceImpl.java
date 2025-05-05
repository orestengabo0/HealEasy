package org.healeasy.services;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.healeasy.DTOs.*;
import org.healeasy.Iservices.IUserService;
import org.healeasy.config.JwtConfig;
import org.healeasy.entities.User;
import org.healeasy.exceptions.*;
import org.healeasy.repositories.UserRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@AllArgsConstructor
public class UserServiceImpl implements IUserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final CloudinaryServiceImpl cloudinaryServiceImpl;
    private final AuthenticationManager authenticationManager;
    private final JwtConfig jwtConfig;

    @Override
    public String login(UserLoginDTO loginDTO, HttpServletResponse response) {
        // Create authentication token
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        loginDTO.getUsername() != null ? loginDTO.getUsername() : loginDTO.getEmail(),
                        loginDTO.getPassword()
                );
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = userRepository.findByUsername(loginDTO.getUsername());
        if (user == null) {
            throw new UserNotFoundException("User not found.");
        }
        var accessToken = jwtService.generateAccessToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        // Store refresh token in the HttpOnly cookie
        var cookie = ResponseCookie.from("refreshToken", refreshToken.toString())
                .httpOnly(true)
                .secure(true)
                .path("/auth/refresh")
                .maxAge(jwtConfig.getRefreshTokenExpiration())
                .sameSite("Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return accessToken.toString();
    }

    @Override
    public void register(UserRegisterDTO registerDTO) {
        if (userRepository.existsByEmail(registerDTO.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }
        if(userRepository.existsByPhoneNumber(registerDTO.getPhoneNumber())) {
            throw new PhoneNumberAlreadyExistsException("Phone number already exists");
        }
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setEmail(registerDTO.getEmail());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setPhoneNumber(registerDTO.getPhoneNumber());
        if (registerDTO.getProfileImage() != null) {
            try{
                String imageUrl = cloudinaryServiceImpl.uploadImage(registerDTO.getProfileImage());
                user.setProfileImageUrl(imageUrl);
            } catch (IOException e) {
                throw new FailedToUploadImageException("Failed to upload image.");
            }
        } else {
            String USER_DEFAULT_AVATAR = "https://upload.wikimedia.org/wikipedia/commons/7/7c/Profile_avatar_placeholder_large.png";
            user.setProfileImageUrl(USER_DEFAULT_AVATAR);
        }
        if(registerDTO.getRole() != null){
            user.setRole(registerDTO.getRole());
        }
        userRepository.save(user);
    }

    @Override
    public User updateProfile(Long userId, UserProfileUpdateDTO userProfileUpdateDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        if(userProfileUpdateDTO.getUsername() != null) user.setUsername(userProfileUpdateDTO.getUsername());
        if(userProfileUpdateDTO.getEmail() != null) user.setEmail(userProfileUpdateDTO.getEmail());
        if (userProfileUpdateDTO.getProfileImage() != null) {
            if (user.getProfileImageUrl() != null && user.getProfileImageUrl().contains("cloudinary")) {
                try{
                    // Delete old image if it exists
                    String publicId = user.getProfileImageUrl().substring(user.getProfileImageUrl().lastIndexOf("/") + 1).split("\\.")[0];
                    cloudinaryServiceImpl.deleteImage(publicId);

                }catch(IOException ex){
                    throw new FailedToUploadImageException("Failed to delete image.");
                }
            }
            try {
                // Upload new image
                String newImageUrl = cloudinaryServiceImpl.uploadImage(userProfileUpdateDTO.getProfileImage());
                user.setProfileImageUrl(newImageUrl);
            }catch (IOException ex){
                throw new FailedToUploadImageException("Failed to upload image.");
            }

        }
        userRepository.save(user);
        return user;
    }

    @Override
    public void updatePassword(Long userId, UserUpdatePasswordDTO userUpdatePasswordDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        if(!passwordEncoder.matches(userUpdatePasswordDTO.getOldPassword(), user.getPassword())){
            throw new InvalidCredentialsException("Invalid old password");
        }
        user.setPassword(passwordEncoder.encode(userUpdatePasswordDTO.getNewPassword()));
        userRepository.save(user);
        }

    @Override
    public String getUserRole(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return user.getRole().name();
    }
}
