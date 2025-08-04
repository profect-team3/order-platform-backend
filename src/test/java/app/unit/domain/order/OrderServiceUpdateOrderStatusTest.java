package app.unit.domain.order;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import app.domain.order.model.dto.response.UpdateOrderStatusResponse;
import app.domain.order.model.entity.Orders;
import app.domain.order.model.entity.enums.OrderStatus;
import app.domain.order.model.repository.OrdersRepository;
import app.domain.order.service.OrderService;
import app.domain.order.status.OrderErrorStatus;
import app.domain.store.model.entity.Store;
import app.domain.user.model.entity.User;
import app.domain.user.model.entity.enums.UserRole;
import app.global.SecurityUtil;
import app.global.apiPayload.code.status.ErrorStatus;
import app.global.apiPayload.exception.GeneralException;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderService.updateOrderStatus 테스트")
class OrderServiceUpdateOrderStatusTest {

	@Mock
	private SecurityUtil securityUtil;

	@Mock
	private ObjectMapper objectMapper;

	@Mock
	private OrdersRepository ordersRepository;

	@InjectMocks
	private OrderService orderService;

	private User storeOwner;
	private Store store;
	private Orders pendingOrder;
	private UUID orderId;

	@BeforeEach
	void setUp() {
		orderId = UUID.randomUUID();

		storeOwner = User.builder()
			.userId(1L)
			.userRole(UserRole.OWNER)
			.build();

		store = Store.builder()
			.storeId(UUID.randomUUID())
			.user(storeOwner)
			.build();

		pendingOrder = Orders.builder()
			.ordersId(orderId)
			.orderStatus(OrderStatus.PENDING)
			.store(store)
			.orderHistory("{}")
			.build();
		ReflectionTestUtils.setField(pendingOrder, "createdAt", LocalDateTime.now());

		when(securityUtil.getCurrentUser()).thenReturn(storeOwner);
	}

	@Nested
	@DisplayName("성공 케이스")
	class SuccessCase {

		@Test
		@DisplayName("가게 주인이 주문 상태를 PENDING에서 ACCEPTED로 성공적으로 변경한다.")
		void updateOrderStatus_ByOwner_Success() throws JsonProcessingException {
			// Given
			OrderStatus newStatus = OrderStatus.ACCEPTED;
			when(ordersRepository.findById(orderId)).thenReturn(Optional.of(pendingOrder));
			when(objectMapper.writeValueAsString(any(Map.class))).thenReturn("{\"ACCEPTED\":\"...\"}");

			// When
			UpdateOrderStatusResponse response = orderService.updateOrderStatus(orderId, newStatus);

			// Then
			verify(ordersRepository).findById(orderId);
			assertThat(pendingOrder.getOrderStatus()).isEqualTo(newStatus);
			assertThat(response.getUpdatedStatus()).isEqualTo(newStatus);
			assertThat(response.getOrderId()).isEqualTo(orderId);
		}

		@Test
		@DisplayName("고객이 주문 후 5분 이내에 PENDING 상태의 주문을 성공적으로 취소한다.")
		void updateOrderStatus_ByCustomer_Within5Minutes_Success() throws
			JsonProcessingException {
			// Given
			User customer = User.builder().userId(2L).userRole(UserRole.CUSTOMER).build();

			Orders customerOrder = Orders.builder()
				.ordersId(orderId)
				.orderStatus(OrderStatus.PENDING)
				.store(store)
				.user(customer)
				.orderHistory("{}")
				.build();
			ReflectionTestUtils.setField(customerOrder, "createdAt", LocalDateTime.now().minusMinutes(1));

			when(securityUtil.getCurrentUser()).thenReturn(customer);
			when(ordersRepository.findById(orderId)).thenReturn(Optional.of(customerOrder));
			when(objectMapper.writeValueAsString(any(Map.class))).thenReturn("{\"REFUNDED\":\"...\"}");

			// When
			UpdateOrderStatusResponse response = orderService.updateOrderStatus(orderId, OrderStatus.REFUNDED);

			// Then
			assertThat(customerOrder.getOrderStatus()).isEqualTo(OrderStatus.REFUNDED);
			assertThat(response.getUpdatedStatus()).isEqualTo(OrderStatus.REFUNDED);
		}
	}

	@Nested
	@DisplayName("실패 케이스")
	class FailureCase {

		@Test
		@DisplayName("다른 가게 주인이 주문 상태 변경을 시도하면 접근 거부 예외가 발생한다.")
		void updateOrderStatus_ByWrongOwner_ThrowsAccessDenied() {
			// Given
			OrderStatus newStatus = OrderStatus.ACCEPTED;
			User anotherOwner = User.builder().userId(99L).userRole(UserRole.OWNER).build();
			when(securityUtil.getCurrentUser()).thenReturn(anotherOwner);
			when(ordersRepository.findById(orderId)).thenReturn(Optional.of(pendingOrder));

			// When & Then
			assertThatThrownBy(() -> orderService.updateOrderStatus(orderId, newStatus))
				.isInstanceOf(GeneralException.class)
				.extracting("errorStatus")
				.isEqualTo(OrderErrorStatus.ORDER_ACCESS_DENIED);
		}

		@Test
		@DisplayName("유효하지 않은 상태(PENDING -> COOKING)로 변경을 시도하면 예외가 발생한다.")
		void updateOrderStatus_InvalidTransition_ThrowsException() {
			// Given
			OrderStatus invalidNewStatus = OrderStatus.COOKING;
			when(ordersRepository.findById(orderId)).thenReturn(Optional.of(pendingOrder));

			// When & Then
			assertThatThrownBy(() -> orderService.updateOrderStatus(orderId, invalidNewStatus))
				.isInstanceOf(GeneralException.class)
				.extracting("errorStatus")
				.isEqualTo(OrderErrorStatus.INVALID_ORDER_STATUS_TRANSITION);

			assertThat(pendingOrder.getOrderStatus()).isEqualTo(OrderStatus.PENDING);
		}

		@Test
		@DisplayName("존재하지 않는 주문 ID로 상태 변경을 시도하면 예외가 발생한다.")
		void updateOrderStatus_OrderNotFound_ThrowsException() {
			// Given
			UUID nonExistentOrderId = UUID.randomUUID();
			OrderStatus newStatus = OrderStatus.ACCEPTED;
			when(ordersRepository.findById(nonExistentOrderId)).thenReturn(Optional.empty());

			// When & Then
			assertThatThrownBy(() -> orderService.updateOrderStatus(nonExistentOrderId, newStatus))
				.isInstanceOf(GeneralException.class)
				.extracting("errorStatus")
				.isEqualTo(ErrorStatus.ORDER_NOT_FOUND);
		}
	}
}