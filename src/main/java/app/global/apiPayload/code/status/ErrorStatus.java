package app.global.apiPayload.code.status;

import org.springframework.http.HttpStatus;

import app.global.apiPayload.code.BaseCode;
import app.global.apiPayload.code.ReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseCode {

	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "GLOBAL001", "존재하지 않는 사용자입니다."),
	STORE_NOT_FOUND(HttpStatus.NOT_FOUND, "STORE004", "해당 가맹점을 찾을 수 없습니다."),
	CART_NOT_FOUND(HttpStatus.NOT_FOUND, "CART004", "장바구니를 찾을 수 없습니다."),
	MENU_NOT_FOUND(HttpStatus.NOT_FOUND, "ORDER004", "메뉴를 찾을 수 없습니다."),
	ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "ORDER006", "주문을 찾을 수 없습니다."),
	PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "PAYMENT005", "결제내역을 찾을 수 없습니다."),

	_INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
	_BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400", "잘못된 요청입니다."),
	_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON401", "인증이 필요합니다."),
	_FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다.");

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