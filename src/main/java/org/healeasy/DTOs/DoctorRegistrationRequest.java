package org.healeasy.DTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

/**
 * DTO for doctor pre-registration request.
 * Contains basic info, professional details, and references to supporting documents.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorRegistrationRequest {
    // Basic info
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{9,12}$", message = "Phone number must be between 9 and 12 digits")
    private String phoneNumber;

    // Professional details
    @NotBlank(message = "Specialization is required")
    private String specialization;

    @NotBlank(message = "License number is required")
    private String licenceNumber;

    // Supporting documents
    private MultipartFile licenseDocument;
    private MultipartFile idDocument;
}