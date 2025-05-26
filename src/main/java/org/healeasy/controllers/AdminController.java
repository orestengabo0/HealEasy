package org.healeasy.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.healeasy.DTOs.*;
import org.healeasy.Iservices.IAdminService;
import org.healeasy.Iservices.IDoctorService;
import org.healeasy.entities.Doctor;
import org.healeasy.enums.UserRole;
import org.healeasy.exceptions.InvalidCredentialsException;
import org.healeasy.mappers.DoctorMapper;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for admin-related endpoints.
 * Provides APIs for administrators to manage doctor approvals and other administrative tasks.
 */
@RestController
@RequestMapping("/api/v1/admin")
@AllArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final IDoctorService doctorService;
    private final DoctorMapper doctorMapper;
    private final IAdminService adminService;

    /**
     * Get all doctors with a specific status.
     * 
     * @param status The status to filter by (PENDING, APPROVED, REJECTED)
     * @return List of doctors with the specified status
     */
    @GetMapping("/doctors")
    public ResponseEntity<List<DoctorDto>> getDoctorsByStatus(
            @RequestParam(defaultValue = "PENDING") String status) {
        try {
            List<Doctor> doctors = doctorService.getDoctorsByStatus(status);
            List<DoctorDto> doctorDtos = doctors.stream()
                    .map(doctorMapper::toDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(doctorDtos);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get a doctor by ID.
     * 
     * @param doctorId The ID of the doctor
     * @return The doctor with the given ID
     */
    @GetMapping("/doctors/{doctorId}")
    public ResponseEntity<DoctorDto> getDoctorById(@PathVariable Long doctorId) {
        try {
            Doctor doctor = doctorService.getDoctorById(doctorId);
            return ResponseEntity.ok(doctorMapper.toDto(doctor));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Approve a doctor's registration request.
     * 
     * @param doctorId The ID of the doctor to approve
     * @return The updated doctor
     */
    @PostMapping("/doctors/{doctorId}/approve")
    public ResponseEntity<DoctorDto> approveDoctor(@PathVariable Long doctorId) {
        try {
            Doctor doctor = doctorService.approveDoctor(doctorId);
            return ResponseEntity.ok(doctorMapper.toDto(doctor));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Reject a doctor's registration request.
     * 
     * @param doctorId The ID of the doctor to reject
     * @param reason The reason for rejection (optional)
     * @return The updated doctor
     */
    @PostMapping("/doctors/{doctorId}/reject")
    public ResponseEntity<DoctorDto> rejectDoctor(
            @PathVariable Long doctorId,
            @RequestParam(required = false) String reason) {
        try {
            Doctor doctor = doctorService.rejectDoctor(doctorId, reason);
            return ResponseEntity.ok(doctorMapper.toDto(doctor));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Generate a new invitation code for a doctor.
     * 
     * @param doctorId The ID of the doctor
     * @param expirationDays Number of days until the code expires (default: 7)
     * @return The generated invitation code
     */
    @PostMapping("/doctors/{doctorId}/invitation-code")
    public ResponseEntity<String> generateInvitationCode(
            @PathVariable Long doctorId,
            @RequestParam(defaultValue = "7") int expirationDays) {
        try {
            var invitationCode = doctorService.generateInvitationCode(doctorId, expirationDays);
            return ResponseEntity.ok(invitationCode.getCode());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

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

    /**
     * Update admin password
     * @param updatePasswordDto The password update data
     * @return Success message or error
     */
    @PutMapping("/password")
    public ResponseEntity<?> updatePassword(@Valid @RequestBody UpdatePasswordDto updatePasswordDto) {
        try {
            Long adminId = getAuthenticatedAdminId();
            adminService.updateAdminPassword(adminId, updatePasswordDto);
            return ResponseEntity.ok("Admin password updated successfully");
        } catch (InvalidCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
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
}
