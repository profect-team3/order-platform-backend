package app.domain.cart.status;

import org.springframework.http.HttpStatus;

import app.global.apiPayload.code.BaseCode;
import app.global.apiPayload.code.ReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CartErrorStatus implements BaseCode {

	CART_REDIS_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "CART001", "장바구니 Redis 저장에 실패했습니다."),
	CART_REDIS_LOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "CART002", "장바구니 Redis 조회에 실패했습니다."),
	CART_ITEM_PARSE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "CART003", "장바구니 아이템 파싱에 실패했습니다."),
	CART_DB_SYNC_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "CART005", "장바구니 DB 동기화에 실패했습니다."),
	INVALID_QUANTITY(HttpStatus.BAD_REQUEST, "CART006", "수량은 1 이상이어야 합니다.");

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