package dev.hireben.spring.reactive.web.demo.storage.annotation.api;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import dev.hireben.spring.reactive.web.demo.storage.annotation.ValidFileIdValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Target({ ElementType.PARAMETER, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidFileIdValidator.class)
public @interface ValidFileId {

  String message() default "{storage.error.id_validation_default}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

}
