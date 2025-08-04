package app.domain.order.status;

import org.springframework.http.HttpStatus;

import app.global.apiPayload.code.BaseCode;
import app.global.apiPayload.code.ReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderErrorStatus implements BaseCode {

	ORDER_CREATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "ORDER001", "주문 생성에 실패했습니다."),
	INVALID_ORDER_REQUEST(HttpStatus.BAD_REQUEST, "ORDER002", "유효하지 않은 주문 요청입니다."),
	ORDER_DIFFERENT_STORE(HttpStatus.BAD_REQUEST, "ORDER003", "서로 다른 매장의 메뉴는 함께 주문할 수 없습니다."),
	INVALID_TOTAL_PRICE(HttpStatus.BAD_REQUEST, "ORDER004", "총 금액은 양의 정수입니다."),
	ORDER_PRICE_MISMATCH(HttpStatus.BAD_REQUEST, "ORDER005", "요청 총액과 장바구니 아이템 총액이 일치하지 않습니다."),
	ORDER_ACCESS_DENIED(HttpStatus.FORBIDDEN, "ORDER006", "해당 주문에 대한 접근 권한이 없습니다."),
	INVALID_ORDER_STATUS_TRANSITION(HttpStatus.BAD_REQUEST, "ORDER007", "유효하지 않은 주문 상태 전환입니다.");

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
			.isSuccess(false)
			.message(message)
			.code(code)
			.httpStatus(httpStatus)
			.build();
	}
}