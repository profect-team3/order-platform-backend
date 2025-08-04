package app.domain.order.model.dto.response;

import java.util.UUID;

import app.domain.order.model.entity.Orders;
import app.domain.order.model.entity.enums.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateOrderStatusResponse {

	@Schema(description = "주문 ID")
	private UUID orderId;

	@Schema(description = "업데이트된 주문 상태")
	private OrderStatus updatedStatus;

	public static UpdateOrderStatusResponse from(Orders order) {
		return UpdateOrderStatusResponse.builder()
			.orderId(order.getOrdersId())
			.updatedStatus(order.getOrderStatus())
			.build();
	}
}