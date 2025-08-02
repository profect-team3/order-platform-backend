package app.domain.cart.model.dto;

import java.util.UUID;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class AddCartItemRequest {

	@NotNull
	private UUID menuId;

	@NotNull
	private UUID storeId;

	@NotNull
	@Min(value = 1, message = "수량은 1 이상이어야 합니다.")
	private Integer quantity;
}