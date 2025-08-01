package app.domain.customer;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import app.domain.customer.dto.response.CustomerOrderResponse;
import app.domain.order.model.entity.enums.OrderChannel;
import app.domain.order.model.entity.enums.OrderStatus;
import app.domain.order.model.entity.enums.PaymentMethod;
import app.domain.order.model.entity.enums.ReceiptMethod;
import app.global.apiPayload.code.status.ErrorStatus;
import app.global.apiPayload.exception.GeneralException;
import app.global.config.SecurityConfig;

@Import(SecurityConfig.class)
@WebMvcTest(CustomerOrderController.class)
@DisplayName("고객 주문 내역 조회 테스트")
class CustomerOrderControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private CustomerOrderService customerOrderService;

	@Test
	@DisplayName("고객 주문 내역 조회 성공")
	@WithMockUser(username = "test@example.com")
	void getCustomerOrders_Success() throws Exception {
		// given
		Long userId = 1L;
		CustomerOrderResponse response = new CustomerOrderResponse(
			UUID.randomUUID(),
			"테스트 가게",
			10000L,
			"테스트 주소",
			PaymentMethod.CREDIT_CARD,
			OrderChannel.ONLINE,
			ReceiptMethod.DELIVERY,
			OrderStatus.PENDING,
			true,
			"{}",
			"요청사항 없음",
			LocalDateTime.now()
		);
		List<CustomerOrderResponse> responses = Collections.singletonList(response);
		given(customerOrderService.getCustomerOrders(userId)).willReturn(responses);

		// when & then
		mockMvc.perform(get("/customer/order"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.result[0].storeName").value("테스트 가게"));
	}

	@Test
	@DisplayName("고객 주문 내역 조회 실패 - 사용자를 찾을 수 없음")
	@WithMockUser(username = "nonexistent@example.com")
	void getCustomerOrders_UserNotFound() throws Exception {
		// given
		Long userId = 1L;
		given(customerOrderService.getCustomerOrders(1L)).willThrow(
			new GeneralException(ErrorStatus.USER_NOT_FOUND));

		// when & then
		mockMvc.perform(get("/customer/order"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message").value(ErrorStatus.USER_NOT_FOUND.getMessage()));
	}
}