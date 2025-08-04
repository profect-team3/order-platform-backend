package app.unit.domain.order.model;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import app.domain.order.model.repository.OrdersRepository;
import app.domain.user.model.entity.User;
import app.domain.order.model.entity.Orders;
import app.domain.order.model.entity.enums.OrderChannel;
import app.domain.order.model.entity.enums.OrderStatus;
import app.domain.order.model.entity.enums.PaymentMethod;
import app.domain.order.model.entity.enums.ReceiptMethod;
import app.domain.store.model.entity.Store;

@ExtendWith(MockitoExtension.class)
class OrdersRepositoryTest {

	@Mock
	private OrdersRepository ordersRepository;

	@Mock
	private Store mockStore;

	@Mock
	private User mockUser;

	private Orders testOrder;
	private UUID orderId;

	@BeforeEach
	void setUp() {
		orderId = UUID.randomUUID();
		testOrder = Orders.builder()
			.ordersId(orderId)
			.store(mockStore)
			.user(mockUser)
			.totalPrice(10000L)
			.deliveryAddress("서울시 강남구")
			.paymentMethod(PaymentMethod.CREDIT_CARD)
			.orderChannel(OrderChannel.ONLINE)
			.receiptMethod(ReceiptMethod.DELIVERY)
			.orderStatus(OrderStatus.PENDING)
			.isRefundable(true)
			.orderHistory("{\"pending\": \"2024-01-01 12:00:00\"}")
			.requestMessage("문 앞에 놓아주세요")
			.build();
	}

	@Test
	@DisplayName("주문 저장 성공")
	void save_Success() {
		// Given
		when(ordersRepository.save(any(Orders.class))).thenReturn(testOrder);

		// When
		Orders savedOrder = ordersRepository.save(testOrder);

		// Then
		assertThat(savedOrder).isNotNull();
		assertThat(savedOrder.getOrdersId()).isEqualTo(orderId);
		verify(ordersRepository).save(testOrder);
	}

	@Test
	@DisplayName("주문 ID로 조회 성공")
	void findById_Success() {
		// Given
		when(ordersRepository.findById(orderId)).thenReturn(Optional.of(testOrder));

		// When
		Optional<Orders> foundOrder = ordersRepository.findById(orderId);

		// Then
		assertThat(foundOrder).isPresent();
		assertThat(foundOrder.get().getOrdersId()).isEqualTo(orderId);
		verify(ordersRepository).findById(orderId);
	}

	@Test
	@DisplayName("존재하지 않는 주문 ID로 조회")
	void findById_NotFound() {
		// Given
		UUID nonExistentId = UUID.randomUUID();
		when(ordersRepository.findById(nonExistentId)).thenReturn(Optional.empty());

		// When
		Optional<Orders> foundOrder = ordersRepository.findById(nonExistentId);

		// Then
		assertThat(foundOrder).isEmpty();
		verify(ordersRepository).findById(nonExistentId);
	}

	@Test
	@DisplayName("주문 삭제 성공")
	void delete_Success() {
		// Given
		doNothing().when(ordersRepository).delete(testOrder);

		// When
		ordersRepository.delete(testOrder);

		// Then
		verify(ordersRepository).delete(testOrder);
	}

	@Test
	@DisplayName("지연 로딩 테스트 - Store 접근 시 쿼리 실행")
	void lazyLoading_Store_Test() {
		// Given
		when(ordersRepository.findById(orderId)).thenReturn(Optional.of(testOrder));
		when(mockStore.getStoreName()).thenReturn("테스트매장");

		// When
		Optional<Orders> foundOrder = ordersRepository.findById(orderId);

		verify(ordersRepository).findById(orderId);
		verify(mockStore, never()).getStoreName();

		String storeName = foundOrder.get().getStore().getStoreName();

		// Then
		assertThat(storeName).isEqualTo("테스트매장");
		verify(mockStore).getStoreName();
	}

	@Test
	@DisplayName("지연 로딩 테스트 - User 접근 시 쿼리 실행")
	void lazyLoading_User_Test() {
		// Given
		when(ordersRepository.findById(orderId)).thenReturn(Optional.of(testOrder));
		when(mockUser.getUserId()).thenReturn(1L);

		// When
		Optional<Orders> foundOrder = ordersRepository.findById(orderId);

		verify(ordersRepository).findById(orderId);
		verify(mockUser, never()).getUserId();

		Long userId = foundOrder.get().getUser().getUserId();

		// Then
		assertThat(userId).isEqualTo(1L);
		verify(mockUser).getUserId();
	}
}