package app.domain.order.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import app.domain.cart.model.dto.RedisCartItem;
import app.domain.cart.service.CartService;
import app.domain.menu.model.entity.Menu;
import app.domain.menu.model.repository.MenuRepository;
import app.domain.order.model.dto.request.CreateOrderRequest;
import app.domain.order.model.dto.response.OrderDetailResponse;
import app.domain.order.model.dto.response.UpdateOrderStatusResponse;
import app.domain.order.model.entity.OrderItem;
import app.domain.order.model.entity.Orders;
import app.domain.order.model.entity.enums.OrderStatus;
import app.domain.order.model.repository.OrderItemRepository;
import app.domain.order.model.repository.OrdersRepository;
import app.domain.order.status.OrderErrorStatus;
import app.domain.store.model.entity.Store;
import app.domain.store.repository.StoreRepository;
import app.domain.user.model.entity.User;
import app.domain.user.model.entity.enums.UserRole;
import app.global.SecurityUtil;
import app.global.apiPayload.code.status.ErrorStatus;
import app.global.apiPayload.exception.GeneralException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

	private final OrdersRepository ordersRepository;
	private final OrderItemRepository orderItemRepository;
	private final CartService cartService;
	private final StoreRepository storeRepository;
	private final MenuRepository menuRepository;
	private final OrderDelayService orderDelayService;
	private final SecurityUtil securityUtil;
	private final ObjectMapper objectMapper;

	@Transactional
	public UUID createOrder(CreateOrderRequest request) {
		try {
			User user = securityUtil.getCurrentUser();
			Long userId = user.getUserId();

			List<RedisCartItem> cartItems = cartService.getCartFromCache();
			if (cartItems.isEmpty()) {
				throw new GeneralException(ErrorStatus.CART_NOT_FOUND);
			}
			UUID storeId = cartItems.get(0).getStoreId();
			boolean allSameStore = cartItems.stream().allMatch(item -> item.getStoreId().equals(storeId));
			if (!allSameStore) {
				throw new GeneralException(OrderErrorStatus.ORDER_DIFFERENT_STORE);
			}

			Store store = storeRepository.findById(storeId)
				.orElseThrow(() -> new GeneralException(ErrorStatus.STORE_NOT_FOUND));

			Map<UUID, Menu> menuMap = new HashMap<>();
			for (RedisCartItem cartItem : cartItems) {
				Menu menu = menuRepository.findById(cartItem.getMenuId())
					.orElseThrow(
						() -> new GeneralException(ErrorStatus.MENU_NOT_FOUND));
				menuMap.put(cartItem.getMenuId(), menu);
			}

			long calculatedTotalPrice = cartItems.stream()
				.mapToLong(cartItem -> menuMap.get(cartItem.getMenuId()).getPrice() * cartItem.getQuantity())
				.sum();

			if (request.getTotalPrice() != calculatedTotalPrice) {
				throw new GeneralException(OrderErrorStatus.ORDER_PRICE_MISMATCH);
			}

			Orders order = Orders.builder()
				.user(user)
				.store(store)
				.paymentMethod(request.getPaymentMethod())
				.orderChannel(request.getOrderChannel())
				.receiptMethod(request.getReceiptMethod())
				.requestMessage(request.getRequestMessage())
				.totalPrice(request.getTotalPrice())
				.orderStatus(OrderStatus.PENDING)
				.deliveryAddress(request.getDeliveryAddress())
				.orderHistory(
					"pending:" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
				.isRefundable(true)
				.build();

			Orders savedOrder = ordersRepository.save(order);

			for (RedisCartItem cartItem : cartItems) {
				Menu menu = menuMap.get(cartItem.getMenuId());

				OrderItem orderItem = OrderItem.builder()
					.orders(savedOrder)
					.menuName(menu.getName())
					.price(menu.getPrice())
					.quantity(cartItem.getQuantity())
					.build();
				orderItemRepository.save(orderItem);
			}

			orderDelayService.scheduleRefundDisable(savedOrder.getOrdersId());

			return savedOrder.getOrdersId();
		} catch (IllegalArgumentException e) {
			log.error("주문 생성 실패 - 유효하지 않은 요청: {}", request, e);
			throw new GeneralException(OrderErrorStatus.INVALID_ORDER_REQUEST);
		} catch (GeneralException e) {
			throw e;
		} catch (Exception e) {
			log.error("주문 생성 실패 - request: {}", request, e);
			throw new GeneralException(OrderErrorStatus.ORDER_CREATE_FAILED);
		}
	}

	public OrderDetailResponse getOrderDetail(UUID orderId) {
		try {
			Orders order = ordersRepository.findById(orderId)
				.orElseThrow(() -> new GeneralException(ErrorStatus.ORDER_NOT_FOUND));

			List<OrderItem> orderItems = orderItemRepository.findByOrders(order);

			return OrderDetailResponse.from(order, orderItems);
		} catch (GeneralException e) {
			throw e;
		} catch (Exception e) {
			log.error("주문 상세 조회 실패 - orderId: {}", orderId, e);
			throw new GeneralException(ErrorStatus._INTERNAL_SERVER_ERROR);
		}
	}

	private static final Map<OrderStatus, Set<OrderStatus>> VALID_TRANSITIONS = Map.of(
		OrderStatus.PENDING, EnumSet.of(OrderStatus.ACCEPTED, OrderStatus.REJECTED, OrderStatus.REFUNDED),
		OrderStatus.ACCEPTED, EnumSet.of(OrderStatus.COOKING),
		OrderStatus.COOKING, EnumSet.of(OrderStatus.IN_DELIVERY),
		OrderStatus.IN_DELIVERY, EnumSet.of(OrderStatus.COMPLETED)
	);

	@Transactional
	public UpdateOrderStatusResponse updateOrderStatus(UUID orderId, OrderStatus newStatus) {
		User currentUser = securityUtil.getCurrentUser();

		Orders order = ordersRepository.findById(orderId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.ORDER_NOT_FOUND));

		OrderStatus oldStatus = order.getOrderStatus();

		validateOrderStatusUpdate(currentUser, order, newStatus);

		String updatedHistory = appendToHistory(order.getOrderHistory(), newStatus);
		order.updateStatusAndHistory(newStatus, updatedHistory);

		log.info("주문 상태 변경 완료 - orderId: {}, user: {}, {} -> {}",
			orderId, currentUser.getUserId(), oldStatus, newStatus);

		return UpdateOrderStatusResponse.from(order);
	}

	private void validateOrderStatusUpdate(User user, Orders order, OrderStatus newStatus) {
		UserRole role = user.getUserRole();

		log.info(
			"Validating order status update. CurrentUser(ID: {}, Role: {}), Order(ID: {}), StoreOwner(ID: {}), CurrentStatus: {}, NewStatus: {}",
			user.getUserId(), role, order.getOrdersId(), order.getStore().getUser().getUserId(), order.getOrderStatus(),
			newStatus);

		switch (role) {
			case OWNER, MANAGER, MASTER -> validateOwnerUpdate(user, order, newStatus);
			default -> throw new GeneralException(OrderErrorStatus.ORDER_ACCESS_DENIED);
		}
	}

	private void validateOwnerUpdate(User owner, Orders order, OrderStatus newStatus) {
		if (!order.getStore().getUser().getUserId().equals(owner.getUserId())) {
			throw new GeneralException(OrderErrorStatus.ORDER_ACCESS_DENIED);
		}

		Set<OrderStatus> allowedTransitions = VALID_TRANSITIONS.getOrDefault(order.getOrderStatus(),
			EnumSet.noneOf(OrderStatus.class));

		if (!allowedTransitions.contains(newStatus)) {
			throw new GeneralException(OrderErrorStatus.INVALID_ORDER_STATUS_TRANSITION);
		}
	}

	private String appendToHistory(String currentHistoryJson, OrderStatus newStatus) {
		try {
			TypeReference<Map<String, String>> typeRef = new TypeReference<>() {
			};

			Map<String, String> historyMap =
				(currentHistoryJson == null || currentHistoryJson.isBlank() || currentHistoryJson.equals("{}"))
					? new LinkedHashMap<>()
					: objectMapper.readValue(currentHistoryJson, typeRef);

			String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
			historyMap.put(newStatus.name(), timestamp);

			return objectMapper.writeValueAsString(historyMap);

		} catch (JsonProcessingException e) {
			log.error("주문 이력(JSON) 업데이트에 실패했습니다. History: {}", currentHistoryJson, e);
			throw new GeneralException(ErrorStatus._INTERNAL_SERVER_ERROR);
		}
	}
}