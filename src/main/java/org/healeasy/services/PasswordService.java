package org.healeasy.services;

import lombok.AllArgsConstructor;
import org.healeasy.DTOs.UpdatePasswordDto;
import org.healeasy.entities.User;
import org.healeasy.exceptions.InvalidCredentialsException;
import org.healeasy.exceptions.UserNotFoundException;
import org.healeasy.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service for handling password-related operations.
 * This service extracts common password update logic to avoid code duplication.
 */
@Service
@AllArgsConstructor
public class PasswordService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Finds a user by ID
     * @param userId The ID of the user to find
     * @return The found user
     * @throws UserNotFoundException if the user is not found
     */
    public User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    /**
     * Validates if the provided password matches the user's current password
     * @param user The user whose password to check
     * @param providedPassword The password to validate
     * @throws InvalidCredentialsException if the password doesn't match
     */
    public void validatePassword(User user, String providedPassword) {
        if (!passwordEncoder.matches(providedPassword, user.getPassword())) {
            throw new InvalidCredentialsException("Invalid old password");
        }
    }

    /**
     * Updates a user's password
     * @param user The user whose password to update
     * @param newPassword The new password
     */
    public void updatePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    /**
     * Complete password update process: find user, validate old password, update to new password
     * @param userId The ID of the user whose password to update
     * @param updatePasswordDto DTO containing old and new passwords
     * @return The updated user
     */
    public User updateUserPassword(Long userId, UpdatePasswordDto updatePasswordDto) {
        User user = findUserById(userId);
        validatePassword(user, updatePasswordDto.getOldPassword());
        updatePassword(user, updatePasswordDto.getNewPassword());
        return user;
    }
}