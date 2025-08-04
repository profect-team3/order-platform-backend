package app.global.validation.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import app.global.validation.validator.UserRoleValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = UserRoleValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidUserRole {
	String message() default "허용되지 않은 사용자 역할입니다. CUSTOMER 또는 OWNER만 가능합니다.";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}