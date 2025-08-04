package app.domain.customer.dto.response;

import app.domain.user.model.entity.UserAddress;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "유저 주소 목록 응답 DTO")
public record GetCustomerAddressListResponse(
	String alias,
	String address,
	String addressDetail,
	boolean isDefault
) {
	public static GetCustomerAddressListResponse from(UserAddress address) {
		return new GetCustomerAddressListResponse(
			address.getAlias(),
			address.getAddress(),
			address.getAddressDetail(),
			address.isDefault()
		);
	}
}