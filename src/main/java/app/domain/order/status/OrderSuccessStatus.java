package app.domain.order.status;

import org.springframework.http.HttpStatus;

import app.global.apiPayload.code.BaseCode;
import app.global.apiPayload.code.ReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderSuccessStatus implements BaseCode {

	ORDER_STATUS_UPDATED(HttpStatus.OK, "ORDER201", "주문 상태 전이에 성공하였습니다."),
	ORDER_DETAIL_FETCHED(HttpStatus.OK, "ORDER202", "주문 상세 조회에 성공하였습니다."),
	ORDER_CREATED(HttpStatus.OK, "ORDER203", "주문 생성에 성공하였습니다.");

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