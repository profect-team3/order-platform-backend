package app.domain.menu.status;

import org.springframework.http.HttpStatus;

import app.global.apiPayload.code.BaseCode;
import app.global.apiPayload.code.ReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StoreMenuErrorCode implements BaseCode {

	MENU_NOT_FOUND(HttpStatus.NOT_FOUND, "MENU001", "해당 메뉴를 찾을 수 없습니다."),
	STORE_NOT_FOUND_FOR_MENU(HttpStatus.NOT_FOUND, "MENU002", "메뉴를 등록할 가게를 찾을 수 없습니다."),
	MENU_ALREADY_DELETED(HttpStatus.BAD_REQUEST, "MENU003", "이미 삭제된 메뉴입니다."),
	MENU_NAME_DUPLICATE(HttpStatus.CONFLICT, "MENU004", "해당 가게에 동일한 이름의 메뉴가 이미 존재합니다."),
	MENU_NAME_NULL(HttpStatus.BAD_REQUEST, "MENU005", "메뉴 이름은 null일 수 없습니다."),
	MENU_PRICE_NULL(HttpStatus.BAD_REQUEST, "MENU006", "메뉴 가격은 null일 수 없습니다."),
	MENU_PRICE_INVALID(HttpStatus.BAD_REQUEST, "MENU007", "메뉴 가격은 0보다 커야 합니다."),
	MENU_ID_NULL(HttpStatus.BAD_REQUEST, "MENU008", "메뉴 ID는 null일 수 없습니다."),
	USER_NOT_FOUND_FOR_MENU(HttpStatus.NOT_FOUND, "MENU009", "메뉴 관련 작업을 수행할 사용자를 찾을 수 없습니다.");

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