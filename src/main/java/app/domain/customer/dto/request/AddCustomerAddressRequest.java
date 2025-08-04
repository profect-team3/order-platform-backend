package app.domain.customer.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class AddCustomerAddressRequest {
	@NotBlank(message="Alias is required")
	private String alias;

	@NotBlank(message="Address is required")
	private String address;

	@NotBlank(message="AddressDetail is required")
	private String addressDetail;

	private boolean isDefault;

	public AddCustomerAddressRequest() {
	}

	public AddCustomerAddressRequest(String alias, String address, String addressDetail, boolean isDefault) {
		this.alias = alias;
		this.address = address;
		this.addressDetail = addressDetail;
		this.isDefault = isDefault;
	}
}