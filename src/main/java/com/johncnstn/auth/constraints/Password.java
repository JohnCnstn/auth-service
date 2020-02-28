package com.johncnstn.auth.constraints;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = PasswordValidator.class)
@NotBlank
@ReportAsSingleViolation
@Retention(RUNTIME)
@Size(min = 6, max = 256)
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
public @interface Password {

    @SuppressWarnings("checkstyle:LineLength")
    String message() default "must contain at least 6 characters, including uppercase characters (A-Z), lowercase characters (a-z) and digits (0-9)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
