package app.domain.user.status;

import org.springframework.http.HttpStatus;

import app.global.apiPayload.code.BaseCode;
import app.global.apiPayload.code.ReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserSuccessStatus implements BaseCode {

	// User
	USER_CREATED(HttpStatus.CREATED, "USER201", "사용자 생성이 성공했습니다."),
	LOGIN_SUCCESS(HttpStatus.OK, "USER202", "로그인에 성공했습니다."),
	LOGOUT_SUCCESS(HttpStatus.OK, "USER203", "로그아웃에 성공했습니다."),
	WITHDRAW_SUCCESS(HttpStatus.OK, "USER204", "회원 탈퇴가 성공적으로 처리되었습니다.");

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
