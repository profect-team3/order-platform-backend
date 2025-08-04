package app.domain.customer.dto.response;

import java.util.UUID;
import app.domain.store.model.entity.Store;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetStoreListResponse {

	private UUID storeId;
	private String storeName;
	private String address;
	private long minOrderAmount;
	private double averageRating;

	public static GetStoreListResponse from(Store store, double averageRating) {
		return GetStoreListResponse.builder()
			.storeId(store.getStoreId())
			.storeName(store.getStoreName())
			.address(store.getAddress())
			.minOrderAmount(store.getMinOrderAmount())
			.averageRating(averageRating)
			.build();
	}
}