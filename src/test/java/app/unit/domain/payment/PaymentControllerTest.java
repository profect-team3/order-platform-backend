package app.unit.domain.payment;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import app.domain.payment.PaymentService;
import app.domain.payment.controller.PaymentController;
import app.domain.payment.model.dto.request.CancelPaymentRequest;
import app.domain.payment.model.dto.request.PaymentConfirmRequest;
import app.domain.payment.model.dto.request.PaymentFailRequest;
import app.domain.payment.status.PaymentErrorStatus;
import app.global.apiPayload.code.status.ErrorStatus;
import app.global.apiPayload.exception.GeneralException;
import app.global.config.MockSecurityConfig;

@WebMvcTest(PaymentController.class)
@Import({MockSecurityConfig.class})
@DisplayName("PaymentController 테스트")
class PaymentControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private WebApplicationContext context;

	@MockitoBean
	private PaymentService paymentService;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders
			.webAppContextSetup(context)
			.apply(springSecurity())
			.build();
	}

	@Test
	@DisplayName("결제 승인 - 성공")
	@WithMockUser(username = "1", roles = {"CUSTOMER"})
	void confirmPayment_Success() throws Exception {
		PaymentConfirmRequest request = new PaymentConfirmRequest(
			"test_payment_key",
			UUID.randomUUID().toString(),
			"10000"
		);
		String resultMessage = "결제 승인이 완료되었습니다.";

		when(paymentService.confirmPayment(any(PaymentConfirmRequest.class)))
			.thenReturn(resultMessage);

		mockMvc.perform(post("/payment/confirm")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value("COMMON200"))
			.andExpect(jsonPath("$.message").value("success"))
			.andExpect(jsonPath("$.result").value(resultMessage));

		verify(paymentService).confirmPayment(any(PaymentConfirmRequest.class));
	}

	@Test
	@DisplayName("결제 승인 - 서비스 에러")
	@WithMockUser(username = "1", roles = {"CUSTOMER"})
	void confirmPayment_ServiceError() throws Exception {
		PaymentConfirmRequest request = new PaymentConfirmRequest(
			"test_payment_key",
			UUID.randomUUID().toString(),
			"10000"
		);

		when(paymentService.confirmPayment(any(PaymentConfirmRequest.class)))
			.thenThrow(new GeneralException(PaymentErrorStatus.PAYMENT_CONFIRM_FAILED));

		mockMvc.perform(post("/payment/confirm")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isInternalServerError())
			.andExpect(jsonPath("$.code").value("PAYMENT002"))
			.andExpect(jsonPath("$.message").value("결제 승인에 실패했습니다."));
	}

	@Test
	@DisplayName("결제 실패 처리 - 성공")
	@WithMockUser(username = "1", roles = {"CUSTOMER"})
	void processFail_Success() throws Exception {
		PaymentFailRequest request = new PaymentFailRequest(
			UUID.randomUUID().toString(),
			"INVALID_CARD",
			"유효하지 않은 카드입니다."
		);
		String resultMessage = "결제 실패 처리가 완료되었습니다.";

		when(paymentService.failSave(any(PaymentFailRequest.class)))
			.thenReturn(resultMessage);

		mockMvc.perform(post("/payment/failsave")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value("COMMON200"))
			.andExpect(jsonPath("$.message").value("success"))
			.andExpect(jsonPath("$.result").value(resultMessage));

		verify(paymentService).failSave(any(PaymentFailRequest.class));
	}

	@Test
	@DisplayName("결제 취소 - 성공")
	@WithMockUser(username = "1", roles = {"CUSTOMER"})
	void cancelPayment_Success() throws Exception {
		CancelPaymentRequest request = new CancelPaymentRequest(
			UUID.randomUUID(),
			"구매자가 취소를 원함"
		);
		String resultMessage = "결제 취소가 완료되었습니다.";

		when(paymentService.cancelPayment(any(CancelPaymentRequest.class)))
			.thenReturn(resultMessage);

		mockMvc.perform(post("/payment/cancel")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value("COMMON200"))
			.andExpect(jsonPath("$.message").value("success"))
			.andExpect(jsonPath("$.result").value(resultMessage));

		verify(paymentService).cancelPayment(any(CancelPaymentRequest.class));
	}

	@Test
	@DisplayName("결제 실패 처리 - 서비스 에러")
	@WithMockUser(username = "1", roles = {"CUSTOMER"})
	void processFail_ServiceError() throws Exception {
		PaymentFailRequest request = new PaymentFailRequest(
			UUID.randomUUID().toString(),
			"INVALID_CARD",
			"유효하지 않은 카드입니다."
		);

		when(paymentService.failSave(any(PaymentFailRequest.class)))
			.thenThrow(new GeneralException(ErrorStatus.ORDER_NOT_FOUND));

		mockMvc.perform(post("/payment/failsave")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value("ORDER006"))
			.andExpect(jsonPath("$.message").value("주문을 찾을 수 없습니다."));
	}

	@Test
	@DisplayName("결제 취소 - 서비스 에러")
	@WithMockUser(username = "1", roles = {"CUSTOMER"})
	void cancelPayment_ServiceError() throws Exception {
		CancelPaymentRequest request = new CancelPaymentRequest(
			UUID.randomUUID(),
			"구매자가 취소를 원함"
		);

		when(paymentService.cancelPayment(any(CancelPaymentRequest.class)))
			.thenThrow(new GeneralException(ErrorStatus.PAYMENT_NOT_FOUND));

		mockMvc.perform(post("/payment/cancel")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value("PAYMENT005"))
			.andExpect(jsonPath("$.message").value("결제내역을 찾을 수 없습니다."));
	}

	@Test
	@DisplayName("필수 파라미터 누락 - paymentKey 없음")
	@WithMockUser(username = "1", roles = {"CUSTOMER"})
	void confirmPayment_MissingPaymentKey() throws Exception {
		String jsonWithoutPaymentKey = "{\"orderId\": \"" + UUID.randomUUID() + "\", \"amount\": \"10000\"}";

		mockMvc.perform(post("/payment/confirm")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonWithoutPaymentKey))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value("COMMON400"))
			.andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
			.andExpect(jsonPath("$.result.paymentKey").value("결제 키는 필수입니다."));

		verify(paymentService, never()).confirmPayment(any());
	}

	@Test
	@DisplayName("필수 파라미터 누락 - orderId 없음")
	@WithMockUser(username = "1", roles = {"CUSTOMER"})
	void confirmPayment_MissingOrderId() throws Exception {
		String jsonWithoutOrderId = "{\"paymentKey\": \"test_key\", \"amount\": \"10000\"}";

		mockMvc.perform(post("/payment/confirm")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonWithoutOrderId))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value("COMMON400"))
			.andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
			.andExpect(jsonPath("$.result.orderId").value("주문 ID는 필수입니다."));

		verify(paymentService, never()).confirmPayment(any());
	}

	@Test
	@DisplayName("필수 파라미터 누락 - amount 없음")
	@WithMockUser(username = "1", roles = {"CUSTOMER"})
	void confirmPayment_MissingAmount() throws Exception {
		String jsonWithoutAmount = "{\"paymentKey\": \"test_key\", \"orderId\": \"" + UUID.randomUUID() + "\"}";

		mockMvc.perform(post("/payment/confirm")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonWithoutAmount))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value("COMMON400"))
			.andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
			.andExpect(jsonPath("$.result.amount").value("결제 금액은 필수입니다."));

		verify(paymentService, never()).confirmPayment(any());
	}

	@Test
	@DisplayName("필수 파라미터 누락 - errorCode 없음")
	@WithMockUser(username = "1", roles = {"CUSTOMER"})
	void processFail_MissingErrorCode() throws Exception {
		String jsonWithoutErrorCode = "{\"orderId\": \"" + UUID.randomUUID() + "\", \"message\": \"에러 메시지\"}";

		mockMvc.perform(post("/payment/failsave")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonWithoutErrorCode))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value("COMMON400"))
			.andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
			.andExpect(jsonPath("$.result.errorCode").value("에러 코드는 필수입니다."));

		verify(paymentService, never()).failSave(any());
	}

	@Test
	@DisplayName("필수 파라미터 누락 - message 없음")
	@WithMockUser(username = "1", roles = {"CUSTOMER"})
	void processFail_MissingMessage() throws Exception {
		String jsonWithoutMessage = "{\"orderId\": \"" + UUID.randomUUID() + "\", \"errorCode\": \"INVALID_CARD\"}";

		mockMvc.perform(post("/payment/failsave")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonWithoutMessage))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value("COMMON400"))
			.andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
			.andExpect(jsonPath("$.result.message").value("실패 사유는 필수입니다."));

		verify(paymentService, never()).failSave(any());
	}

	@Test
	@DisplayName("필수 파라미터 누락 - orderId 없음 (cancel)")
	@WithMockUser(username = "1", roles = {"CUSTOMER"})
	void cancelPayment_MissingOrderId() throws Exception {
		String jsonWithoutOrderId = "{\"cancelReason\": \"구매자 취소\"}";

		mockMvc.perform(post("/payment/cancel")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonWithoutOrderId))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value("COMMON400"))
			.andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
			.andExpect(jsonPath("$.result.orderId").value("주문 ID는 필수입니다."));

		verify(paymentService, never()).cancelPayment(any());
	}
}