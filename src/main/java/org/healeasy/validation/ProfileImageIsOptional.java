package org.healeasy.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ProfileImageIsOptionalValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ProfileImageIsOptional {
    String message() default "Profile image is optional";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
