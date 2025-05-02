package org.healeasy.DTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.healeasy.validation.AtLeastOneNotBlank;
import org.healeasy.validation.ProfileImageIsOptional;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AtLeastOneNotBlank(message = "Atleast on field(username, email) must be provided")
@ProfileImageIsOptional
public class UserProfileUpdateDTO {
    @Size(min = 4, max = 20, message = "Username must be between 4 and 20 characters.")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only contain alphanumeric characters and underscores.")
    private String username;

    @Email(message = "Enter a valid email.")
    private String email;

    private MultipartFile profileImage;
}
