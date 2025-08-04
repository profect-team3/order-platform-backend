package app.domain.user.model.dto.request;

import app.domain.user.model.entity.enums.UserRole;
import app.global.validation.annotation.ValidUserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserRequest {

	@NotBlank
	private String username;

	@NotBlank
	@Size(min = 8, max = 20)
	private String password;

	@NotBlank
	@Email
	private String email;

	@NotBlank
	@Size(min = 2, max = 10)
	private String nickname;

	@NotBlank
	@Size(min = 2, max = 50)
	private String realName;

	@NotBlank
	@Pattern(regexp = "^\\d{10,11}$")
	private String phoneNumber;

	@NotNull
	@ValidUserRole
	private UserRole userRole;
}