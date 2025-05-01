package org.healeasy.DTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import org.healeasy.validation.AtLeastOneNotBlank;

@Getter
@AtLeastOneNotBlank
public class UserLoginDTO {
    @Size(min = 4, max = 20, message = "Username must be between 4 and 20 characters.")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only contain alphanumeric characters and underscores.")
    private String username;

    @Email(message = "Enter a valid email.")
    private String email;

    @NotBlank(message = "Password is required.")
    @Size(min = 5, max = 20, message = "Password must be between 5 and 20 characters.")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
            message = "Password must contain at least one uppercase letter, one number, and one special character.")
    private String password;
}
