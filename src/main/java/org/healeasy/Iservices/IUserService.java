package org.healeasy.Iservices;

import jakarta.servlet.http.HttpServletResponse;
import org.healeasy.DTOs.*;
import org.healeasy.entities.User;

public interface IUserService {
    String login(UserLoginDTO loginDTO, HttpServletResponse response);
    void register(UserRegisterDTO registerDTO);
    User updateProfile(Long userId, UserProfileUpdateDTO userProfileUpdateDTO);
    void updatePassword(Long userId, UserUpdatePasswordDTO userUpdatePasswordDTO);
    String getUserRole(Long userId);
    Long getAuthenticatedUserId();
}
