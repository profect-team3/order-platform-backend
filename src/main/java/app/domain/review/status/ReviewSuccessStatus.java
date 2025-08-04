package app.domain.review.status;

import org.springframework.http.HttpStatus;

import app.global.apiPayload.code.BaseCode;
import app.global.apiPayload.code.ReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReviewSuccessStatus implements BaseCode {

	REVIEW_CREATED(HttpStatus.CREATED, "REVIEW201", "리뷰 작성이 성공했습니다."),
	GET_REVIEWS_SUCCESS(HttpStatus.OK, "REVIEW200", "리뷰 성공적으로 불러왔습니다.");

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
