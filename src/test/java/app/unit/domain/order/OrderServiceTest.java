// package app.unit.domain.order;
//
// import static org.assertj.core.api.Assertions.*;
// import static org.mockito.ArgumentMatchers.*;
// import static org.mockito.Mockito.*;
//
// import java.time.LocalDateTime;
// import java.util.List;
// import java.util.Optional;
// import java.util.UUID;
//
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
//
// import app.domain.cart.model.dto.RedisCartItem;
// import app.domain.cart.service.CartService;
// import app.domain.menu.model.entity.Menu;
// import app.domain.menu.model.repository.MenuRepository;
// import app.domain.order.model.dto.request.CreateOrderRequest;
// import app.domain.order.model.dto.response.OrderDetailResponse;
// import app.domain.order.model.entity.OrderItem;
// import app.domain.order.model.entity.Orders;
// import app.domain.order.model.entity.enums.OrderChannel;
// import app.domain.order.model.entity.enums.PaymentMethod;
// import app.domain.order.model.entity.enums.ReceiptMethod;
// import app.domain.order.model.repository.OrderItemRepository;
// import app.domain.order.model.repository.OrdersRepository;
// import app.domain.order.service.OrderDelayService;
// import app.domain.order.service.OrderService;
// import app.domain.store.model.entity.Store;
// import app.domain.store.repository.StoreRepository;
// import app.domain.user.model.UserRepository;
// import app.domain.user.model.entity.User;
// import app.global.apiPayload.exception.GeneralException;
//
// @ExtendWith(MockitoExtension.class)
// class OrderServiceTest {
//
// 	@Mock
// 	private OrdersRepository ordersRepository;
//
// 	@Mock
// 	private OrderItemRepository orderItemRepository;
//
// 	@Mock
// 	private CartService cartService;
//
// 	@Mock
// 	private UserRepository userRepository;
//
// 	@Mock
// 	private StoreRepository storeRepository;
//
// 	@Mock
// 	private MenuRepository menuRepository;
//
// 	@Mock
// 	private OrderDelayService orderDelayService;
//
// 	@InjectMocks
// 	private OrderService orderService;
//
// 	private Long userId;
// 	private UUID storeId;
// 	private UUID menuId;
// 	private CreateOrderRequest request;
// 	private LocalDateTime requestTime;
//
// 	@BeforeEach
// 	void setUp() {
// 		userId = 1L;
// 		storeId = UUID.randomUUID();
// 		menuId = UUID.randomUUID();
//
// 		request = new CreateOrderRequest(
// 			PaymentMethod.CREDIT_CARD,
// 			OrderChannel.ONLINE,
// 			ReceiptMethod.DELIVERY,
// 			"문 앞에 놓아주세요",
// 			10000L,
// 			"서울시 강남구"
// 		);
// 	}
//
// 	@Test
// 	@DisplayName("주문 생성 성공")
// 	void createOrder_Success() {
// 		// Given
// 		RedisCartItem cartItem = RedisCartItem.builder()
// 			.menuId(menuId)
// 			.storeId(storeId)
// 			.quantity(2)
// 			.build();
// 		List<RedisCartItem> cartItems = List.of(cartItem);
//
// 		User user = User.builder().userId(userId).build();
// 		Store store = Store.builder().storeId(storeId).build();
// 		Menu menu = Menu.builder().menuId(menuId).name("테스트메뉴").price(5000L).build();
// 		Orders savedOrder = Orders.builder().ordersId(UUID.randomUUID()).build();
//
// 		when(cartService.getCartFromCache()).thenReturn(cartItems);
// 		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
// 		when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
// 		when(menuRepository.findById(menuId)).thenReturn(Optional.of(menu));
// 		when(ordersRepository.save(any(Orders.class))).thenReturn(savedOrder);
//
// 		// When
// 		UUID result = orderService.createOrder(request);
//
// 		// Then
// 		assertThat(result).isInstanceOf(UUID.class);
// 		verify(cartService).getCartFromCache();
// 		verify(userRepository).findById(userId);
// 		verify(storeRepository).findById(storeId);
// 		verify(menuRepository).findById(menuId);
// 		verify(ordersRepository).save(any(Orders.class));
// 		verify(orderItemRepository).save(any(OrderItem.class));
// 		verify(orderDelayService).scheduleRefundDisable(any(UUID.class));
// 	}
//
// 	@Test
// 	@DisplayName("유저를 찾을 수 없음")
// 	void createOrder_UserNotFound() {
// 		// Given
// 		RedisCartItem cartItem = RedisCartItem.builder()
// 			.menuId(menuId)
// 			.storeId(storeId)
// 			.quantity(2)
// 			.build();
// 		List<RedisCartItem> cartItems = List.of(cartItem);
//
// 		when(cartService.getCartFromCache()).thenReturn(cartItems);
// 		when(userRepository.findById(userId)).thenReturn(Optional.empty());
//
// 		// When & Then
// 		assertThatThrownBy(() -> orderService.createOrder(request))
// 			.isInstanceOf(GeneralException.class)
// 			.satisfies(ex -> {
// 				GeneralException generalEx = (GeneralException)ex;
// 				assertThat(generalEx.getErrorStatus().getMessage()).isEqualTo("존재하지 않는 사용자입니다.");
// 			});
//
// 		verify(cartService).getCartFromCache(userId);
// 		verify(userRepository).findById(userId);
// 		verify(storeRepository, never()).findById(any());
// 		verify(ordersRepository, never()).save(any());
// 	}
//
// 	@Test
// 	@DisplayName("매장을 찾을 수 없음")
// 	void createOrder_StoreNotFound() {
// 		// Given
// 		RedisCartItem cartItem = RedisCartItem.builder()
// 			.menuId(menuId)
// 			.storeId(storeId)
// 			.quantity(2)
// 			.build();
// 		List<RedisCartItem> cartItems = List.of(cartItem);
//
// 		User user = User.builder().userId(userId).build();
//
// 		when(cartService.getCartFromCache(userId)).thenReturn(cartItems);
// 		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
// 		when(storeRepository.findById(storeId)).thenReturn(Optional.empty());
//
// 		// When & Then
// 		assertThatThrownBy(() -> orderService.createOrder(userId, request))
// 			.isInstanceOf(GeneralException.class)
// 			.satisfies(ex -> {
// 				GeneralException generalEx = (GeneralException)ex;
// 				assertThat(generalEx.getErrorStatus().getMessage()).isEqualTo("해당 가맹점을 찾을 수 없습니다.");
// 			});
//
// 		verify(cartService).getCartFromCache(userId);
// 		verify(userRepository).findById(userId);
// 		verify(storeRepository).findById(storeId);
// 		verify(ordersRepository, never()).save(any());
// 	}
//
// 	@Test
// 	@DisplayName("장바구니에 2개 매장의 메뉴가 들어있음")
// 	void createOrder_DifferentStores() {
// 		// Given
// 		UUID anotherStoreId = UUID.randomUUID();
// 		RedisCartItem cartItem1 = RedisCartItem.builder()
// 			.menuId(menuId)
// 			.storeId(storeId)
// 			.quantity(2)
// 			.build();
// 		RedisCartItem cartItem2 = RedisCartItem.builder()
// 			.menuId(UUID.randomUUID())
// 			.storeId(anotherStoreId)
// 			.quantity(1)
// 			.build();
// 		List<RedisCartItem> cartItems = List.of(cartItem1, cartItem2);
//
// 		User user = User.builder().userId(userId).build();
//
// 		when(cartService.getCartFromCache(userId)).thenReturn(cartItems);
// 		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
//
// 		// When & Then
// 		assertThatThrownBy(() -> orderService.createOrder(userId, request))
// 			.isInstanceOf(GeneralException.class)
// 			.satisfies(ex -> {
// 				GeneralException generalEx = (GeneralException)ex;
// 				assertThat(generalEx.getErrorStatus().getMessage()).isEqualTo("서로 다른 매장의 메뉴는 함께 주문할 수 없습니다.");
// 			});
//
// 		verify(cartService).getCartFromCache(userId);
// 		verify(userRepository).findById(userId);
// 		verify(storeRepository, never()).findById(any());
// 		verify(ordersRepository, never()).save(any());
// 	}
//
// 	@Test
// 	@DisplayName("장바구니가 비어있음")
// 	void createOrder_EmptyCart() {
// 		// Given
// 		List<RedisCartItem> cartItems = List.of();
//
// 		when(cartService.getCartFromCache(userId)).thenReturn(cartItems);
//
// 		// When & Then
// 		assertThatThrownBy(() -> orderService.createOrder(userId, request))
// 			.isInstanceOf(GeneralException.class)
// 			.satisfies(ex -> {
// 				GeneralException generalEx = (GeneralException)ex;
// 				assertThat(generalEx.getErrorStatus().getMessage()).isEqualTo("장바구니를 찾을 수 없습니다.");
// 			});
//
// 		verify(cartService).getCartFromCache(userId);
// 		verify(userRepository, never()).findById(any());
// 		verify(ordersRepository, never()).save(any());
// 	}
//
// 	@Test
// 	@DisplayName("요청 총액과 장바구니 아이템 총액 불일치")
// 	void createOrder_PriceMismatch() {
// 		// Given
// 		RedisCartItem cartItem = RedisCartItem.builder()
// 			.menuId(menuId)
// 			.storeId(storeId)
// 			.quantity(2)
// 			.build();
// 		List<RedisCartItem> cartItems = List.of(cartItem);
//
// 		User user = User.builder().userId(userId).build();
// 		Store store = Store.builder().storeId(storeId).build();
// 		Menu menu = Menu.builder().menuId(menuId).name("테스트메뉴").price(5000).build();
//
// 		CreateOrderRequest mismatchRequest = new CreateOrderRequest(
// 			PaymentMethod.CREDIT_CARD,
// 			OrderChannel.ONLINE,
// 			ReceiptMethod.DELIVERY,
// 			"문 앞에 놓아주세요",
// 			15000L,
// 			"서울시 강남구"
// 		);
//
// 		when(cartService.getCartFromCache(userId)).thenReturn(cartItems);
// 		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
// 		when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
// 		when(menuRepository.findById(menuId)).thenReturn(Optional.of(menu));
//
// 		// When & Then
// 		assertThatThrownBy(() -> orderService.createOrder(userId, mismatchRequest))
// 			.isInstanceOf(GeneralException.class)
// 			.satisfies(ex -> {
// 				GeneralException generalEx = (GeneralException)ex;
// 				assertThat(generalEx.getErrorStatus().getMessage()).isEqualTo("요청 총액과 장바구니 아이템 총액이 일치하지 않습니다.");
// 			});
//
// 		verify(cartService).getCartFromCache(userId);
// 		verify(userRepository).findById(userId);
// 		verify(storeRepository).findById(storeId);
// 		verify(menuRepository).findById(menuId);
// 		verify(ordersRepository, never()).save(any());
// 	}
//
// 	@Test
// 	@DisplayName("메뉴를 찾을 수 없음")
// 	void createOrder_MenuNotFound() {
// 		// Given
// 		RedisCartItem cartItem = RedisCartItem.builder()
// 			.menuId(menuId)
// 			.storeId(storeId)
// 			.quantity(2)
// 			.build();
// 		List<RedisCartItem> cartItems = List.of(cartItem);
//
// 		User user = User.builder().userId(userId).build();
// 		Store store = Store.builder().storeId(storeId).build();
//
// 		when(cartService.getCartFromCache(userId)).thenReturn(cartItems);
// 		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
// 		when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
// 		when(menuRepository.findById(menuId)).thenReturn(Optional.empty());
//
// 		// When & Then
// 		assertThatThrownBy(() -> orderService.createOrder(userId, request))
// 			.isInstanceOf(GeneralException.class)
// 			.satisfies(ex -> {
// 				GeneralException generalEx = (GeneralException)ex;
// 				assertThat(generalEx.getErrorStatus().getMessage()).isEqualTo("메뉴를 찾을 수 없습니다.");
// 			});
//
// 		verify(cartService).getCartFromCache(userId);
// 		verify(userRepository).findById(userId);
// 		verify(storeRepository).findById(storeId);
// 		verify(ordersRepository, never()).save(any());
// 		verify(orderItemRepository, never()).save(any());
// 	}
//
// 	@Test
// 	@DisplayName("주문 상세 조회 성공")
// 	void getOrderDetail_Success() {
// 		// Given
// 		UUID orderId = UUID.randomUUID();
// 		User user = User.builder().userId(userId).build();
// 		Store store = Store.builder().storeId(storeId).storeName("테스트매장").build();
// 		Orders order = Orders.builder()
// 			.ordersId(orderId)
// 			.user(user)
// 			.store(store)
// 			.totalPrice(10000L)
// 			.deliveryAddress("서울시 강남구")
// 			.paymentMethod(PaymentMethod.CREDIT_CARD)
// 			.orderChannel(OrderChannel.ONLINE)
// 			.receiptMethod(ReceiptMethod.DELIVERY)
// 			.orderStatus(app.domain.order.model.entity.enums.OrderStatus.PENDING)
// 			.requestMessage("문 앞에 놓아주세요")
// 			.build();
//
// 		OrderItem orderItem = OrderItem.builder()
// 			.orders(order)
// 			.menuName("테스트메뉴")
// 			.price(5000)
// 			.quantity(2)
// 			.build();
// 		List<OrderItem> orderItems = List.of(orderItem);
//
// 		when(ordersRepository.findById(orderId)).thenReturn(Optional.of(order));
// 		when(orderItemRepository.findByOrders(order)).thenReturn(orderItems);
//
// 		// When
// 		OrderDetailResponse result = orderService.getOrderDetail(orderId);
//
// 		// Then
// 		assertThat(result.getStoreName()).isEqualTo("테스트매장");
// 		assertThat(result.getMenuList()).hasSize(1);
// 		assertThat(result.getMenuList().get(0).getMenuName()).isEqualTo("테스트메뉴");
// 		assertThat(result.getMenuList().get(0).getQuantity()).isEqualTo(2);
// 		assertThat(result.getMenuList().get(0).getPrice()).isEqualTo(5000);
// 		assertThat(result.getTotalPrice()).isEqualTo(10000L);
// 		assertThat(result.getDeliveryAddress()).isEqualTo("서울시 강남구");
// 		assertThat(result.getPaymentMethod()).isEqualTo(PaymentMethod.CREDIT_CARD);
// 		assertThat(result.getOrderChannel()).isEqualTo(OrderChannel.ONLINE);
// 		assertThat(result.getReceiptMethod()).isEqualTo(ReceiptMethod.DELIVERY);
// 		assertThat(result.getOrderStatus()).isEqualTo(app.domain.order.model.entity.enums.OrderStatus.PENDING);
// 		assertThat(result.getRequestMessage()).isEqualTo("문 앞에 놓아주세요");
//
// 		verify(ordersRepository).findById(orderId);
// 		verify(orderItemRepository).findByOrders(order);
// 	}
//
// 	@Test
// 	@DisplayName("주문을 찾을 수 없음")
// 	void getOrderDetail_OrderNotFound() {
// 		// Given
// 		UUID orderId = UUID.randomUUID();
//
// 		when(ordersRepository.findById(orderId)).thenReturn(Optional.empty());
//
// 		// When & Then
// 		assertThatThrownBy(() -> orderService.getOrderDetail(orderId))
// 			.isInstanceOf(GeneralException.class)
// 			.satisfies(ex -> {
// 				GeneralException generalEx = (GeneralException)ex;
// 				assertThat(generalEx.getErrorStatus().getMessage()).isEqualTo("주문을 찾을 수 없습니다.");
// 			});
//
// 		verify(ordersRepository).findById(orderId);
// 		verify(orderItemRepository, never()).findByOrders(any());
// 	}
// }