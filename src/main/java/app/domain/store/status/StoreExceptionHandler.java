package app.domain.store.status;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import app.global.apiPayload.ApiResponse;
import app.global.apiPayload.code.ErrorReasonDTO;

@RestControllerAdvice
public class StoreExceptionHandler {

    @ExceptionHandler(value = StoreException.class)
    public ApiResponse<ErrorReasonDTO> handleStoreException(StoreException e) {
        return ApiResponse.onFailure(e.getCode().getCode(), e.getCode().getMessage(), e.getErrorReason());
    }
}