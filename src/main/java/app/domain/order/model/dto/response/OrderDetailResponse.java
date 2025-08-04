package app.domain.order.model.dto.response;

import java.util.List;

import app.domain.order.model.entity.OrderItem;
import app.domain.order.model.entity.Orders;
import app.domain.order.model.entity.enums.OrderChannel;
import app.domain.order.model.entity.enums.OrderStatus;
import app.domain.order.model.entity.enums.PaymentMethod;
import app.domain.order.model.entity.enums.ReceiptMethod;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class OrderDetailResponse {

	private String storeName;
	private List<Menu> menuList;
	private Long totalPrice;
	private String deliveryAddress;
	private PaymentMethod paymentMethod;
	private OrderChannel orderChannel;
	private ReceiptMethod receiptMethod;
	private OrderStatus orderStatus;
	private String requestMessage;

	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	@Builder
	public static class Menu {
		private String menuName;
		private int quantity;
		private Long price;

		public static Menu from(OrderItem orderItem) {
			return Menu.builder()
				.menuName(orderItem.getMenuName())
				.quantity(orderItem.getQuantity())
				.price(orderItem.getPrice())
				.build();
		}
	}

	public static OrderDetailResponse from(Orders orders, List<OrderItem> orderItems) {
		return OrderDetailResponse.builder()
			.storeName(orders.getStore().getStoreName())
			.menuList(orderItems.stream().map(Menu::from).toList())
			.totalPrice(orders.getTotalPrice())
			.deliveryAddress(orders.getDeliveryAddress())
			.paymentMethod(orders.getPaymentMethod())
			.orderChannel(orders.getOrderChannel())
			.receiptMethod(orders.getReceiptMethod())
			.orderStatus(orders.getOrderStatus())
			.requestMessage(orders.getRequestMessage())
			.build();
	}
}
