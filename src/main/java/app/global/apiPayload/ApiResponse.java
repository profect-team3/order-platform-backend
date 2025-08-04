package app.global.apiPayload;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import app.global.apiPayload.code.BaseCode;

@JsonPropertyOrder({"isSuccess", "code", "message", "result"})
public record ApiResponse<T>(

	@JsonProperty("isSuccess")
	Boolean isSuccess, String code, String message,

	@JsonInclude(JsonInclude.Include.NON_NULL) T result
) {

	public static <T> ApiResponse<T> onSuccess(BaseCode code, T result) {
		return new ApiResponse<>(true, code.getReasonHttpStatus().getCode(), code.getReasonHttpStatus().getMessage(),
			result);
	}

	public static <T> ApiResponse<T> onFailure(BaseCode code, T errorData) {
		return new ApiResponse<>(false, code.getReasonHttpStatus().getCode(), code.getReasonHttpStatus().getMessage(),
			errorData);
	}
}