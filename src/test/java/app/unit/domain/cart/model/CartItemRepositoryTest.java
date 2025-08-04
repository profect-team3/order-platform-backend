package app.unit.domain.cart.model;

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

import app.domain.cart.model.entity.Cart;
import app.domain.cart.model.entity.CartItem;
import app.domain.cart.model.repository.CartItemRepository;
import app.domain.menu.model.entity.Menu;

@ExtendWith(MockitoExtension.class)
class CartItemRepositoryTest {

	@Mock
	private CartItemRepository cartItemRepository;

	@Mock
	private Cart mockCart;

	@Mock
	private Menu mockMenu;

	private CartItem testCartItem1;
	private CartItem testCartItem2;
	private UUID cartId;
	private UUID cartItemId1;
	private UUID cartItemId2;

	@BeforeEach
	void setUp() {
		cartId = UUID.randomUUID();
		cartItemId1 = UUID.randomUUID();
		cartItemId2 = UUID.randomUUID();

		testCartItem1 = CartItem.builder()
			.cartItemId(cartItemId1)
			.cart(mockCart)
			.menu(mockMenu)
			.quantity(2)
			.build();

		testCartItem2 = CartItem.builder()
			.cartItemId(cartItemId2)
			.cart(mockCart)
			.menu(mockMenu)
			.quantity(1)
			.build();
	}

	@Test
	@DisplayName("장바구니 ID로 장바구니 아이템 조회 - 성공")
	void findByCart_CartId_Success() {
		// Given
		List<CartItem> cartItems = List.of(testCartItem1, testCartItem2);
		when(cartItemRepository.findByCart_CartId(cartId)).thenReturn(cartItems);

		// When
		List<CartItem> result = cartItemRepository.findByCart_CartId(cartId);

		// Then
		assertThat(result).hasSize(2);
		assertThat(result).containsExactly(testCartItem1, testCartItem2);
		verify(cartItemRepository).findByCart_CartId(cartId);
	}

	@Test
	@DisplayName("장바구니 ID로 장바구니 아이템 조회 - 빈 결과")
	void findByCart_CartId_Empty() {
		// Given
		UUID nonExistentCartId = UUID.randomUUID();
		when(cartItemRepository.findByCart_CartId(nonExistentCartId)).thenReturn(List.of());

		// When
		List<CartItem> result = cartItemRepository.findByCart_CartId(nonExistentCartId);

		// Then
		assertThat(result).isEmpty();
		verify(cartItemRepository).findByCart_CartId(nonExistentCartId);
	}

	@Test
	@DisplayName("장바구니 아이템 저장 성공")
	void save_Success() {
		// Given
		when(cartItemRepository.save(any(CartItem.class))).thenReturn(testCartItem1);

		// When
		CartItem savedCartItem = cartItemRepository.save(testCartItem1);

		// Then
		assertThat(savedCartItem).isNotNull();
		assertThat(savedCartItem.getCartItemId()).isEqualTo(cartItemId1);
		verify(cartItemRepository).save(testCartItem1);
	}

	@Test
	@DisplayName("장바구니 아이템 ID로 조회 성공")
	void findById_Success() {
		// Given
		when(cartItemRepository.findById(cartItemId1)).thenReturn(Optional.of(testCartItem1));

		// When
		Optional<CartItem> foundCartItem = cartItemRepository.findById(cartItemId1);

		// Then
		assertThat(foundCartItem).isPresent();
		assertThat(foundCartItem.get().getCartItemId()).isEqualTo(cartItemId1);
		verify(cartItemRepository).findById(cartItemId1);
	}

	@Test
	@DisplayName("장바구니 ID로 장바구니 아이템 삭제")
	void deleteByCart_CartId() {
		// Given
		doNothing().when(cartItemRepository).deleteByCart_CartId(cartId);

		// When
		cartItemRepository.deleteByCart_CartId(cartId);

		// Then
		verify(cartItemRepository).deleteByCart_CartId(cartId);
	}

	@Test
	@DisplayName("존재하지 않는 장바구니 아이템 ID로 조회")
	void findById_NotFound() {
		// Given
		UUID nonExistentId = UUID.randomUUID();
		when(cartItemRepository.findById(nonExistentId)).thenReturn(Optional.empty());

		// When
		Optional<CartItem> foundCartItem = cartItemRepository.findById(nonExistentId);

		// Then
		assertThat(foundCartItem).isEmpty();
		verify(cartItemRepository).findById(nonExistentId);
	}

	@Test
	@DisplayName("장바구니 아이템 삭제 성공")
	void delete_Success() {
		// Given
		doNothing().when(cartItemRepository).delete(testCartItem1);

		// When
		cartItemRepository.delete(testCartItem1);

		// Then
		verify(cartItemRepository).delete(testCartItem1);
	}

	@Test
	@DisplayName("지연 로딩 테스트 - Cart 접근 시 쿼리 실행")
	void lazyLoading_Cart_Test() {
		// Given
		when(cartItemRepository.findById(cartItemId1)).thenReturn(Optional.of(testCartItem1));
		when(mockCart.getCartId()).thenReturn(cartId);

		// When
		Optional<CartItem> foundCartItem = cartItemRepository.findById(cartItemId1);

		// CartItem 조회 시점에는 Cart 쿼리가 실행되지 않음
		verify(cartItemRepository).findById(cartItemId1);
		verify(mockCart, never()).getCartId();

		// Cart 필드에 접근할 때 쿼리 실행
		UUID foundCartId = foundCartItem.get().getCart().getCartId();

		// Then
		assertThat(foundCartId).isEqualTo(cartId);
		verify(mockCart).getCartId();
	}

	@Test
	@DisplayName("지연 로딩 테스트 - Menu 접근 시 쿼리 실행")
	void lazyLoading_Menu_Test() {
		// Given
		UUID menuId = UUID.randomUUID();
		when(cartItemRepository.findById(cartItemId1)).thenReturn(Optional.of(testCartItem1));
		when(mockMenu.getMenuId()).thenReturn(menuId);

		// When
		Optional<CartItem> foundCartItem = cartItemRepository.findById(cartItemId1);

		// CartItem 조회 시점에는 Menu 쿼리가 실행되지 않음
		verify(cartItemRepository).findById(cartItemId1);
		verify(mockMenu, never()).getMenuId();

		// Menu 필드에 접근할 때 쿼리 실행
		UUID foundMenuId = foundCartItem.get().getMenu().getMenuId();

		// Then
		assertThat(foundMenuId).isEqualTo(menuId);
		verify(mockMenu).getMenuId();
	}

	@Test
	@DisplayName("장바구니 아이템 ID로 삭제")
	void deleteById_Success() {
		// Given
		doNothing().when(cartItemRepository).deleteById(cartItemId1);

		// When
		cartItemRepository.deleteById(cartItemId1);

		// Then
		verify(cartItemRepository).deleteById(cartItemId1);
	}
}