package org.healeasy.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.healeasy.DTOs.PaginationRequest;
import org.healeasy.DTOs.PagingResult;
import org.healeasy.DTOs.UpdatePasswordDto;
import org.healeasy.DTOs.UserDTO;
import org.healeasy.Iservices.IAdminService;
import org.healeasy.enums.UserRole;
import org.healeasy.exceptions.AdminOperationException;
import org.healeasy.exceptions.InvalidCredentialsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@AllArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final IAdminService adminService;
    /**
     * Get all users with pagination
     * @param page The page number (1-based)
     * @param size The page size
     * @param sortField The field to sort by
     * @param direction The sort direction
     * @return Paginated list of users
     */
    @GetMapping("/users/paginated")
    public ResponseEntity<PagingResult<UserDTO>> getAllUsersPaginated(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "id") String sortField,
            @RequestParam(defaultValue = "DESC") Sort.Direction direction) {
        PaginationRequest paginationRequest = new PaginationRequest(page, size, sortField, direction);
        return ResponseEntity.ok(adminService.getAllUsersPaginated(paginationRequest));
    }

    /**
     * Get users by role with pagination
     * @param role The role to filter by
     * @param page The page number (1-based)
     * @param size The page size
     * @param sortField The field to sort by
     * @param direction The sort direction
     * @return Paginated list of users with the specified role
     */
    @GetMapping("/users/role/{role}/paginated")
    public ResponseEntity<PagingResult<UserDTO>> getUsersByRolePaginated(
            @PathVariable UserRole role,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "id") String sortField,
            @RequestParam(defaultValue = "DESC") Sort.Direction direction) {
        PaginationRequest paginationRequest = new PaginationRequest(page, size, sortField, direction);
        return ResponseEntity.ok(adminService.getUsersByRolePaginated(role, paginationRequest));
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long userId) {
        return ResponseEntity.ok(adminService.getUserById(userId));
    }

    /**
     * Search users with pagination
     * @param searchTerm The term to search for
     * @param page The page number (1-based)
     * @param size The page size
     * @param sortField The field to sort by
     * @param direction The sort direction
     * @return Paginated list of users matching the search term
     */
    @GetMapping("/users/search/paginated")
    public ResponseEntity<PagingResult<UserDTO>> searchUsersPaginated(
            @RequestParam String searchTerm,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "id") String sortField,
            @RequestParam(defaultValue = "DESC") Sort.Direction direction) {
        PaginationRequest paginationRequest = new PaginationRequest(page, size, sortField, direction);
        return ResponseEntity.ok(adminService.searchUsersPaginated(searchTerm, paginationRequest));
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        adminService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    // Helper method to get authenticated admin ID
    private Long getAuthenticatedAdminId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null ||
                "anonymousUser".equals(authentication.getPrincipal())) {
            throw new InvalidCredentialsException("Admin not authenticated");
        }
        try {
            return Long.valueOf(authentication.getPrincipal().toString());
        } catch (NumberFormatException e) {
            throw new InvalidCredentialsException("Invalid admin ID format");
        }
    }

    @PutMapping("/password")
    public ResponseEntity<?> updatePassword(@Valid @RequestBody UpdatePasswordDto updatePasswordDto){
        try {
            Long adminId = getAuthenticatedAdminId();
            adminService.updateAdminPassword(adminId, updatePasswordDto);
            return ResponseEntity.ok("Admin password updated successfully");
        } catch (InvalidCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (AdminOperationException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
