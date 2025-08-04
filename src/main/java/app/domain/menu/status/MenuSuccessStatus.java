package app.domain.menu.status;

import org.springframework.http.HttpStatus;

import app.global.apiPayload.code.BaseCode;
import app.global.apiPayload.code.ReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MenuSuccessStatus implements BaseCode {

	CUSTOMER_GET_STORE_MENU_LIST_OK(HttpStatus.OK, "CUSTOMER201", "사용자의 가게 메뉴 목록 조회가 성공했습니다.");


	private final HttpStatus httpStatus;
	private final String code;
	private final String message;

	@Override
	public ReasonDTO getReason() {
		return ReasonDTO.builder()
			.isSuccess(true)
			.message(message)
			.code(code)
			.build();
	}

	@Override
	public ReasonDTO getReasonHttpStatus() {
		return ReasonDTO.builder()
			.isSuccess(true)
			.message(message)
			.code(code)
			.httpStatus(httpStatus)
			.build();
	}
}
