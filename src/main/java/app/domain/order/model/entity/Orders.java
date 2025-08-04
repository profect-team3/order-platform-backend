package app.domain.order.model.entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import app.domain.order.model.entity.enums.OrderChannel;
import app.domain.order.model.entity.enums.OrderStatus;
import app.domain.order.model.entity.enums.PaymentMethod;
import app.domain.order.model.entity.enums.ReceiptMethod;
import app.domain.store.model.entity.Store;
import app.domain.user.model.entity.User;
import app.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Orders extends BaseEntity {

	@Id
	@GeneratedValue
	private UUID ordersId;

	@ManyToOne
	@JoinColumn(name = "store_id", nullable = false)
	private Store store;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user; // nullable (오프라인 주문 고려)

	@Column(nullable = false)
	private Long totalPrice;

	@Column(nullable = false)
	private String deliveryAddress; // 오프라인,포장은 "없음"

	@Column(nullable = false, length = 50)
	@Enumerated(EnumType.STRING)
	private PaymentMethod paymentMethod;

	@Column(nullable = false, length = 20)
	@Enumerated(EnumType.STRING)
	private OrderChannel orderChannel;

	@Column(nullable = false, length = 20)
	@Enumerated(EnumType.STRING)
	private ReceiptMethod receiptMethod;

	@Column(nullable = false, length = 20)
	@Enumerated(EnumType.STRING)
	private OrderStatus orderStatus;

	@Column(nullable = false)
	private boolean isRefundable;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String orderHistory;

	private String requestMessage;

	public void updateOrderStatus(OrderStatus orderStatus) {
		this.orderStatus = orderStatus;
	}

	public void addHistory(String state, LocalDateTime dateTime) {
		String newEntry = state + ":" + dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

		if (this.orderHistory == null || this.orderHistory.toString().isEmpty()) {
			this.orderHistory = newEntry;
		} else {
			this.orderHistory = this.orderHistory.toString() + "\n" + newEntry;
		}
	}

	public void disableRefund() {
		this.isRefundable = false;
	}

	public void updateStatusAndHistory(OrderStatus newStatus, String updatedHistory) {
		this.orderStatus = newStatus;
		this.orderHistory = updatedHistory;
	}

}