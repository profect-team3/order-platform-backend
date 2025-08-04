package app.unit.domain.cart.service;

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

import app.domain.cart.model.dto.AddCartItemRequest;
import app.domain.cart.model.dto.RedisCartItem;
import app.domain.cart.model.entity.Cart;
import app.domain.cart.model.entity.CartItem;
import app.domain.cart.model.repository.CartItemRepository;
import app.domain.cart.model.repository.CartRepository;
import app.domain.cart.service.CartRedisService;
import app.domain.cart.service.CartService;
import app.domain.menu.model.entity.Menu;
import app.domain.menu.model.repository.MenuRepository;
import app.domain.store.model.entity.Store;
import app.domain.store.repository.StoreRepository;
import app.domain.user.model.entity.User;
import app.global.apiPayload.code.status.ErrorStatus;
import app.global.apiPayload.exception.GeneralException;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

	@Mock
	private CartRedisService cartRedisService;

	@Mock
	private CartRepository cartRepository;

	@Mock
	private CartItemRepository cartItemRepository;

	@Mock
	private MenuRepository menuRepository;

	@Mock
	private StoreRepository storeRepository;

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
		when(menuRepository.existsById(menuId)).thenReturn(true);
		when(storeRepository.existsById(storeId)).thenReturn(true);
		when(cartRedisService.existsCartInRedis(userId)).thenReturn(true);
		when(cartRedisService.getCartFromRedis(userId)).thenReturn(cartItems);
		when(cartRedisService.saveCartToRedis(eq(userId), any())).thenReturn("성공");

		cartService.addCartItem(request);

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
		when(menuRepository.existsById(menuId)).thenReturn(true);
		when(storeRepository.existsById(storeId)).thenReturn(true);
		when(cartRedisService.existsCartInRedis(userId)).thenReturn(true);
		when(cartRedisService.getCartFromRedis(userId)).thenReturn(cartItems);
		when(cartRedisService.saveCartToRedis(eq(userId), any())).thenReturn("성공");

		cartService.addCartItem(request);

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
		when(menuRepository.existsById(menuId)).thenReturn(true);
		when(storeRepository.existsById(storeId)).thenReturn(true);
		when(cartRedisService.existsCartInRedis(userId)).thenReturn(true);
		when(cartRedisService.getCartFromRedis(userId)).thenReturn(cartItems);
		when(cartRedisService.saveCartToRedis(eq(userId), any())).thenReturn("성공");

		cartService.addCartItem(request);

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

		cartService.updateCartItem(menuId, 5);

		verify(cartRedisService).saveCartToRedis(eq(userId), argThat(items ->
			items.get(0).getQuantity() == 5
		));
	}

	@Test
	@DisplayName("장바구니에서 특정 아이템을 삭제할 수 있다")
	void removeItem() {
		when(cartRedisService.removeCartItem(userId, menuId)).thenReturn("성공");

		cartService.removeCartItem(menuId);

		verify(cartRedisService).removeCartItem(userId, menuId);
	}

	@Test
	@DisplayName("Redis에 장바구니가 있으면 Redis에서 조회한다")
	void getFromRedis() {
		when(cartRedisService.existsCartInRedis(userId)).thenReturn(true);
		when(cartRedisService.getCartFromRedis(userId)).thenReturn(cartItems);

		List<RedisCartItem> result = cartService.getCartFromCache();

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

		List<RedisCartItem> result = cartService.getCartFromCache();

		verify(cartRedisService).saveCartToRedis(eq(userId), any());
		assertThat(result).isEqualTo(cartItems);
	}

	@Test
	@DisplayName("장바구니의 모든 아이템을 삭제할 수 있다")
	void clearItems() {
		when(cartRedisService.clearCartItems(userId)).thenReturn("성공");

		cartService.clearCartItems();

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

		cartService.loadDbToRedis();

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
		Menu menu = Menu.builder().menuId(menuId).store(Store.builder().storeId(storeId).build()).build();

		when(cartRedisService.existsCartInRedis(userId)).thenReturn(true);
		when(cartRedisService.getCartFromRedis(userId)).thenReturn(List.of(redisItem));
		when(cartRepository.findByUser_UserId(userId)).thenReturn(Optional.of(cart));
		when(menuRepository.existsById(menuId)).thenReturn(true);
		when(menuRepository.findById(menuId)).thenReturn(Optional.of(menu));

		cartService.syncRedisToDb(userId);

		verify(cartItemRepository).deleteByCart_CartId(cart.getCartId());
		verify(menuRepository).existsById(menuId);
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

		UUID menuId1 = UUID.randomUUID();
		UUID menuId2 = UUID.randomUUID();
		RedisCartItem redisItem1 = RedisCartItem.builder()
			.menuId(menuId1)
			.storeId(storeId)
			.quantity(1)
			.build();
		RedisCartItem redisItem2 = RedisCartItem.builder()
			.menuId(menuId2)
			.storeId(storeId)
			.quantity(2)
			.build();
		when(cartRedisService.existsCartInRedis(1L)).thenReturn(true);
		when(cartRedisService.existsCartInRedis(2L)).thenReturn(true);
		when(cartRedisService.getCartFromRedis(1L)).thenReturn(List.of(redisItem1));
		when(cartRedisService.getCartFromRedis(2L)).thenReturn(List.of(redisItem2));

		Cart cart1 = Cart.builder().cartId(UUID.randomUUID()).user(User.builder().userId(1L).build()).build();
		Cart cart2 = Cart.builder().cartId(UUID.randomUUID()).user(User.builder().userId(2L).build()).build();
		when(cartRepository.findByUser_UserId(1L)).thenReturn(Optional.of(cart1));
		when(cartRepository.findByUser_UserId(2L)).thenReturn(Optional.of(cart2));

		when(menuRepository.existsById(menuId1)).thenReturn(true);
		when(menuRepository.existsById(menuId2)).thenReturn(true);
		when(menuRepository.findById(menuId1)).thenReturn(Optional.of(
			Menu.builder().menuId(menuId1).store(Store.builder().storeId(storeId).build()).build()));
		when(menuRepository.findById(menuId2)).thenReturn(Optional.of(
			Menu.builder().menuId(menuId2).store(Store.builder().storeId(storeId).build()).build()));

		cartService.syncAllCartsToDb();

		verify(cartItemRepository).deleteByCart_CartId(cart1.getCartId());
		verify(cartItemRepository).deleteByCart_CartId(cart2.getCartId());
		verify(cartItemRepository, times(2)).saveAll(any());
	}

	@Test
	@DisplayName("존재하지 않는 menuId로 장바구니 추가 시 예외 발생")
	void addCartItem_MenuNotFound() {
		// Given
		AddCartItemRequest request = new AddCartItemRequest(menuId, storeId, 2);
		when(menuRepository.existsById(menuId)).thenReturn(false);

		// When & Then
		assertThatThrownBy(() -> cartService.addCartItem(request))
			.isInstanceOf(GeneralException.class)
			.satisfies(ex -> {
				GeneralException generalEx = (GeneralException)ex;
				assertThat(generalEx.getErrorReason().getCode()).isEqualTo(ErrorStatus.MENU_NOT_FOUND.getCode());
			});

		verify(menuRepository).existsById(menuId);
		verify(storeRepository, never()).existsById(any());
		verify(cartRedisService, never()).getCartFromRedis(any());
	}

	@Test
	@DisplayName("존재하지 않는 storeId로 장바구니 추가 시 예외 발생")
	void addCartItem_StoreNotFound() {
		// Given
		AddCartItemRequest request = new AddCartItemRequest(menuId, storeId, 2);
		when(menuRepository.existsById(menuId)).thenReturn(true);
		when(storeRepository.existsById(storeId)).thenReturn(false);

		// When & Then
		assertThatThrownBy(() -> cartService.addCartItem(request))
			.isInstanceOf(GeneralException.class)
			.satisfies(ex -> {
				GeneralException generalEx = (GeneralException)ex;
				assertThat(generalEx.getErrorReason().getCode()).isEqualTo("해당 가맹점을 찾을 수 없습니다.");
			});

		verify(menuRepository).existsById(menuId);
		verify(storeRepository).existsById(storeId);
		verify(cartRedisService, never()).getCartFromRedis(any());
	}
}