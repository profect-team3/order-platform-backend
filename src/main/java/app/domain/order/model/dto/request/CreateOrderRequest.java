package app.domain.order.model.dto.request;

import app.domain.order.model.entity.enums.OrderChannel;
import app.domain.order.model.entity.enums.PaymentMethod;
import app.domain.order.model.entity.enums.ReceiptMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CreateOrderRequest {

	@NotNull(message = "결제 방법은 필수입니다.")
	private PaymentMethod paymentMethod;

	@NotNull(message = "주문 채널은 필수입니다.")
	private OrderChannel orderChannel;

	@NotNull(message = "수령 방법은 필수입니다.")
	private ReceiptMethod receiptMethod;

	private String requestMessage;

	@NotNull(message = "총 금액은 필수입니다.")
	@Positive(message = "총 금액은 양의 정수여야 합니다.")
	private Long totalPrice;

	@NotBlank(message = "배송 주소는 필수입니다.")
	private String deliveryAddress;
}
