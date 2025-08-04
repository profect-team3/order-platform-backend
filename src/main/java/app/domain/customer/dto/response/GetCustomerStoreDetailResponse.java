package app.domain.customer.dto.response;

import java.util.UUID;

import app.domain.store.model.entity.Store;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetCustomerStoreDetailResponse {

	private UUID storeId;
	private String storeName;
	private String description;
	private String address;
	private String phoneNumber;
	private long minOrderAmount;
	private String categoryName;
	private double averageRating;

	public static GetCustomerStoreDetailResponse from(Store store, double avgRating) {
		return new GetCustomerStoreDetailResponse(
			store.getStoreId(),
			store.getStoreName(),
			store.getDescription(),
			store.getAddress(),
			store.getPhoneNumber(),
			store.getMinOrderAmount(),
			store.getCategory().getCategoryName(),
			avgRating
		);
	}
}
