package app.domain.payment.model.dto.request;

import java.util.UUID;

import app.domain.order.model.entity.enums.PaymentMethod;
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
public class CheckoutRequest {

	@NotNull(message = "주문 ID는 필수입니다.")
	private UUID orderId;

	@NotNull(message = "총 결제 금액은 필수입니다.")
	@Positive(message = "총 결제 금액은 양수여야 합니다.")
	private Long totalPrice;

	@NotNull(message = "결제 수단은 필수입니다.")
	private PaymentMethod paymentMethod;
}