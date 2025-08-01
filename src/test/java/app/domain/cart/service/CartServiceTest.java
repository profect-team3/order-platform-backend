package app.domain.cart.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import app.domain.cart.model.CartItemRepository;
import app.domain.cart.model.CartRepository;
import app.domain.cart.model.dto.AddCartItemRequest;
import app.domain.cart.model.dto.RedisCartItem;
import app.domain.cart.model.entity.Cart;
import app.domain.cart.model.entity.CartItem;
import app.domain.user.model.entity.User;
import app.domain.menu.model.entity.Menu;
import app.domain.store.model.entity.Store;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

	@Mock
	private CartRedisService cartRedisService;

	@Mock
	private CartRepository cartRepository;

	@Mock
	private CartItemRepository cartItemRepository;

	@InjectMocks
	private CartService cartService;

	private Long userId;
	private UUID menuId;
	private UUID storeId;
	private List<RedisCartItem> cartItems;

	@BeforeEach
	void setUp() {
		userId = 1L;
		menuId = UUID.randomUUID();
		storeId = UUID.randomUUID();
		cartItems = new ArrayList<>();
	}

	@Test
	@DisplayName("장바구니에 새로운 아이템을 추가할 수 있다")
	void addItem() {
		AddCartItemRequest request = new AddCartItemRequest(menuId, storeId, 2);
		when(cartRedisService.existsCartInRedis(userId)).thenReturn(true);
		when(cartRedisService.getCartFromRedis(userId)).thenReturn(cartItems);
		when(cartRedisService.saveCartToRedis(eq(userId), any())).thenReturn("성공");

		cartService.addCartItem(userId, request);

		verify(cartRedisService).saveCartToRedis(eq(userId), argThat(items ->
			items.size() == 1 &&
				items.get(0).getMenuId().equals(menuId) &&
				items.get(0).getQuantity() == 2
		));
	}

	@Test
	@DisplayName("이미 존재하는 아이템을 추가하면 수량이 누적된다")
	void addExistingItem() {
		AddCartItemRequest request = new AddCartItemRequest(menuId, storeId, 2);
		cartItems.add(RedisCartItem.builder().menuId(menuId).storeId(storeId).quantity(1).build());
		when(cartRedisService.existsCartInRedis(userId)).thenReturn(true);
		when(cartRedisService.getCartFromRedis(userId)).thenReturn(cartItems);
		when(cartRedisService.saveCartToRedis(eq(userId), any())).thenReturn("성공");

		cartService.addCartItem(userId, request);

		verify(cartRedisService).saveCartToRedis(eq(userId), argThat(items ->
			items.get(0).getQuantity() == 3
		));
	}

	@Test
	@DisplayName("다른 매장의 아이템을 추가하면 기존 장바구니가 초기화된다")
	void addDifferentStoreItem() {
		AddCartItemRequest request = new AddCartItemRequest(menuId, storeId, 2);
		UUID otherStoreId = UUID.randomUUID();
		cartItems.add(RedisCartItem.builder().menuId(UUID.randomUUID()).storeId(otherStoreId).quantity(1).build());
		when(cartRedisService.existsCartInRedis(userId)).thenReturn(true);
		when(cartRedisService.getCartFromRedis(userId)).thenReturn(cartItems);
		when(cartRedisService.saveCartToRedis(eq(userId), any())).thenReturn("성공");

		cartService.addCartItem(userId, request);

		verify(cartRedisService).saveCartToRedis(eq(userId), argThat(items ->
			items.size() == 1 &&
				items.get(0).getStoreId().equals(storeId)
		));
	}

	@Test
	@DisplayName("장바구니 아이템의 수량을 수정할 수 있다")
	void updateItem() {
		cartItems.add(RedisCartItem.builder().menuId(menuId).storeId(storeId).quantity(1).build());
		when(cartRedisService.existsCartInRedis(userId)).thenReturn(true);
		when(cartRedisService.getCartFromRedis(userId)).thenReturn(cartItems);
		when(cartRedisService.saveCartToRedis(eq(userId), any())).thenReturn("성공");

		cartService.updateCartItem(userId, menuId, 5);

		verify(cartRedisService).saveCartToRedis(eq(userId), argThat(items ->
			items.get(0).getQuantity() == 5
		));
	}

	@Test
	@DisplayName("장바구니에서 특정 아이템을 삭제할 수 있다")
	void removeItem() {
		when(cartRedisService.removeCartItem(userId, menuId)).thenReturn("성공");

		cartService.removeCartItem(userId, menuId);

		verify(cartRedisService).removeCartItem(userId, menuId);
	}

	@Test
	@DisplayName("Redis에 장바구니가 있으면 Redis에서 조회한다")
	void getFromRedis() {
		when(cartRedisService.existsCartInRedis(userId)).thenReturn(true);
		when(cartRedisService.getCartFromRedis(userId)).thenReturn(cartItems);

		List<RedisCartItem> result = cartService.getCartFromCache(userId);

		assertThat(result).isEqualTo(cartItems);
		verify(cartRedisService, never()).saveCartToRedis(any(), any());
	}

	@Test
	@DisplayName("Redis에 장바구니가 없으면 DB에서 로드한다")
	void getFromDb() {
		when(cartRedisService.existsCartInRedis(userId)).thenReturn(false);
		when(cartRedisService.getCartFromRedis(userId)).thenReturn(cartItems);

		Cart cart = Cart.builder().cartId(UUID.randomUUID()).user(User.builder().userId(userId).build()).build();
		CartItem cartItem = CartItem.builder()
			.cart(cart)
			.menu(Menu.builder().menuId(menuId).store(Store.builder().storeId(storeId).build()).build())
			.quantity(2)
			.build();

		when(cartRepository.findByUser_UserId(userId)).thenReturn(Optional.of(cart));
		when(cartItemRepository.findByCart_CartId(cart.getCartId())).thenReturn(List.of(cartItem));

		List<RedisCartItem> result = cartService.getCartFromCache(userId);

		verify(cartRedisService).saveCartToRedis(eq(userId), any());
		assertThat(result).isEqualTo(cartItems);
	}

	@Test
	@DisplayName("장바구니의 모든 아이템을 삭제할 수 있다")
	void clearItems() {
		when(cartRedisService.clearCartItems(userId)).thenReturn("성공");

		cartService.clearCartItems(userId);

		verify(cartRedisService).clearCartItems(userId);
	}

	@Test
	@DisplayName("DB의 장바구니 데이터를 Redis로 로드할 수 있다")
	void loadDbToRedis() {
		Cart cart = Cart.builder().cartId(UUID.randomUUID()).user(User.builder().userId(userId).build()).build();
		CartItem cartItem = CartItem.builder()
			.cart(cart)
			.menu(Menu.builder().menuId(menuId).store(Store.builder().storeId(storeId).build()).build())
			.quantity(2)
			.build();

		when(cartRepository.findByUser_UserId(userId)).thenReturn(Optional.of(cart));
		when(cartItemRepository.findByCart_CartId(cart.getCartId())).thenReturn(List.of(cartItem));
		when(cartRedisService.saveCartToRedis(eq(userId), any())).thenReturn("성공");

		cartService.loadDbToRedis(userId);

		verify(cartRedisService).saveCartToRedis(eq(userId), argThat(items ->
			items.size() == 1 &&
				items.get(0).getMenuId().equals(menuId) &&
				items.get(0).getStoreId().equals(storeId) &&
				items.get(0).getQuantity() == 2
		));
	}

	@Test
	@DisplayName("Redis의 장바구니 데이터를 DB에 동기화할 수 있다")
	void syncRedisToDb() {
		RedisCartItem redisItem = RedisCartItem.builder().menuId(menuId).storeId(storeId).quantity(3).build();
		Cart cart = Cart.builder().cartId(UUID.randomUUID()).user(User.builder().userId(userId).build()).build();

		when(cartRedisService.existsCartInRedis(userId)).thenReturn(true);
		when(cartRedisService.getCartFromRedis(userId)).thenReturn(List.of(redisItem));
		when(cartRepository.findByUser_UserId(userId)).thenReturn(Optional.of(cart));

		cartService.syncRedisToDb(userId);

		verify(cartItemRepository).deleteByCart_CartId(cart.getCartId());
		verify(cartItemRepository).saveAll(argThat((List<CartItem> items) ->
			items.size() == 1 &&
				items.get(0).getQuantity() == 3
		));
	}

	@Test
	@DisplayName("모든 사용자의 Redis 장바구니를 DB에 동기화할 수 있다")
	void syncAllCarts() {
		Set<String> cartKeys = Set.of("cart:1", "cart:2");
		when(cartRedisService.getAllCartKeys()).thenReturn(cartKeys);
		when(cartRedisService.extractUserIdFromKey("cart:1")).thenReturn(1L);
		when(cartRedisService.extractUserIdFromKey("cart:2")).thenReturn(2L);

		RedisCartItem redisItem1 = RedisCartItem.builder()
			.menuId(UUID.randomUUID())
			.storeId(storeId)
			.quantity(1)
			.build();
		RedisCartItem redisItem2 = RedisCartItem.builder()
			.menuId(UUID.randomUUID())
			.storeId(storeId)
			.quantity(2)
			.build();
		when(cartRedisService.getCartFromRedis(1L)).thenReturn(List.of(redisItem1));
		when(cartRedisService.getCartFromRedis(2L)).thenReturn(List.of(redisItem2));

		Cart cart1 = Cart.builder().cartId(UUID.randomUUID()).user(User.builder().userId(1L).build()).build();
		Cart cart2 = Cart.builder().cartId(UUID.randomUUID()).user(User.builder().userId(2L).build()).build();
		when(cartRepository.findByUser_UserId(1L)).thenReturn(Optional.of(cart1));
		when(cartRepository.findByUser_UserId(2L)).thenReturn(Optional.of(cart2));

		cartService.syncAllCartsToDb();

		verify(cartItemRepository).deleteByCart_CartId(cart1.getCartId());
		verify(cartItemRepository).deleteByCart_CartId(cart2.getCartId());
		verify(cartItemRepository, times(2)).saveAll(any());
	}
}