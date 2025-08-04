package app.domain.review.status;

import org.springframework.http.HttpStatus;

import app.global.apiPayload.code.BaseCode;
import app.global.apiPayload.code.ReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReviewErrorStatus implements BaseCode {

	ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "REVIEW001", "해당 주문이 존재하지 않습니다."),
	NO_REVIEWS_FOUND_FOR_USER(HttpStatus.NOT_FOUND, "REVIEW002", "해당 사용자가 작성한 리뷰가 없습니다."),
	REVIEW_ALREADY_EXISTS(HttpStatus.CONFLICT, "REVIEW003", "이미 해당 주문에 대한 리뷰가 존재합니다."),
	REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "REVIEW004", "리뷰가 존재하지 않습니다.");

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
