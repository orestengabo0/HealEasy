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
import org.springframework.security.authentication.BadCredentialsException;
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
    private final PasswordService passwordService;

    @Override
    public String login(UserLoginDTO loginDTO, HttpServletResponse response) {
        // 1. Input validation
        if ((loginDTO.getUsername() == null && loginDTO.getEmail() == null) ||
                loginDTO.getPassword() == null) {
            throw new IllegalArgumentException("Username/email and password are required");
        }

        try {
            // 2. Authentication
            String loginIdentifier = loginDTO.getUsername() != null ?
                    loginDTO.getUsername() : loginDTO.getEmail();

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginIdentifier,
                            loginDTO.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 3. Find user (more efficient single query)
            User user = userRepository.findByUsernameOrEmail(
                    loginDTO.getUsername(),
                    loginDTO.getEmail()
            );

            if (user == null) {
                throw new UserNotFoundException("User not found");
            }

            // 4. Generate tokens
            var accessToken = jwtService.generateAccessToken(user);
            var refreshToken = jwtService.generateRefreshToken(user);

            // 5. Set secure cookie
            setRefreshTokenCookie(response, refreshToken.toString());

            return accessToken.toString();

        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid username/email or password");
        }
    }

    private void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true) // In production, set to true (requires HTTPS)
                .path("/api/v1/auth/refresh")
                .maxAge(jwtConfig.getRefreshTokenExpiration())
                .sameSite("Lax") // or "Strict" for better security
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    @Override
    public User register(UserRegisterDTO registerDTO) {
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
            if(registerDTO.getProfileImage().getSize() > cloudinaryServiceImpl
                    .extractMBsFromStrSize(cloudinaryServiceImpl.getMaxFileSize())){
                throw new LargeFileException("Large file size exceeded");
            }
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
        userRepository.save(user);
        return user;
    }

    @Override
    public User updateProfile(Long userId, UserProfileUpdateDTO userProfileUpdateDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        if(userProfileUpdateDTO.getUsername() != null) user.setUsername(userProfileUpdateDTO.getUsername());
        if(userProfileUpdateDTO.getEmail() != null) user.setEmail(userProfileUpdateDTO.getEmail());
        if (userProfileUpdateDTO.getProfileImage() != null) {
            if (user.getProfileImageUrl() != null && user.getProfileImageUrl().contains("cloudinary")) {
                if(userProfileUpdateDTO.getProfileImage().getSize() > cloudinaryServiceImpl
                        .extractMBsFromStrSize(cloudinaryServiceImpl.getMaxFileSize())){
                    throw new LargeFileException("Large file size exceeded");
                }
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
    public void updatePassword(Long userId, UpdatePasswordDto updatePasswordDto) {
        passwordService.updateUserPassword(userId, updatePasswordDto);
    }

    @Override
    public String getUserRole(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return user.getRole().name();
    }
}
