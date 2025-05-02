package org.healeasy.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.healeasy.DTOs.UserLoginDTO;
import org.healeasy.DTOs.UserProfileUpdateDTO;
import org.healeasy.DTOs.UserRegisterDTO;

import java.io.File;
import java.util.List;

public class AtLeastOneNotBlankValidator implements ConstraintValidator<AtLeastOneNotBlank, Object> {
    private static final List<String> VALID_IMAGE_EXTENSIONS = List.of("jpg", "jpeg", "png", "webp");

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        if (obj instanceof UserLoginDTO dto) {
            return (dto.getUsername() != null && !dto.getUsername().isBlank()) ||
                    (dto.getEmail() != null && !dto.getEmail().isBlank());
        } else if (obj instanceof UserProfileUpdateDTO dto) {
            return (dto.getUsername() != null && !dto.getUsername().isBlank()) ||
                    (dto.getEmail() != null && !dto.getEmail().isBlank()) ||
                    (dto.getProfileImage() != null);
        }
        return false;
    }
}