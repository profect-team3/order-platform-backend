package app.domain.user.model.dto.response;

import app.domain.user.model.entity.UserAddress;

public record GetUserAddressListResponse(
	String alias,
	String address,
	String addressDetail,
	boolean isDefault
) {
	public static GetUserAddressListResponse from(UserAddress address) {
		return new GetUserAddressListResponse(
			address.getAlias(),
			address.getAddress(),
		    address.getAddressDetail(),
			address.isDefault()
		);
	}
}