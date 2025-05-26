package org.healeasy.Iservices;

import org.healeasy.DTOs.DoctorRegistrationRequest;
import org.healeasy.entities.Doctor;
import org.healeasy.entities.InvitationCode;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Service interface for doctor-related operations.
 */
public interface IDoctorService {

    /**
     * Register a new doctor with pending status.
     * Creates a new User with PENDING_DOCTOR role and a Doctor entity with PENDING status.
     * 
     * @param registrationRequest The doctor registration request
     * @return The created Doctor entity
     */
    Doctor registerDoctor(DoctorRegistrationRequest registrationRequest);

    /**
     * Upload a document for a doctor.
     * 
     * @param doctorId The ID of the doctor
     * @param documentType The type of document (license or ID)
     * @param file The document file
     * @return The URL of the uploaded document
     */
    String uploadDocument(Long doctorId, String documentType, MultipartFile file);

    /**
     * Get a doctor by ID.
     * 
     * @param doctorId The ID of the doctor
     * @return The doctor with the given ID
     */
    Doctor getDoctorById(Long doctorId);

    /**
     * Check if a license number is already in use.
     * 
     * @param licenseNumber The license number to check
     * @return True if the license number is already in use, false otherwise
     */
    boolean isLicenseNumberInUse(String licenseNumber);

    /**
     * Approve a doctor's registration request.
     * Changes the doctor's status from PENDING to APPROVED,
     * generates an invitation code, and sends an email to the doctor.
     * 
     * @param doctorId The ID of the doctor to approve
     * @return The updated Doctor entity
     */
    Doctor approveDoctor(Long doctorId);

    /**
     * Reject a doctor's registration request.
     * Changes the doctor's status from PENDING to REJECTED.
     * 
     * @param doctorId The ID of the doctor to reject
     * @param reason The reason for rejection (optional)
     * @return The updated Doctor entity
     */
    Doctor rejectDoctor(Long doctorId, String reason);

    /**
     * Generate a new invitation code for a doctor.
     * 
     * @param doctorId The ID of the doctor
     * @param expirationDays Number of days until the code expires
     * @return The generated InvitationCode entity
     */
    InvitationCode generateInvitationCode(Long doctorId, int expirationDays);

    /**
     * Validate an invitation code.
     * 
     * @param code The invitation code to validate
     * @return The Doctor entity associated with the code if valid, null otherwise
     */
    Doctor validateInvitationCode(String code);

    /**
     * Complete a doctor's registration using an invitation code.
     * Sets the doctor's password, uploads a profile photo if provided,
     * and changes the user role from PENDING_DOCTOR to DOCTOR.
     * 
     * @param code The invitation code
     * @param password The new password
     * @param profilePhoto The profile photo (optional)
     * @return The updated Doctor entity
     */
    Doctor completeRegistration(String code, String password, MultipartFile profilePhoto);

    /**
     * Get all doctors with a specific status.
     * 
     * @param status The status to filter by (PENDING, APPROVED, REJECTED)
     * @return List of doctors with the specified status
     */
    List<Doctor> getDoctorsByStatus(String status);
}
