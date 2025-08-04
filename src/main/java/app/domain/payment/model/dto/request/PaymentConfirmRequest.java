package app.domain.payment.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PaymentConfirmRequest {

	@NotBlank(message = "결제 키는 필수입니다.")
	private String paymentKey;

	@NotBlank(message = "주문 ID는 필수입니다.")
	private String orderId;

	@NotBlank(message = "결제 금액은 필수입니다.")
	@Pattern(regexp = "^[1-9]\\d*$", message = "결제 금액은 양수여야 합니다.")
	private String amount;
}