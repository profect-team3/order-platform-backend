package app.domain.manager.dto.response;

import java.time.LocalDateTime;

import app.domain.user.model.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GetCustomerListResponse {

	private Long id;
	private String email;
	private String name;
	private LocalDateTime createdAt;

	public static GetCustomerListResponse from(User user) {
		return new GetCustomerListResponse(
			user.getUserId(),
			user.getEmail(),
			user.getUsername(),
			user.getCreatedAt()
		);
	}
}