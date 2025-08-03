package app.domain.customer;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import app.domain.customer.dto.response.CustomerOrderResponse;
import app.domain.customer.status.CustomerErrorStatus;
import app.domain.order.model.OrdersRepository;
import app.domain.order.model.entity.Orders;
import app.domain.order.model.entity.enums.OrderChannel;
import app.domain.order.model.entity.enums.OrderStatus;
import app.domain.order.model.entity.enums.PaymentMethod;
import app.domain.order.model.entity.enums.ReceiptMethod;
import app.domain.store.model.entity.Store;
import app.domain.user.model.UserRepository;
import app.domain.user.model.entity.User;
import app.global.apiPayload.code.status.ErrorStatus;
import app.global.apiPayload.exception.GeneralException;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomerOrderService 테스트")
class CustomerOrderServiceTest {

	@Mock
	private OrdersRepository ordersRepository;

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private CustomerOrderService customerOrderService;

	private User testUser;
	private Store testStore;
	private Orders testOrder;

	@BeforeEach
	void setUp() {
		testUser = User.builder()
			.userId(1L)
			.email("test@example.com")
			.build();

		testStore = Store.builder()
			.storeId(UUID.randomUUID())
			.storeName("Test Store")
			.build();

		testOrder = Orders.builder()
			.ordersId(UUID.randomUUID())
			.user(testUser)
			.store(testStore)
			.totalPrice(15000L)
			.deliveryAddress("Test Address")
			.paymentMethod(PaymentMethod.CREDIT_CARD)
			.orderStatus(OrderStatus.COMPLETED)
			.orderChannel(OrderChannel.ONLINE)
			.receiptMethod(ReceiptMethod.DELIVERY)
			.isRefundable(false)
			.orderHistory("{}")
			.build();
	}

	@Test
	@DisplayName("고객 주문 내역 조회 성공")
	void getCustomerOrders_Success() {
		when(userRepository.findById(testUser.getUserId())).thenReturn(Optional.of(testUser));
		when(ordersRepository.findByUser(testUser)).thenReturn(Arrays.asList(testOrder));

		List<CustomerOrderResponse> result = customerOrderService.getCustomerOrders(testUser.getUserId());

		assertThat(result).hasSize(1);
		assertThat(result.get(0).getStoreName()).isEqualTo("Test Store");
		assertThat(result.get(0).getTotalPrice()).isEqualTo(15000L);

		verify(userRepository, times(1)).findById(testUser.getUserId());
		verify(ordersRepository, times(1)).findByUser(testUser);
	}

	@Test
	@DisplayName("고객 주문 내역 조회 실패 - 사용자를 찾을 수 없음")
	void getCustomerOrders_UserNotFound() {
		Long nonExistentUserId = 999L;
		when(userRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> customerOrderService.getCustomerOrders(nonExistentUserId))
			.isInstanceOf(GeneralException.class)
			.extracting("code")
			.isEqualTo(ErrorStatus.USER_NOT_FOUND);

		verify(userRepository, times(1)).findById(nonExistentUserId);
		verify(ordersRepository, never()).findByUser(any());
	}

	@Test
	@DisplayName("고객 주문 내역 조회 실패 - 주문이 없는 경우")
	void getCustomerOrders_NoOrdersFound() {
		when(userRepository.findById(testUser.getUserId())).thenReturn(Optional.of(testUser));
		when(ordersRepository.findByUser(testUser)).thenReturn(Collections.emptyList());

		assertThatThrownBy(() -> customerOrderService.getCustomerOrders(testUser.getUserId()))
			.isInstanceOf(GeneralException.class)
			.extracting("code")
			.isEqualTo(CustomerErrorStatus.CUSTOMER_ORDER_NOT_FOUND); // 수정된 부분

		verify(userRepository, times(1)).findById(testUser.getUserId());
		verify(ordersRepository, times(1)).findByUser(testUser);
	}
}