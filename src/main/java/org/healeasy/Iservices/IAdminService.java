package org.healeasy.Iservices;

import org.healeasy.DTOs.PaginationRequest;
import org.healeasy.DTOs.PagingResult;
import org.healeasy.DTOs.UpdatePasswordDto;
import org.healeasy.DTOs.UserDTO;
import org.healeasy.enums.UserRole;

import java.util.List;

public interface IAdminService {
    /**
     * Get all users in the system
     * @return List of all users
     * @deprecated Use {@link #getAllUsersPaginated(PaginationRequest)} instead
     */
    @Deprecated
    List<UserDTO> getAllUsers();

    /**
     * Get all users in the system with pagination
     * @param paginationRequest The pagination parameters
     * @return Paginated list of users
     */
    PagingResult<UserDTO> getAllUsersPaginated(PaginationRequest paginationRequest);

    /**
     * Get users by role
     * @param role The role to filter by
     * @return List of users with the specified role
     * @deprecated Use {@link #getUsersByRolePaginated(UserRole, PaginationRequest)} instead
     */
    @Deprecated
    List<UserDTO> getUsersByRole(UserRole role);

    /**
     * Get users by role with pagination
     * @param role The role to filter by
     * @param paginationRequest The pagination parameters
     * @return Paginated list of users with the specified role
     */
    PagingResult<UserDTO> getUsersByRolePaginated(UserRole role, PaginationRequest paginationRequest);

    /**
     * Get user by ID
     * @param userId The ID of the user to retrieve
     * @return The user with the specified ID
     */
    UserDTO getUserById(Long userId);

    /**
     * Delete a user
     * @param userId The ID of the user to delete
     */
    void deleteUser(Long userId);

    /**
     * Search users by a search term
     * @param searchTerm The term to search for in username, email, or phone number
     * @return List of users matching the search term
     * @deprecated Use {@link #searchUsersPaginated(String, PaginationRequest)} instead
     */
    @Deprecated
    List<UserDTO> searchUsers(String searchTerm);

    /**
     * Search users by a search term with pagination
     * @param searchTerm The term to search for in username, email, or phone number
     * @param paginationRequest The pagination parameters
     * @return Paginated list of users matching the search term
     */
    PagingResult<UserDTO> searchUsersPaginated(String searchTerm, PaginationRequest paginationRequest);

    /**
     * Update admin password
     * @param userId The ID of the admin user
     * @param updatePasswordDto The password update data
     */
    void updateAdminPassword(Long userId, UpdatePasswordDto updatePasswordDto);
}
