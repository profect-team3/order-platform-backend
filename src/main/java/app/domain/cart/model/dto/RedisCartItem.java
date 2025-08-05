package app.domain.cart.model.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class RedisCartItem {

	@NotNull
	private UUID menuId;

	@NotNull
	private UUID storeId;

	@NotNull
	private int quantity;

	public RedisCartItem(UUID menuId, UUID storeId, int quantity) {
		this.menuId = menuId;
		this.storeId = storeId;
		this.quantity = quantity;
	}
}
