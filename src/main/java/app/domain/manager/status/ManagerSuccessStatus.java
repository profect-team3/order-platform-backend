package app.domain.manager.status;

import org.springframework.http.HttpStatus;

import app.global.apiPayload.code.BaseCode;
import app.global.apiPayload.code.ReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ManagerSuccessStatus implements BaseCode {

	MANAGER_GET_CUSTOMER_OK(HttpStatus.OK, "MANAGER200", "관리자의 유저 목록 조회가 성공했습니다."),
	MANAGER_GET_CUSTOMER_DETAIL_OK(HttpStatus.OK, "MANAGER201", "관리자의 유저 상세 조회가 성공했습니다."),
	MANAGER_GET_CUSTOMER_ORDER_OK(HttpStatus.OK, "MANAGER202", "관리자의 유저 주문 목록 조회가 성공했습니다."),
	MANAGER_SEARCH_CUSTOMER_OK(HttpStatus.OK, "MANAGER203", "관리자의 유저 검색이 성공했습니다."),
	MANAGER_GET_STORE_LIST_OK(HttpStatus.OK, "MANAGER204", "관리자의 가게 목록 조회가 성공했습니다."),
	MANAGER_GET_STORE_DETAIL_OK(HttpStatus.OK, "MANAGER205", "관리자의 가게 상세 조회가 성공했습니다."),
	MANAGER_UPDATE_STORE_STATUS_OK(HttpStatus.OK, "MANAGER206", "관리자의 가게 상태 수정이 성공했습니다."),
	MANAGER_SEARCH_STORE_OK(HttpStatus.OK, "MANAGER207", "관리자의 가게 검색이 성공했습니다.");

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
