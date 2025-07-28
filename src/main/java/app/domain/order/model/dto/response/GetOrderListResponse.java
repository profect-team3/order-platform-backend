package app.domain.order.model.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import app.domain.order.model.entity.Orders;
import app.domain.order.model.entity.enums.OrderStatus;
import app.domain.order.model.entity.enums.PaymentMethod;
import app.domain.order.model.entity.enums.ReceiptMethod;

public record GetOrderListResponse(
	UUID ordersId,
	String storeName,
	int totalPrice,
	String deliveryAddress,
	PaymentMethod paymentMethod,
	ReceiptMethod receiptMethod,
	OrderStatus orderStatus,
	boolean isRefundable,
	String requestMessage,
	LocalDateTime createdAt
) {
	public static GetOrderListResponse from(Orders orders) {
		return new GetOrderListResponse(
			orders.getOrdersId(),
			orders.getStore().getStoreName(),
			orders.getTotalPrice(),
			orders.getDeliveryAddress(),
			orders.getPaymentMethod(),
			orders.getReceiptMethod(),
			orders.getOrderStatus(),
			orders.isRefundable(),
			orders.getRequestMessage(),
			orders.getCreatedAt()
		);
	}
}
