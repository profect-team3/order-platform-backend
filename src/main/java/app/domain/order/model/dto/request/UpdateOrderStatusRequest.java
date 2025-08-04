package app.domain.order.model.dto.request;

import app.domain.order.model.entity.enums.OrderStatus;
import lombok.Getter;

@Getter
public class UpdateOrderStatusRequest {
	private OrderStatus newStatus;
}