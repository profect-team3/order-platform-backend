package app.domain.cart.status;

import org.springframework.http.HttpStatus;

import app.global.apiPayload.code.BaseCode;
import app.global.apiPayload.code.ReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CartSuccessStatus implements BaseCode {
	CART_ITEM_ADDED(HttpStatus.OK, "CART201", "장바구니에 아이템이 추가되었습니다"),
	CART_ITEM_UPDATED(HttpStatus.OK, "CART202", "장바구니 아이템 수량이 수정되었습니다"),
	CART_ITEM_REMOVED(HttpStatus.OK, "CART203", "장바구니에서 아이템이 삭제되었습니다"),
	CART_RETRIEVED(HttpStatus.OK, "CART204", "장바구니 조회가 완료되었습니다"),
	CART_CLEARED(HttpStatus.OK, "CART205", "장바구니가 전체 삭제되었습니다");

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
