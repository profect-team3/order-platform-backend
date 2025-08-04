package app.domain.ai.model.dto.request;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Constraint(validatedBy = ConditionalNotNullValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ConditionalNotNull {
    String message() default "Menu name is required for this request type";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
