package org.healeasy.DTOs;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.healeasy.enums.UserRole;
import org.healeasy.validation.ProfileImageIsOptional;
import org.springframework.web.multipart.MultipartFile;

@ProfileImageIsOptional
@Getter
@Setter
public class UserRegisterDTO {
    @NotBlank(message = "Username is required")
    @Size(min = 4, max = 20, message = "Username must be between 4 and 20 characters.")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only contain alphanumeric characters and underscores.")
    private String username;

    @Email(message = "Enter a valid email.")
    @NotBlank(message = "User email is required.")
    private String email;

    @NotBlank(message = "Phone number is required.")
    @Size(min = 9, max = 12, message = "Phone number must be between 9 and 12 characters.")
    @Pattern(regexp = "^[0-9]+$", message = "Phone number can only contain digits.")
    private String phoneNumber;

    private MultipartFile profileImage;

    @NotBlank(message = "Password is required.")
    @Size(min = 5, max = 20, message = "Password must be between 5 and 20 characters.")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
            message = "Password must contain at least one uppercase letter, one number, and one special character.")
    private String password;

    @NotNull(message = "Role is required.")
    private UserRole role;
}