package app.domain.payment.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import app.domain.payment.PaymentService;
import app.domain.payment.model.dto.request.CancelPaymentRequest;
import app.domain.payment.model.dto.request.PaymentConfirmRequest;
import app.domain.payment.model.dto.request.PaymentFailRequest;
import app.domain.payment.status.PaymentSuccessStatus;
import app.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "payment", description = "결제 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {

	private final PaymentService paymentService;

	@Operation(summary = "결제 승인 API", description = "토스페이먼츠를 통해 결제를 승인합니다.")
	@PostMapping("/confirm")
	public ApiResponse<String> confirm(@Valid @RequestBody PaymentConfirmRequest request) {
		String result = paymentService.confirmPayment(request);
		return ApiResponse.onSuccess(PaymentSuccessStatus.PAYMENT_CONFIRMED, result);
	}

	@Operation(summary = "결제 실패 처리 API", description = "결제 실패 정보를 DB에 저장합니다.")
	@PostMapping("/failsave")
	public ApiResponse<String> processFail(@Valid @RequestBody PaymentFailRequest request) {
		String result = paymentService.failSave(request);
		return ApiResponse.onSuccess(PaymentSuccessStatus.PAYMENT_FAIL_SAVED, result);
	}

	@Operation(summary = "결제 취소 처리 API", description = "토스페이먼츠를 통해 결제를 취소합니다.")
	@PostMapping("/cancel")
	public ApiResponse<String> cancelPayment(@Valid @RequestBody CancelPaymentRequest request) {
		String result = paymentService.cancelPayment(request);
		return ApiResponse.onSuccess(PaymentSuccessStatus.PAYMENT_CANCELLED, result);
	}
}