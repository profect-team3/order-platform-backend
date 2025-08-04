package app.domain.user.status;

import org.springframework.http.HttpStatus;

import app.global.apiPayload.code.BaseCode;
import app.global.apiPayload.code.ReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserErrorStatus implements BaseCode {

	// 인증 관련
	INVALID_PASSWORD(HttpStatus.FORBIDDEN, "AUTH_001", "비밀번호가 일치하지 않습니다."),

	// User 관련
	USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "USER001", "이미 존재하는 유저입니다."),
	EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "USER002", "이미 사용 중인 이메일입니다."),
	NICKNAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "USER003", "이미 사용 중인 닉네임입니다."),
	PHONE_NUMBER_ALREADY_EXISTS(HttpStatus.CONFLICT, "USER004", "이미 사용 중인 전화번호입니다.");

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
