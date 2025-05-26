package org.healeasy.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.healeasy.DTOs.DoctorRegistrationRequest;
import org.healeasy.Iservices.IDoctorService;
import org.healeasy.Iservices.IEmailService;
import org.healeasy.entities.Doctor;
import org.healeasy.entities.InvitationCode;
import org.healeasy.entities.User;
import org.healeasy.enums.DoctorStatus;
import org.healeasy.enums.UserRole;
import org.healeasy.exceptions.DoctorNotFoundException;
import org.healeasy.exceptions.EmailAlreadyExistsException;
import org.healeasy.exceptions.FailedToUploadImageException;
import org.healeasy.exceptions.PhoneNumberAlreadyExistsException;
import org.healeasy.repositories.DoctorRepository;
import org.healeasy.repositories.InvitationCodeRepository;
import org.healeasy.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of the IDoctorService interface.
 * Handles business logic for doctor-related operations.
 */
@Service
@AllArgsConstructor
@Slf4j
public class DoctorServiceImpl implements IDoctorService {

    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final InvitationCodeRepository invitationCodeRepository;
    private final PasswordEncoder passwordEncoder;
    private final CloudinaryServiceImpl cloudinaryService;
    private final IEmailService emailService;

    /**
     * Register a new doctor with pending status.
     * Creates a new User with PENDING_DOCTOR role and a Doctor entity with PENDING status.
     * Also automatically approves the doctor and sends an invitation email.
     * 
     * @param registrationRequest The doctor registration request
     * @return The created Doctor entity
     */
    @Override
    @Transactional
    public Doctor registerDoctor(DoctorRegistrationRequest registrationRequest) {
        // Check if email already exists
        if (userRepository.existsByEmail(registrationRequest.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        // Check if phone number already exists
        if (userRepository.existsByPhoneNumber(registrationRequest.getPhoneNumber())) {
            throw new PhoneNumberAlreadyExistsException("Phone number already exists");
        }

        // Check if license number already exists
        if (doctorRepository.existsByLicenceNumber(registrationRequest.getLicenceNumber())) {
            throw new IllegalArgumentException("License number already exists");
        }

        // Create user with PENDING_DOCTOR role
        User user = new User();
        user.setUsername(registrationRequest.getUsername());
        user.setEmail(registrationRequest.getEmail());
        user.setPhoneNumber(registrationRequest.getPhoneNumber());
        user.setRole(UserRole.PENDING_DOCTOR);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        // Set default profile image
        String USER_DEFAULT_AVATAR = "https://upload.wikimedia.org/wikipedia/commons/7/7c/Profile_avatar_placeholder_large.png";
        user.setProfileImageUrl(USER_DEFAULT_AVATAR);

        // Save user
        userRepository.save(user);

        // Create doctor with APPROVED status (automatically approve)
        Doctor doctor = new Doctor();
        doctor.setUser(user);
        doctor.setSpecialization(registrationRequest.getSpecialization());
        doctor.setLicenceNumber(registrationRequest.getLicenceNumber());
        doctor.setStatus(DoctorStatus.APPROVED); // Set status to APPROVED instead of PENDING
        doctor.setCreatedAt(LocalDateTime.now());
        doctor.setUpdatedAt(LocalDateTime.now());

        // Upload license document if provided
        if (registrationRequest.getLicenseDocument() != null) {
            try {
                String licenseDocUrl = cloudinaryService.uploadDocument(registrationRequest.getLicenseDocument());
                doctor.setLicenseDocumentUrl(licenseDocUrl);
            } catch (IOException e) {
                throw new FailedToUploadImageException("Failed to upload license document");
            }
        }

        // Upload ID document if provided
        if (registrationRequest.getIdDocument() != null) {
            try {
                String idDocUrl = cloudinaryService.uploadDocument(registrationRequest.getIdDocument());
                doctor.setIdDocumentUrl(idDocUrl);
            } catch (IOException e) {
                throw new FailedToUploadImageException("Failed to upload ID document");
            }
        }

        // Save doctor
        doctorRepository.save(doctor);

        // Send application submission confirmation email
        boolean submissionEmailSent = emailService.sendDoctorApplicationSubmissionEmail(
            user.getEmail(),
            user.getUsername()
        );

        if (!submissionEmailSent) {
            // Log the error but don't throw an exception to allow the registration process to continue
            log.error("Failed to send application submission email to doctor: {}", user.getEmail());
        } else {
            log.info("Application submission email sent to doctor: {}", user.getEmail());
        }

        // Generate invitation code and send email
        InvitationCode invitationCode = generateInvitationCode(doctor.getId(), 7); // 7 days expiration

        // Send invitation email
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String expirationDate = invitationCode.getExpirationDate().format(formatter);

        boolean emailSent = emailService.sendDoctorInvitationEmail(
            user.getEmail(),
            user.getUsername(),
            invitationCode.getCode(),
            expirationDate
        );

        if (!emailSent) {
            // Log the error but don't throw an exception to allow the registration process to continue
            log.error("Failed to send invitation email to doctor: {}", user.getEmail());
        } else {
            log.info("Invitation email sent to doctor: {}", user.getEmail());
        }

        return doctor;
    }

    /**
     * Upload a document for a doctor.
     * 
     * @param doctorId The ID of the doctor
     * @param documentType The type of document (license or ID)
     * @param file The document file
     * @return The URL of the uploaded document
     */
    @Override
    @Transactional
    public String uploadDocument(Long doctorId, String documentType, MultipartFile file) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor not found"));

        try {
            String documentUrl = cloudinaryService.uploadDocument(file);

            if ("license".equalsIgnoreCase(documentType)) {
                doctor.setLicenseDocumentUrl(documentUrl);
            } else if ("id".equalsIgnoreCase(documentType)) {
                doctor.setIdDocumentUrl(documentUrl);
            } else {
                throw new IllegalArgumentException("Invalid document type. Must be 'license' or 'id'");
            }

            doctor.setUpdatedAt(LocalDateTime.now());
            doctorRepository.save(doctor);

            return documentUrl;
        } catch (IOException e) {
            throw new FailedToUploadImageException("Failed to upload document");
        }
    }

    /**
     * Get a doctor by ID.
     * 
     * @param doctorId The ID of the doctor
     * @return The doctor with the given ID
     */
    @Override
    public Doctor getDoctorById(Long doctorId) {
        return doctorRepository.findById(doctorId)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor not found"));
    }

    /**
     * Check if a license number is already in use.
     * 
     * @param licenseNumber The license number to check
     * @return True if the license number is already in use, false otherwise
     */
    @Override
    public boolean isLicenseNumberInUse(String licenseNumber) {
        return doctorRepository.existsByLicenceNumber(licenseNumber);
    }

    /**
     * Approve a doctor's registration request.
     * Changes the doctor's status from PENDING to APPROVED,
     * generates an invitation code, and sends an email to the doctor.
     * 
     * @param doctorId The ID of the doctor to approve
     * @return The updated Doctor entity
     */
    @Override
    @Transactional
    public Doctor approveDoctor(Long doctorId) {
        Doctor doctor = getDoctorById(doctorId);

        // Check if doctor is in PENDING status
        if (doctor.getStatus() != DoctorStatus.PENDING) {
            throw new IllegalStateException("Doctor is not in PENDING status");
        }

        // Update doctor status to APPROVED
        doctor.setStatus(DoctorStatus.APPROVED);
        doctor.setUpdatedAt(LocalDateTime.now());
        doctorRepository.save(doctor);

        // Generate invitation code
        InvitationCode invitationCode = generateInvitationCode(doctorId, 7); // 7 days expiration

        // Send invitation email
        User user = doctor.getUser();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String expirationDate = invitationCode.getExpirationDate().format(formatter);

        boolean emailSent = emailService.sendDoctorInvitationEmail(
            user.getEmail(),
            user.getUsername(),
            invitationCode.getCode(),
            expirationDate
        );

        if (!emailSent) {
            // Log the error but don't throw an exception to allow the approval process to continue
            // This way, the doctor is still approved even if the email fails to send
            // The admin can generate a new invitation code later if needed
            System.out.println("Failed to send invitation email to doctor: " + user.getEmail());
        }

        return doctor;
    }

    /**
     * Reject a doctor's registration request.
     * Changes the doctor's status from PENDING to REJECTED.
     * 
     * @param doctorId The ID of the doctor to reject
     * @param reason The reason for rejection (optional)
     * @return The updated Doctor entity
     */
    @Override
    @Transactional
    public Doctor rejectDoctor(Long doctorId, String reason) {
        Doctor doctor = getDoctorById(doctorId);

        // Check if doctor is in PENDING status
        if (doctor.getStatus() != DoctorStatus.PENDING) {
            throw new IllegalStateException("Doctor is not in PENDING status");
        }

        // Update doctor status to REJECTED
        doctor.setStatus(DoctorStatus.REJECTED);
        doctor.setUpdatedAt(LocalDateTime.now());
        doctorRepository.save(doctor);

        // Send rejection email with reason
        User user = doctor.getUser();
        boolean rejectionEmailSent = emailService.sendDoctorApplicationRejectionEmail(
            user.getEmail(),
            user.getUsername(),
            reason
        );

        if (!rejectionEmailSent) {
            // Log the error but don't throw an exception to allow the rejection process to continue
            log.error("Failed to send rejection email to doctor: {}", user.getEmail());
        } else {
            log.info("Rejection email sent to doctor: {}", user.getEmail());
        }

        return doctor;
    }

    /**
     * Generate a new invitation code for a doctor.
     * 
     * @param doctorId The ID of the doctor
     * @param expirationDays Number of days until the code expires
     * @return The generated InvitationCode entity
     */
    @Override
    @Transactional
    public InvitationCode generateInvitationCode(Long doctorId, int expirationDays) {
        Doctor doctor = getDoctorById(doctorId);

        // Generate a unique code
        String code = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();

        // Create invitation code entity
        InvitationCode invitationCode = new InvitationCode();
        invitationCode.setCode(code);
        invitationCode.setDoctor(doctor);
        invitationCode.setUsed(false);
        invitationCode.setExpirationDate(LocalDateTime.now().plusDays(expirationDays));

        // Save invitation code
        return invitationCodeRepository.save(invitationCode);
    }

    /**
     * Validate an invitation code.
     * 
     * @param code The invitation code to validate
     * @return The Doctor entity associated with the code if valid, null otherwise
     */
    @Override
    public Doctor validateInvitationCode(String code) {
        return invitationCodeRepository.findByCode(code)
                .filter(InvitationCode::isValid)
                .map(InvitationCode::getDoctor)
                .orElse(null);
    }

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
    @Override
    @Transactional
    public Doctor completeRegistration(String code, String password, MultipartFile profilePhoto) {
        // Validate invitation code
        InvitationCode invitationCode = invitationCodeRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Invalid invitation code"));

        if (!invitationCode.isValid()) {
            throw new IllegalArgumentException("Invitation code is expired or already used");
        }

        Doctor doctor = invitationCode.getDoctor();
        User user = doctor.getUser();

        // Update password
        user.setPassword(passwordEncoder.encode(password));

        // Upload profile photo if provided
        if (profilePhoto != null && !profilePhoto.isEmpty()) {
            try {
                String profileImageUrl = cloudinaryService.uploadImage(profilePhoto);
                user.setProfileImageUrl(profileImageUrl);
            } catch (IOException e) {
                throw new FailedToUploadImageException("Failed to upload profile photo");
            }
        }

        // Change user role from PENDING_DOCTOR to DOCTOR
        user.setRole(UserRole.DOCTOR);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        // Mark invitation code as used
        invitationCode.setUsed(true);
        invitationCode.setUpdatedAt(LocalDateTime.now());
        invitationCodeRepository.save(invitationCode);

        return doctor;
    }

    /**
     * Get all doctors with a specific status.
     * 
     * @param status The status to filter by (PENDING, APPROVED, REJECTED)
     * @return List of doctors with the specified status
     */
    @Override
    public List<Doctor> getDoctorsByStatus(String status) {
        try {
            DoctorStatus doctorStatus = DoctorStatus.valueOf(status.toUpperCase());
            return doctorRepository.findByStatus(doctorStatus);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid doctor status: " + status);
        }
    }
}
