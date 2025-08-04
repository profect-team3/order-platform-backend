package app.domain.cart.service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import app.domain.cart.model.dto.AddCartItemRequest;
import app.domain.cart.model.dto.RedisCartItem;
import app.domain.cart.model.entity.Cart;
import app.domain.cart.model.entity.CartItem;
import app.domain.cart.model.repository.CartItemRepository;
import app.domain.cart.model.repository.CartRepository;
import app.domain.menu.model.entity.Menu;
import app.domain.menu.model.repository.MenuRepository;
import app.domain.store.repository.StoreRepository;
import app.domain.user.model.entity.User;
import app.global.SecurityUtil;
import app.global.apiPayload.code.status.ErrorStatus;
import app.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {

	private final CartRedisService cartRedisService;
	private final CartRepository cartRepository;
	private final CartItemRepository cartItemRepository;
	private final MenuRepository menuRepository;
	private final StoreRepository storeRepository;
	private final SecurityUtil securityUtil;

	@PreAuthorize("hasAuthority('CUSTOMER')")
	public String addCartItem(AddCartItemRequest request) {
		User user = securityUtil.getCurrentUser();
		if (!menuRepository.existsById(request.getMenuId())) {
			throw new GeneralException(ErrorStatus.MENU_NOT_FOUND);
		}

		if (!storeRepository.existsById(request.getStoreId())) {
			throw new GeneralException(ErrorStatus.STORE_NOT_FOUND);
		}

		List<RedisCartItem> items = getCartFromCache();

		if (!items.isEmpty() && !items.get(0).getStoreId().equals(request.getStoreId())) {
			items.clear();
		}

		boolean isExist = items.stream().anyMatch(i -> i.getMenuId().equals(request.getMenuId()));
		if (isExist) {
			items.stream()
				.filter(item -> item.getMenuId().equals(request.getMenuId()))
				.findFirst()
				.ifPresent(item -> item.setQuantity(item.getQuantity() + request.getQuantity()));
		} else {
			items.add(RedisCartItem.builder()
				.menuId(request.getMenuId())
				.storeId(request.getStoreId())
				.quantity(request.getQuantity())
				.build());
		}

		return cartRedisService.saveCartToRedis(user.getUserId(), items);
	}

	@PreAuthorize("hasAuthority('CUSTOMER')")
	public String updateCartItem(UUID menuId, int quantity) {
		User user = securityUtil.getCurrentUser();
		List<RedisCartItem> items = getCartFromCache();

		items.stream()
			.filter(item -> item.getMenuId().equals(menuId))
			.findFirst()
			.ifPresent(item -> item.setQuantity(quantity));
		return cartRedisService.saveCartToRedis(user.getUserId(), items);
	}

	@PreAuthorize("hasAuthority('CUSTOMER')")
	public String removeCartItem(UUID menuId) {
		User user = securityUtil.getCurrentUser();
		return cartRedisService.removeCartItem(user.getUserId(), menuId);
	}

	@PreAuthorize("hasAuthority('CUSTOMER')")
	public List<RedisCartItem> getCartFromCache() {
		User user = securityUtil.getCurrentUser();
		if (!cartRedisService.existsCartInRedis(user.getUserId())) {
			loadDbToRedis();
		}
		return cartRedisService.getCartFromRedis(user.getUserId());
	}

	@PreAuthorize("hasAuthority('CUSTOMER')")
	public String clearCartItems() {
		User user = securityUtil.getCurrentUser();
		return cartRedisService.clearCartItems(user.getUserId());
	}

	@PreAuthorize("hasAuthority('CUSTOMER')")
	@Transactional(readOnly = true)
	public String loadDbToRedis() {
		User user = securityUtil.getCurrentUser();
		Cart cart = cartRepository.findByUser_UserId(user.getUserId())
			.orElseThrow(() -> new GeneralException(ErrorStatus.CART_NOT_FOUND));

		List<CartItem> cartItems = cartItemRepository.findByCart_CartId(cart.getCartId());
		List<RedisCartItem> redisItems = cartItems.stream()
			.map(item -> RedisCartItem.builder()
				.menuId(item.getMenu().getMenuId())
				.storeId(item.getMenu().getStore().getStoreId())
				.quantity(item.getQuantity())
				.build())
			.toList();
		cartRedisService.saveCartToRedis(user.getUserId(), redisItems);
		return "사용자 " + user.getUserId() + "의 장바구니가 DB에서 Redis로 성공적으로 로드되었습니다.";
	}

	@Transactional
	public String syncRedisToDb(Long userId) {
		List<RedisCartItem> redisItems = cartRedisService.getCartFromRedis(userId);

		Cart cart = cartRepository.findByUser_UserId(userId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.CART_NOT_FOUND));

		cartItemRepository.deleteByCart_CartId(cart.getCartId());
		if (!redisItems.isEmpty()) {
			List<CartItem> cartItems = redisItems.stream()
				.filter(item -> menuRepository.existsById(item.getMenuId()))
				.map(item -> {
					Menu menu = menuRepository.findById(item.getMenuId()).get();
					return CartItem.builder()
						.cart(cart)
						.menu(menu)
						.quantity(item.getQuantity())
						.build();
				})
				.toList();

			cartItemRepository.saveAll(cartItems);
		}
		return "사용자 " + userId + "의 장바구니가 Redis에서 DB로 성공적으로 동기화되었습니다.";
	}

	@Scheduled(initialDelay = 900000, fixedRate = 900000)
	@Transactional
	public String syncAllCartsToDb() {
		Set<String> cartKeys = cartRedisService.getAllCartKeys();
		int successCount = 0;
		for (String key : cartKeys) {
			Long userId = cartRedisService.extractUserIdFromKey(key);
			syncRedisToDb(userId);
			successCount++;
		}
		return "전체 장바구니 동기화 완료 - 성공: " + successCount + "/" + cartKeys.size();
	}
}
