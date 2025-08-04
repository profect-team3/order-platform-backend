package app.global.validation.validator;

import java.util.Arrays;
import java.util.List;

import app.domain.user.model.entity.enums.UserRole;
import app.global.validation.annotation.ValidUserRole;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UserRoleValidator implements ConstraintValidator<ValidUserRole, UserRole> {

	private final List<UserRole> allowedRoles = Arrays.asList(UserRole.CUSTOMER, UserRole.OWNER);

	@Override
	public boolean isValid(UserRole value, ConstraintValidatorContext context) {
		// null 값은 @NotNull 어노테이션이 처리하므로, 여기서는 null이 아닌 경우만 검사
		if (value == null) {
			return true;
		}
		return allowedRoles.contains(value);
	}
}