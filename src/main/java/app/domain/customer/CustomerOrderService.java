package app.domain.customer;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import app.domain.customer.dto.response.CustomerOrderResponse;
import app.domain.customer.status.CustomerErrorStatus;
import app.domain.order.model.repository.OrdersRepository;
import app.domain.order.model.entity.Orders;
import app.domain.user.model.UserRepository;
import app.domain.user.model.entity.User;
import app.global.SecurityUtil;
import app.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerOrderService {

	private final OrdersRepository ordersRepository;
	private final UserRepository userRepository;
	private final SecurityUtil securityUtil;

	@Transactional(readOnly = true)
	public List<CustomerOrderResponse> getCustomerOrders() {
		User user = securityUtil.getCurrentUser();
		List<Orders> orders = ordersRepository.findByUser(user);
		if (orders.isEmpty()) {
			throw new GeneralException(CustomerErrorStatus.CUSTOMER_ORDER_NOT_FOUND);
		}
		return orders.stream()
			.map(CustomerOrderResponse::of)
			.collect(Collectors.toList());

	}
}