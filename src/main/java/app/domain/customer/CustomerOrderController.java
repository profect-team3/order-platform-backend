package app.domain.customer;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import app.domain.customer.dto.response.CustomerOrderResponse;
import app.domain.customer.status.CustomerSuccessStatus;
import app.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "customer-order", description = "고객 주문 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/customer/order")
public class CustomerOrderController {

	private final CustomerOrderService customerOrderService;

	@Operation(summary = "고객 주문 내역 조회 API", description = "자신의 모든 주문 내역을 조회합니다.")
	@GetMapping
	public ApiResponse<List<CustomerOrderResponse>> getCustomerOrders(
	) {
		return ApiResponse.onSuccess(CustomerSuccessStatus.CUSTOMER_OK, customerOrderService.getCustomerOrders());
	}
}