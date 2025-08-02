package app.domain.ai.status;

import org.springframework.http.HttpStatus;

import app.global.apiPayload.code.BaseCode;
import app.global.apiPayload.code.ReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AiErrorStatus implements BaseCode {

	AI_GENERATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "AI001", "AI 콘텐츠 생성에 실패했습니다."),
	AI_INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "AI002", "잘못된 입력값입니다");

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