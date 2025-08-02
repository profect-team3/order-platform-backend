package app.global.apiPayload.code;

import org.springframework.http.HttpStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReasonDTO {

	private final HttpStatus httpStatus;
	private final boolean isSuccess; // 💡 성공/실패 여부를 나타내는 필드 추가
	private final String code;
	private final String message;
}