package org.healeasy.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.healeasy.DTOs.UserProfileUpdateDTO;
import org.healeasy.DTOs.UserRegisterDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class ProfileImageIsOptionalValidator implements ConstraintValidator<ProfileImageIsOptional, Object> {
    private static final List<String> VALID_IMAGE_EXTENSIONS = List.of("jpg", "jpeg", "png", "webp");

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        MultipartFile profileImage = extractProfileImage(obj);
        if (profileImage != null) {
            if (profileImage.isEmpty() || !isValidImageFormat(profileImage)) {
                addConstraintViolation(context, "Invalid image format. Supported formats are: jpg, jpeg, png, webp.");
                return false;
            }
        }
        return true;
    }

    private MultipartFile extractProfileImage(Object obj) {
        if (obj instanceof UserRegisterDTO dto) {
            return dto.getProfileImage();
        } else if (obj instanceof UserProfileUpdateDTO dto) {
            return dto.getProfileImage();
        }
        return null;
    }

    private boolean isValidImageFormat(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            return false;
        }
        fileName = fileName.toLowerCase();
        return VALID_IMAGE_EXTENSIONS.stream().anyMatch(fileName::endsWith);
    }

    private void addConstraintViolation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }
}