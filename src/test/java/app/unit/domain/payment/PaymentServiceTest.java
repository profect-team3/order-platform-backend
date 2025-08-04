package app.unit.domain.payment;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.net.HttpURLConnection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import app.domain.cart.service.CartService;
import app.domain.order.model.entity.Orders;
import app.domain.order.model.entity.enums.OrderStatus;
import app.domain.order.model.entity.enums.PaymentMethod;
import app.domain.order.model.repository.OrdersRepository;
import app.domain.payment.PaymentService;
import app.domain.payment.model.dto.request.CancelPaymentRequest;
import app.domain.payment.model.dto.request.PaymentConfirmRequest;
import app.domain.payment.model.dto.request.PaymentFailRequest;
import app.domain.payment.model.entity.Payment;
import app.domain.payment.model.entity.PaymentEtc;
import app.domain.payment.model.entity.enums.PaymentStatus;
import app.domain.payment.model.repository.PaymentEtcRepository;
import app.domain.payment.model.repository.PaymentRepository;
import app.domain.payment.status.PaymentErrorStatus;
import app.domain.store.model.entity.Store;
import app.domain.user.model.entity.User;
import app.global.apiPayload.code.status.ErrorStatus;
import app.global.apiPayload.exception.GeneralException;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

	@Mock
	private OrdersRepository ordersRepository;

	@Mock
	private PaymentRepository paymentRepository;

	@Mock
	private PaymentEtcRepository paymentEtcRepository;

	@Mock
	private CartService cartService;

	@Mock
	private HttpURLConnection mockConnection;

	@Spy
	@InjectMocks
	private PaymentService paymentService;

	private UUID orderId;
	private Long userId;
	private PaymentConfirmRequest confirmRequest;
	private PaymentFailRequest failRequest;
	private CancelPaymentRequest cancelRequest;
	private Orders order;
	private Payment payment;

	@BeforeEach
	void setUp() {
		orderId = UUID.randomUUID();
		userId = 1L;

		ReflectionTestUtils.setField(paymentService, "tossSecretKey", "test_secret_key");
		ReflectionTestUtils.setField(paymentService, "tossUrl", "https://api.tosspayments.com/v1/payments");

		confirmRequest = new PaymentConfirmRequest(
			"test_payment_key",
			orderId.toString(),
			"10000"
		);

		failRequest = new PaymentFailRequest(String.valueOf(orderId), "INVALID_CARD", "유효하지 않은 카드입니다.");

		cancelRequest = new CancelPaymentRequest(orderId, "구매자가 취소를 원함");

		User user = User.builder().userId(userId).build();
		Store store = Store.builder().storeId(UUID.randomUUID()).build();

		order = Orders.builder()
			.ordersId(orderId)
			.user(user)
			.store(store)
			.totalPrice(10000L)
			.paymentMethod(PaymentMethod.CREDIT_CARD)
			.orderStatus(OrderStatus.PENDING)
			.orderHistory("pending:" + "2024-01-01 10:00:00")
			.build();

		payment = Payment.builder()
			.paymentId(UUID.randomUUID())
			.ordersId(orderId)
			.paymentKey("test_payment_key")
			.paymentMethod(PaymentMethod.CREDIT_CARD)
			.paymentStatus(PaymentStatus.COMPLETED)
			.amount(10000L)
			.build();
	}

	@Test
	@DisplayName("결제 승인 - 비즈니스 로직 검증")
	void confirmPayment_BusinessLogic_Validation() {
		// Given
		when(ordersRepository.findById(orderId)).thenReturn(Optional.of(order));
		when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
		when(paymentEtcRepository.save(any(PaymentEtc.class))).thenReturn(mock(PaymentEtc.class));

		try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(
			SecurityContextHolder.class)) {
			SecurityContext mockSecurityContext = mock(SecurityContext.class);
			Authentication mockAuth = mock(Authentication.class);
			org.springframework.security.core.userdetails.User mockUser =
				new org.springframework.security.core.userdetails.User("1", "password", List.of());

			mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(mockSecurityContext);
			when(mockSecurityContext.getAuthentication()).thenReturn(mockAuth);
			when(mockAuth.getPrincipal()).thenReturn(mockUser);

			assertThatThrownBy(() -> paymentService.confirmPayment(confirmRequest))
				.isInstanceOf(GeneralException.class);

			verify(ordersRepository).findById(orderId);
		}
	}

	@Test
	@DisplayName("결제 승인 실패 - 주문을 찾을 수 없음")
	void confirmPayment_OrderNotFound() {
		// Given
		when(ordersRepository.findById(orderId)).thenReturn(Optional.empty());

		// When & Then
		assertThatThrownBy(() -> paymentService.confirmPayment(confirmRequest))
			.isInstanceOf(GeneralException.class)
			.satisfies(ex -> {
				GeneralException generalEx = (GeneralException)ex;
				assertThat(generalEx.getErrorReason().getCode()).isEqualTo(ErrorStatus.ORDER_NOT_FOUND.getCode());
			});

		verify(ordersRepository).findById(orderId);
		verify(paymentRepository, never()).save(any());
	}

	@Test
	@DisplayName("결제 승인 실패 - 금액 불일치")
	void confirmPayment_AmountMismatch() {
		// Given
		PaymentConfirmRequest wrongAmountRequest = new PaymentConfirmRequest(
			"test_payment_key",
			orderId.toString(),
			"20000"
		);
		when(ordersRepository.findById(orderId)).thenReturn(Optional.of(order));

		// When & Then
		assertThatThrownBy(() -> paymentService.confirmPayment(wrongAmountRequest))
			.isInstanceOf(GeneralException.class)
			.satisfies(ex -> {
				GeneralException generalEx = (GeneralException)ex;
				assertThat(generalEx.getErrorReason().getCode()).isEqualTo(
					PaymentErrorStatus.PAYMENT_AMOUNT_MISMATCH.getCode());
			});

		verify(ordersRepository).findById(orderId);
		verify(paymentRepository, never()).save(any());
	}

	@Test
	@DisplayName("결제 실패 처리 성공")
	void failSave_Success() {
		// Given
		when(ordersRepository.findById(orderId)).thenReturn(Optional.of(order));

		// When
		String result = paymentService.failSave(failRequest);

		// Then
		assertThat(result).isEqualTo("결제 실패 처리가 완료되었습니다.");
		verify(ordersRepository).findById(orderId);
		// updateOrderStatus는 엔티티 내부 메서드이므로 별도 검증 불필요
	}

	@Test
	@DisplayName("결제 실패 처리 실패 - 주문을 찾을 수 없음")
	void failSave_OrderNotFound() {
		// Given
		when(ordersRepository.findById(orderId)).thenReturn(Optional.empty());

		// When & Then
		assertThatThrownBy(() -> paymentService.failSave(failRequest))
			.isInstanceOf(GeneralException.class)
			.satisfies(ex -> {
				GeneralException generalEx = (GeneralException)ex;
				assertThat(generalEx.getErrorReason().getCode()).isEqualTo(ErrorStatus.ORDER_NOT_FOUND.getCode());
			});

		verify(ordersRepository).findById(orderId);
		verify(paymentRepository, never()).save(any());
	}

	@Test
	@DisplayName("결제 취소 - 비즈니스 로직 검증")
	void cancelPayment_BusinessLogic_Validation() {
		// Given - isRefundable = true로 설정
		Orders refundableOrder = Orders.builder()
			.ordersId(orderId)
			.user(User.builder().userId(userId).build())
			.store(Store.builder().storeId(UUID.randomUUID()).build())
			.totalPrice(10000L)
			.paymentMethod(PaymentMethod.CREDIT_CARD)
			.orderStatus(OrderStatus.PENDING)
			.orderHistory("pending:" + "2024-01-01 10:00:00")
			.isRefundable(true)
			.build();

		when(ordersRepository.findById(orderId)).thenReturn(Optional.of(refundableOrder));
		when(paymentRepository.findByOrdersId(orderId)).thenReturn(Optional.of(payment));
		when(paymentEtcRepository.save(any(PaymentEtc.class))).thenReturn(mock(PaymentEtc.class));

		try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(
			SecurityContextHolder.class)) {
			SecurityContext mockSecurityContext = mock(SecurityContext.class);
			Authentication mockAuth = mock(Authentication.class);
			org.springframework.security.core.userdetails.User mockUser =
				new org.springframework.security.core.userdetails.User("1", "password", List.of());

			mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(mockSecurityContext);
			when(mockSecurityContext.getAuthentication()).thenReturn(mockAuth);
			when(mockAuth.getPrincipal()).thenReturn(mockUser);

			assertThatThrownBy(() -> paymentService.cancelPayment(cancelRequest))
				.isInstanceOf(GeneralException.class);

			verify(ordersRepository).findById(orderId);
			verify(paymentRepository).findByOrdersId(orderId);
		}
	}

	@Test
	@DisplayName("결제 취소 실패 - 주문을 찾을 수 없음")
	void cancelPayment_OrderNotFound() {
		// Given
		when(ordersRepository.findById(orderId)).thenReturn(Optional.empty());

		// When & Then
		assertThatThrownBy(() -> paymentService.cancelPayment(cancelRequest))
			.isInstanceOf(GeneralException.class)
			.satisfies(ex -> {
				GeneralException generalEx = (GeneralException)ex;
				assertThat(generalEx.getErrorReason().getCode()).isEqualTo(ErrorStatus.ORDER_NOT_FOUND.getCode());
			});

		verify(ordersRepository).findById(orderId);
		verify(paymentRepository, never()).save(any());
	}

	@Test
	@DisplayName("결제 취소 실패 - 결제 정보를 찾을 수 없음")
	void cancelPayment_PaymentNotFound() {
		// Given - isRefundable = true로 설정
		Orders refundableOrder = Orders.builder()
			.ordersId(orderId)
			.user(User.builder().userId(userId).build())
			.store(Store.builder().storeId(UUID.randomUUID()).build())
			.totalPrice(10000L)
			.paymentMethod(PaymentMethod.CREDIT_CARD)
			.orderStatus(OrderStatus.PENDING)
			.orderHistory("pending:" + "2024-01-01 10:00:00")
			.isRefundable(true)
			.build();

		when(ordersRepository.findById(orderId)).thenReturn(Optional.of(refundableOrder));
		when(paymentRepository.findByOrdersId(orderId)).thenReturn(Optional.empty());

		// When & Then
		assertThatThrownBy(() -> paymentService.cancelPayment(cancelRequest))
			.isInstanceOf(GeneralException.class)
			.satisfies(ex -> {
				GeneralException generalEx = (GeneralException)ex;
				assertThat(generalEx.getErrorReason().getCode()).isEqualTo(ErrorStatus.PAYMENT_NOT_FOUND.getCode());
			});

		verify(ordersRepository).findById(orderId);
		verify(paymentRepository).findByOrdersId(orderId);
		verify(paymentRepository, never()).save(any());
	}

	@Test
	@DisplayName("결제 취소 실패 - 환불 불가능")
	void cancelPayment_NotRefundable() {
		// Given
		Orders nonRefundableOrder = Orders.builder()
			.ordersId(orderId)
			.user(User.builder().userId(userId).build())
			.store(Store.builder().storeId(UUID.randomUUID()).build())
			.totalPrice(10000L)
			.paymentMethod(PaymentMethod.CREDIT_CARD)
			.orderStatus(OrderStatus.PENDING)
			.orderHistory("pending:" + "2024-01-01 10:00:00")
			.isRefundable(false)
			.build();

		when(ordersRepository.findById(orderId)).thenReturn(Optional.of(nonRefundableOrder));

		// When & Then
		assertThatThrownBy(() -> paymentService.cancelPayment(cancelRequest))
			.isInstanceOf(GeneralException.class)
			.satisfies(ex -> {
				GeneralException generalEx = (GeneralException)ex;
				assertThat(generalEx.getErrorReason().getCode()).isEqualTo(
					PaymentErrorStatus.PAYMENT_NOT_REFUNDABLE.getCode());
			});

		verify(ordersRepository).findById(orderId);
		verify(paymentRepository, never()).findByOrdersId(any());
	}
}