package app.domain.customer.status;

import org.springframework.http.HttpStatus;

import app.global.apiPayload.code.BaseCode;
import app.global.apiPayload.code.ReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CustomerSuccessStatus implements BaseCode {

	CUSTOMER_OK(HttpStatus.OK, "CUSTOMER200", "커스터머의 오더조회가 성공했습니다."),
	ADDRESS_LIST_FOUND(HttpStatus.OK, "ADDRESS_200", "주소 목록 조회가 완료되었습니다."),
	ADDRESS_ADDED(HttpStatus.OK, "ADDRESS_200", "주소 등록이 완료되었습니다."),

	CUSTOMER_GET_STORE_LIST_OK(HttpStatus.OK, "CUSTOMER200", "사용자의 가게 목록 조회가 성공했습니다."),
	CUSTOMER_GET_STORE_DETAIL_OK(HttpStatus.OK, "CUSTOMER201", "사용자의 가게 상세 조회가 성공했습니다."),
	CUSTOMER_SEARCH_STORE_OK(HttpStatus.OK, "CUSTOMER202", "사용자의 가게 검색이 성공했습니다.");

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
