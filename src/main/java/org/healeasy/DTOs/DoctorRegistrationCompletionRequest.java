package org.healeasy.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

/**
 * DTO for completing doctor registration after receiving an invitation code.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorRegistrationCompletionRequest {
    
    /**
     * The invitation code received by email.
     */
    @NotBlank(message = "Invitation code is required")
    private String invitationCode;
    
    /**
     * The new password for the doctor's account.
     */
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$", 
             message = "Password must contain at least one digit, one lowercase, one uppercase, one special character, and no whitespace")
    private String password;
    
    /**
     * Confirmation of the new password.
     */
    @NotBlank(message = "Password confirmation is required")
    private String passwordConfirmation;
    
    /**
     * Optional profile photo for the doctor.
     */
    private MultipartFile profilePhoto;
}