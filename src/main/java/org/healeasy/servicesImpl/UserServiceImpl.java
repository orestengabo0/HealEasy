package org.healeasy.servicesImpl;

import org.healeasy.DTOs.UserLoginDTO;
import org.healeasy.DTOs.UserProfileUpdateDTO;
import org.healeasy.DTOs.UserRegisterDTO;
import org.healeasy.DTOs.UserUpdatePasswordDTO;
import org.healeasy.Iservices.IUserService;
import org.healeasy.entities.User;
import org.healeasy.exceptions.EmailAlreadyExistsException;
import org.healeasy.exceptions.FailedToUploadImageException;
import org.healeasy.exceptions.PhoneNumberAlreadyExistsException;
import org.healeasy.exceptions.UserNotFoundException;
import org.healeasy.repositories.UserRepository;
import org.healeasy.security.JwtTokenProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class UserServiceImpl implements IUserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final CloudinaryServiceImpl cloudinaryServiceImpl;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtTokenProvider jwtTokenProvider, CloudinaryServiceImpl cloudinaryServiceImpl) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.cloudinaryServiceImpl = cloudinaryServiceImpl;
    }

    @Override
    public String login(UserLoginDTO loginDTO) {
        User user = userRepository.findByUsernameOrEmail(loginDTO.getUsername(), loginDTO.getEmail());
        if(user == null){
            throw new UserNotFoundException("User not found.");
        }
        if(!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())){
            throw new IllegalArgumentException("Invalid username, email or password");
        }
        return jwtTokenProvider.generateToken(user.getUsername());
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
    public void updateProfile(Long userId, UserProfileUpdateDTO userProfileUpdateDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
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
    }

    @Override
    public void updatePassword(Long userId, UserUpdatePasswordDTO userUpdatePasswordDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if(!passwordEncoder.matches(userUpdatePasswordDTO.getOldPassword(), user.getPassword())){
            throw new IllegalArgumentException("Invalid old password");
        }
        user.setPassword(passwordEncoder.encode(userUpdatePasswordDTO.getNewPassword()));
        userRepository.save(user);
        }

    @Override
    public String getUserRole(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return user.getRole().name();
    }

    @Override
    public Long getAuthenticatedUserId(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || !authentication.isAuthenticated()){
            throw new IllegalStateException("No authenticated user found");
        }

        String username = authentication.getName();
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }
        return user.getId();
    }
}
