package org.healeasy.services;

import lombok.AllArgsConstructor;
import org.healeasy.DTOs.PaginationRequest;
import org.healeasy.DTOs.PagingResult;
import org.healeasy.DTOs.UpdatePasswordDto;
import org.healeasy.DTOs.UserDTO;
import org.healeasy.Iservices.IAdminService;
import org.healeasy.entities.User;
import org.healeasy.enums.UserRole;
import org.healeasy.exceptions.AdminOperationException;
import org.healeasy.exceptions.UserNotFoundException;
import org.healeasy.mappers.UserMapper;
import org.healeasy.repositories.UserRepository;
import org.healeasy.utils.PaginationUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AdminServiceImpl implements IAdminService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordService passwordService;

    @Override
    @Deprecated
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public PagingResult<UserDTO> getAllUsersPaginated(PaginationRequest paginationRequest) {
        Pageable pageable = PaginationUtils.getPageable(paginationRequest);
        Page<User> userPage = userRepository.findAll(pageable);

        List<UserDTO> userDTOs = userPage.getContent().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());

        return new PagingResult<>(
                userDTOs,
                userPage.getTotalPages(),
                userPage.getTotalElements(),
                userPage.getSize(),
                userPage.getNumber(),
                userPage.isEmpty()
        );
    }

    @Override
    @Deprecated
    public List<UserDTO> getUsersByRole(UserRole role) {
        return userRepository.findAll().stream()
                .filter(user -> user.getRole() == role)
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public PagingResult<UserDTO> getUsersByRolePaginated(UserRole role, PaginationRequest paginationRequest) {
        Pageable pageable = PaginationUtils.getPageable(paginationRequest);
        Page<User> userPage = userRepository.findByRole(role, pageable);

        List<UserDTO> userDTOs = userPage.getContent().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());

        return new PagingResult<>(
                userDTOs,
                userPage.getTotalPages(),
                userPage.getTotalElements(),
                userPage.getSize(),
                userPage.getNumber(),
                userPage.isEmpty()
        );
    }

    @Override
    public UserDTO getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        return userMapper.toDto(user);
    }

    @Override
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        // Prevent deleting the last admin user
        if (user.getRole() == UserRole.ADMIN) {
            long adminCount = userRepository.findAll().stream()
                    .filter(u -> u.getRole() == UserRole.ADMIN)
                    .count();

            if (adminCount <= 1) {
                throw new AdminOperationException("Cannot delete the last admin user");
            }
        }

        userRepository.deleteById(userId);
    }

    @Override
    @Deprecated
    public List<UserDTO> searchUsers(String searchTerm) {
        return userRepository.searchUsers(searchTerm).stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public PagingResult<UserDTO> searchUsersPaginated(String searchTerm, PaginationRequest paginationRequest) {
        Pageable pageable = PaginationUtils.getPageable(paginationRequest);
        Page<User> userPage = userRepository.searchUsersPaginated(searchTerm, pageable);

        List<UserDTO> userDTOs = userPage.getContent().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());

        return new PagingResult<>(
                userDTOs,
                userPage.getTotalPages(),
                userPage.getTotalElements(),
                userPage.getSize(),
                userPage.getNumber(),
                userPage.isEmpty()
        );
    }

    @Override
    public void updateAdminPassword(Long adminId, UpdatePasswordDto updatePasswordDto){
        // First find the user to check if they are an admin
        User user = passwordService.findUserById(adminId);

        // Verify the user is an admin
        if(user.getRole() != UserRole.ADMIN){
            throw new AdminOperationException("Not authorized to use this operation");
        }

        // Use the password service to handle the password update
        passwordService.validatePassword(user, updatePasswordDto.getOldPassword());
        passwordService.updatePassword(user, updatePasswordDto.getNewPassword());
    }
}
