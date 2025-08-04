package app.domain.menu.status;

import org.springframework.http.HttpStatus;

import app.global.apiPayload.code.BaseCode;
import app.global.apiPayload.code.ReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StoreMenuSuccessStatus implements BaseCode {

	_OK(HttpStatus.OK, "COMMON200", "성공입니다."),

	MENU_CREATED_SUCCESS(HttpStatus.CREATED, "MENU201", "메뉴가 성공적으로 생성되었습니다."),
	MENU_UPDATED_SUCCESS(HttpStatus.OK, "MENU202", "메뉴가 성공적으로 업데이트되었습니다."),
	MENU_DELETED_SUCCESS(HttpStatus.OK, "MENU203", "메뉴가 성공적으로 삭제되었습니다.");

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;

	@Override
	public ReasonDTO getReason() {
		return ReasonDTO.builder()
			.message(message)
			.code(code)
			.isSuccess(true)
			.build();
	}

	@Override
	public ReasonDTO getReasonHttpStatus() {
		return ReasonDTO.builder()
			.message(message)
			.code(code)
			.isSuccess(true)
			.httpStatus(httpStatus)
			.build();
	}
}

