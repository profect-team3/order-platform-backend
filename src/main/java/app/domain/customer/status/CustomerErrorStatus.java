package app.domain.customer.status;

import org.springframework.http.HttpStatus;

import app.global.apiPayload.code.BaseCode;
import app.global.apiPayload.code.ReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CustomerErrorStatus implements BaseCode {

	CUSTOMER_ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "CUSTOMER404", "유저에 대한 오더가 존재하지 않습니다."),

	ADDRESS_ALREADY_EXISTS(HttpStatus.CONFLICT, "ADDRESS_001", "이미 존재하는 주소입니다."),
	ADDRESS_ADD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "ADDRESS_002", "주소 등록에 실패했습니다."),
	ADDRESS_READ_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "ADDRESS_003", "주소 목록 조회에 실패했습니다."),
	ADDRESS_ALIAS_INVALID(HttpStatus.BAD_REQUEST, "ADDRESS_004", "주소 별명은 null이 들어갈 수 없습니다."),
	ADDRESS_ADDRESS_INVALID(HttpStatus.BAD_REQUEST, "ADDRESS_005", "주소 별명은 null이 들어갈 수 없습니다."),
	ADDRESS_ADDRESSDETAIL_INVALID(HttpStatus.BAD_REQUEST, "ADDRESS_006", "주소 별명은 null이 들어갈 수 없습니다.");

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