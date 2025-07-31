package app.domain.customer;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import app.domain.customer.dto.response.CustomerOrderResponse;
import app.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "customer-order", description = "고객 주문 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/customer/order")
public class CustomerOrderController {

	private final CustomerOrderService customerOrderService;

	@Operation(summary = "고객 주문 내역 조회 API", description = "자신의 모든 주문 내역을 조회합니다.")
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공")
	})
	@Parameters({
		@Parameter(name = "authentication", hidden = true)
	})
	@GetMapping
	public ApiResponse<List<CustomerOrderResponse>> getCustomerOrders(Authentication authentication, Long userId) {
		// todo userid 가져와야함
		return ApiResponse.onSuccess(customerOrderService.getCustomerOrders(userId));
	}
}