package app.domain.store.status;

import org.springframework.http.HttpStatus;

import app.global.apiPayload.code.BaseCode;
import app.global.apiPayload.code.ReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum StoreSuccessStatus implements BaseCode {
	// 일반적인 응답
	_OK(HttpStatus.OK, "COMMON200", "성공입니다."),

	// 가게 관련 응답
	STORE_CREATED_SUCCESS(HttpStatus.CREATED, "STORE201", "가게가 성공적으로 생성되었습니다."),
	STORE_UPDATED_SUCCESS(HttpStatus.OK, "STORE202", "가게 정보가 성공적으로 업데이트되었습니다."),
	STORE_DELETED_SUCCESS(HttpStatus.OK, "STORE203", "가게가 성공적으로 삭제되었습니다.");


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
