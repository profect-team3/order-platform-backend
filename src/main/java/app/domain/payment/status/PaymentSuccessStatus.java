package app.domain.payment.status;

import org.springframework.http.HttpStatus;

import app.global.apiPayload.code.BaseCode;
import app.global.apiPayload.code.ReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PaymentSuccessStatus implements BaseCode {

	PAYMENT_CONFIRMED(HttpStatus.OK, "PAYMENT201", "결제가 성공적으로 승인되었습니다"),
	PAYMENT_FAIL_SAVED(HttpStatus.OK, "PAYMENT202", "결제 실패 정보가 저장되었습니다"),
	PAYMENT_CANCELLED(HttpStatus.OK, "PAYMENT203", "결제가 성공적으로 취소되었습니다");

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;

	@Override
	public ReasonDTO getReason() {
		return ReasonDTO.builder()
			.message(message)
			.code(code)
			.build();
	}

	@Override
	public ReasonDTO getReasonHttpStatus() {
		return ReasonDTO.builder()
			.message(message)
			.code(code)
			.httpStatus(httpStatus)
			.build();
	}
}
