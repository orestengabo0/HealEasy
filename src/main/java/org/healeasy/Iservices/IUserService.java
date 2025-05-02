package org.healeasy.Iservices;

import org.healeasy.DTOs.UserLoginDTO;
import org.healeasy.DTOs.UserProfileUpdateDTO;
import org.healeasy.DTOs.UserRegisterDTO;
import org.healeasy.DTOs.UserUpdatePasswordDTO;

public interface IUserService {
    String login(UserLoginDTO loginDTO);
    void register(UserRegisterDTO registerDTO);
    void updateProfile(Long userId, UserProfileUpdateDTO userProfileUpdateDTO);
    void updatePassword(Long userId, UserUpdatePasswordDTO userUpdatePasswordDTO);
    String getUserRole(Long userId);
    Long getAuthenticatedUserId();
}
