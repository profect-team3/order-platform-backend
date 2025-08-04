package app.global.apiPayload.code;

import org.springframework.http.HttpStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReasonDTO {

	private final HttpStatus httpStatus;
	private final boolean isSuccess; // Indicates whether the operation was successful
	private final String code;
	private final String message;
}