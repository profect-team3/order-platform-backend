package app.unit.domain.order.model;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import app.domain.order.model.entity.OrderItem;
import app.domain.order.model.entity.Orders;
import app.domain.order.model.repository.OrderItemRepository;

@ExtendWith(MockitoExtension.class)
class OrderItemRepositoryTest {

	@Mock
	private OrderItemRepository orderItemRepository;

	@Mock
	private Orders mockOrders;

	private OrderItem testOrderItem1;
	private OrderItem testOrderItem2;
	private UUID orderItemId1;
	private UUID orderItemId2;

	@BeforeEach
	void setUp() {
		orderItemId1 = UUID.randomUUID();
		orderItemId2 = UUID.randomUUID();

		testOrderItem1 = OrderItem.builder()
			.orderItemId(orderItemId1)
			.orders(mockOrders)
			.menuName("치킨버거")
			.price(8000L)
			.quantity(2)
			.build();

		testOrderItem2 = OrderItem.builder()
			.orderItemId(orderItemId2)
			.orders(mockOrders)
			.menuName("감자튀김")
			.price(3000L)
			.quantity(1)
			.build();
	}

	@Test
	@DisplayName("주문 아이템 저장 성공")
	void save_Success() {
		// Given
		when(orderItemRepository.save(any(OrderItem.class))).thenReturn(testOrderItem1);

		// When
		OrderItem savedOrderItem = orderItemRepository.save(testOrderItem1);

		// Then
		assertThat(savedOrderItem).isNotNull();
		assertThat(savedOrderItem.getOrderItemId()).isEqualTo(orderItemId1);
		assertThat(savedOrderItem.getMenuName()).isEqualTo("치킨버거");
		verify(orderItemRepository).save(testOrderItem1);
	}

	@Test
	@DisplayName("주문 아이템 ID로 조회 성공")
	void findById_Success() {
		// Given
		when(orderItemRepository.findById(orderItemId1)).thenReturn(Optional.of(testOrderItem1));

		// When
		Optional<OrderItem> foundOrderItem = orderItemRepository.findById(orderItemId1);

		// Then
		assertThat(foundOrderItem).isPresent();
		assertThat(foundOrderItem.get().getOrderItemId()).isEqualTo(orderItemId1);
		assertThat(foundOrderItem.get().getMenuName()).isEqualTo("치킨버거");
		verify(orderItemRepository).findById(orderItemId1);
	}

	@Test
	@DisplayName("존재하지 않는 주문 아이템 ID로 조회")
	void findById_NotFound() {
		// Given
		UUID nonExistentId = UUID.randomUUID();
		when(orderItemRepository.findById(nonExistentId)).thenReturn(Optional.empty());

		// When
		Optional<OrderItem> foundOrderItem = orderItemRepository.findById(nonExistentId);

		// Then
		assertThat(foundOrderItem).isEmpty();
		verify(orderItemRepository).findById(nonExistentId);
	}

	@Test
	@DisplayName("주문으로 주문 아이템 목록 조회 성공")
	void findByOrders_Success() {
		// Given
		List<OrderItem> orderItems = List.of(testOrderItem1, testOrderItem2);
		when(orderItemRepository.findByOrders(mockOrders)).thenReturn(orderItems);

		// When
		List<OrderItem> foundOrderItems = orderItemRepository.findByOrders(mockOrders);

		// Then
		assertThat(foundOrderItems).hasSize(2);
		assertThat(foundOrderItems).containsExactly(testOrderItem1, testOrderItem2);
		verify(orderItemRepository).findByOrders(mockOrders);
	}

	@Test
	@DisplayName("주문으로 주문 아이템 목록 조회 - 빈 결과")
	void findByOrders_EmptyResult() {
		// Given
		when(orderItemRepository.findByOrders(mockOrders)).thenReturn(List.of());

		// When
		List<OrderItem> foundOrderItems = orderItemRepository.findByOrders(mockOrders);

		// Then
		assertThat(foundOrderItems).isEmpty();
		verify(orderItemRepository).findByOrders(mockOrders);
	}

	@Test
	@DisplayName("주문 아이템 삭제 성공")
	void delete_Success() {
		// Given
		doNothing().when(orderItemRepository).delete(testOrderItem1);

		// When
		orderItemRepository.delete(testOrderItem1);

		// Then
		verify(orderItemRepository).delete(testOrderItem1);
	}

	@Test
	@DisplayName("지연 로딩 테스트 - Orders 접근 시 쿼리 실행")
	void lazyLoading_Orders_Test() {
		// Given
		when(orderItemRepository.findById(orderItemId1)).thenReturn(Optional.of(testOrderItem1));
		when(mockOrders.getOrdersId()).thenReturn(UUID.randomUUID());

		// When
		Optional<OrderItem> foundOrderItem = orderItemRepository.findById(orderItemId1);

		verify(orderItemRepository).findById(orderItemId1);
		verify(mockOrders, never()).getOrdersId();

		UUID ordersId = foundOrderItem.get().getOrders().getOrdersId();

		// Then
		assertThat(ordersId).isNotNull();
		verify(mockOrders).getOrdersId();
	}
}