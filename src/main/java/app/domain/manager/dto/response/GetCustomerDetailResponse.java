package app.domain.manager.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import app.domain.customer.dto.response.GetCustomerAddressListResponse;
import app.domain.user.model.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetCustomerDetailResponse {

	private Long userId;
	private String email;
	private String userName;
	private String name;
	private String nickName;
	private String phoneNumber;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private List<GetCustomerAddressListResponse> address;

	public static GetCustomerDetailResponse from(User user, List<GetCustomerAddressListResponse> addressList) {
		return new GetCustomerDetailResponse(
			user.getUserId(),
			user.getEmail(),
			user.getUsername(),
			user.getRealName(),
			user.getNickname(),
			user.getPhoneNumber(),
			user.getCreatedAt(),
			user.getUpdatedAt(),
			addressList
		);
	}
}