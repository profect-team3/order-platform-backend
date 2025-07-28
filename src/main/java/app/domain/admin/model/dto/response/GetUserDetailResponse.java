package app.domain.admin.model.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import app.domain.user.model.dto.response.GetUserAddressListResponse;
import app.domain.user.model.entity.User;

public record GetUserDetailResponse(
	Long userId,
	String email,
	String name,
	String nickName,
	String phoneNumber,
	LocalDateTime createdAt,
	LocalDateTime updatedAt,
	List<GetUserAddressListResponse> address
) {
	public static GetUserDetailResponse from(
		User user,
		List<GetUserAddressListResponse> addressList
	) {
		return new GetUserDetailResponse(
			user.getUserId(),
			user.getEmail(),
			user.getUsername(),
			user.getNickname(),
			user.getPhoneNumber(),
			user.getCreatedAt(),
			user.getUpdatedAt(),
			addressList
		);
	}
}