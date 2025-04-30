package org.healeasy.servicesImpl;

import org.healeasy.DTOs.UserLoginDTO;
import org.healeasy.DTOs.UserProfileUpdateDTO;
import org.healeasy.DTOs.UserRegisterDTO;
import org.healeasy.DTOs.UserUpdatePasswordDTO;
import org.healeasy.Iservices.IUserService;
import org.healeasy.entities.User;
import org.healeasy.repositories.UserRepository;
import org.healeasy.security.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements IUserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final String USER_DEFAULT_AVATAR = "https://upload.wikimedia.org/wikipedia/commons/7/7c/Profile_avatar_placeholder_large.png";

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public String login(UserLoginDTO loginDTO) {
        User user = userRepository.findByUsernameOrEmail(loginDTO.getUsername(), loginDTO.getEmail());
        if(!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())){
            throw new IllegalArgumentException("Invalid username, email or password");
        }
        return jwtTokenProvider.generateToken(user.getUsername());
    }

    @Override
    public void register(UserRegisterDTO registerDTO) {
        if(userRepository.existsByEmail(registerDTO.getEmail())){
            throw new IllegalArgumentException("Email already exists");
        }
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setEmail(registerDTO.getEmail());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setPhoneNumber(registerDTO.getPhoneNumber());
        // Handle optional profile image
        if (registerDTO.getProfileImage() != null) {
            user.setProfileImageUrl(registerDTO.getProfileImage().getAbsolutePath());
        } else {
            user.setProfileImageUrl(USER_DEFAULT_AVATAR);
        }
        userRepository.save(user);
    }

    @Override
    public void updateProfile(Long userId, UserProfileUpdateDTO userProfileUpdateDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if(userProfileUpdateDTO.getUsername() != null) user.setUsername(userProfileUpdateDTO.getUsername());
        if(userProfileUpdateDTO.getEmail() != null) user.setEmail(userProfileUpdateDTO.getEmail());
        if(userProfileUpdateDTO.getProfileImage() != null) user.setProfileImageUrl(userProfileUpdateDTO.getProfileImage());
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
        return "";
    }
}
