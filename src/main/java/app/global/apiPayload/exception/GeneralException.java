package app.global.apiPayload.exception;

import app.global.apiPayload.code.BaseCode;
import app.global.apiPayload.code.ReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GeneralException extends RuntimeException {

	private final BaseCode code;

	public ReasonDTO getErrorReason() {
		return this.code.getReason();
	}

	public ReasonDTO getErrorReasonHttpStatus() {
		return this.code.getReasonHttpStatus();
	}
}