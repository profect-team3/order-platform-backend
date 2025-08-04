package app.domain.manager.dto.response;

import java.util.UUID;

import app.domain.store.model.entity.Store;
import app.domain.user.model.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "가게 상세 응답 DTO")
@Getter
@AllArgsConstructor
public class GetStoreDetailResponse {

	private UUID storeId;
	private String storeName;
	private String description;
	private String address;
	private String phoneNumber;
	private long minOrderAmount;
	private String regionName;
	private String categoryName;
	private double averageRating;

	private Long ownerId;
	private String ownerEmail;
	private String ownerName;
	private String ownerRealName;
	private String ownerPhone;


	public static GetStoreDetailResponse from(Store store, double avgRating) {
		User user = store.getUser();

		return new GetStoreDetailResponse(
			store.getStoreId(),
			store.getStoreName(),
			store.getDescription(),
			store.getAddress(),
			store.getPhoneNumber(),
			store.getMinOrderAmount(),
			store.getRegion().getRegionName(),
			store.getCategory().getCategoryName(),
			avgRating,
			user.getUserId(),
			user.getEmail(),
			user.getUsername(),
			user.getNickname(),
			user.getPhoneNumber()
		);
	}
}