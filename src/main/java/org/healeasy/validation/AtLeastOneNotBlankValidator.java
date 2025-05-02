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
                    (dto.getProfileImage() != null && !dto.getProfileImage().isBlank());
        } else if (obj instanceof UserRegisterDTO dto) {
            File profileImage = dto.getProfileImage();
            if (profileImage != null) {
                if (!profileImage.exists() || !isValidImageFormat(profileImage)) {
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate("Invalid image format. Supported formats are: jpg, jpeg, png, webp.")
                            .addConstraintViolation();
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private boolean isValidImageFormat(File file) {
        String fileName = file.getName().toLowerCase();
        return VALID_IMAGE_EXTENSIONS.stream().anyMatch(fileName::endsWith);
    }
}