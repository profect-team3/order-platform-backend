package app.domain.customer;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import app.domain.customer.dto.response.CustomerOrderResponse;
import app.domain.order.model.OrdersRepository;
import app.domain.order.model.entity.Orders;
import app.domain.user.model.UserRepository;
import app.domain.user.model.entity.User;
import app.global.apiPayload.code.status.ErrorStatus;
import app.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerOrderService {

	private final OrdersRepository ordersRepository;
	private final UserRepository userRepository;

	@Transactional(readOnly = true)
	public List<CustomerOrderResponse> getCustomerOrders(Long userid) {
		try {
			User user = userRepository.findByUserId(userid)
				.orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

			List<Orders> orders = ordersRepository.findByUser(user);

			return orders.stream()
				.map(CustomerOrderResponse::of)
				.collect(Collectors.toList());
		} catch (GeneralException e) {
			throw e;
		} catch (Exception e) {
			throw new GeneralException(ErrorStatus._INTERNAL_SERVER_ERROR);
		}
	}
}