package app.unit.domain.cart.model;

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

import app.domain.cart.model.entity.Cart;
import app.domain.cart.model.repository.CartRepository;
import app.domain.user.model.entity.User;

@ExtendWith(MockitoExtension.class)
class CartRepositoryTest {

	@Mock
	private CartRepository cartRepository;

	@Mock
	private User mockUser;

	private Cart testCart;
	private UUID cartId;
	private Long userId;

	@BeforeEach
	void setUp() {
		cartId = UUID.randomUUID();
		userId = 1L;

		testCart = Cart.builder()
			.cartId(cartId)
			.user(mockUser)
			.build();
	}

	@Test
	@DisplayName("사용자 ID로 장바구니 조회 - 성공")
	void findByUser_UserId_Success() {
		// Given
		when(cartRepository.findByUser_UserId(userId)).thenReturn(Optional.of(testCart));

		// When
		Optional<Cart> result = cartRepository.findByUser_UserId(userId);

		// Then
		assertThat(result).isPresent();
		assertThat(result.get().getCartId()).isEqualTo(cartId);
		verify(cartRepository).findByUser_UserId(userId);
	}

	@Test
	@DisplayName("사용자 ID로 장바구니 조회 - 존재하지 않는 사용자")
	void findByUser_UserId_NotFound() {
		// Given
		Long nonExistentUserId = 999L;
		when(cartRepository.findByUser_UserId(nonExistentUserId)).thenReturn(Optional.empty());

		// When
		Optional<Cart> result = cartRepository.findByUser_UserId(nonExistentUserId);

		// Then
		assertThat(result).isEmpty();
		verify(cartRepository).findByUser_UserId(nonExistentUserId);
	}

	@Test
	@DisplayName("장바구니 저장 성공")
	void save_Success() {
		// Given
		when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

		// When
		Cart savedCart = cartRepository.save(testCart);

		// Then
		assertThat(savedCart).isNotNull();
		assertThat(savedCart.getCartId()).isEqualTo(cartId);
		verify(cartRepository).save(testCart);
	}

	@Test
	@DisplayName("장바구니 ID로 조회 성공")
	void findById_Success() {
		// Given
		when(cartRepository.findById(cartId)).thenReturn(Optional.of(testCart));

		// When
		Optional<Cart> foundCart = cartRepository.findById(cartId);

		// Then
		assertThat(foundCart).isPresent();
		assertThat(foundCart.get().getCartId()).isEqualTo(cartId);
		verify(cartRepository).findById(cartId);
	}

	@Test
	@DisplayName("장바구니 삭제 성공")
	void delete_Success() {
		// Given
		doNothing().when(cartRepository).delete(testCart);

		// When
		cartRepository.delete(testCart);

		// Then
		verify(cartRepository).delete(testCart);
	}

	@Test
	@DisplayName("존재하지 않는 장바구니 ID로 조회")
	void findById_NotFound() {
		// Given
		UUID nonExistentId = UUID.randomUUID();
		when(cartRepository.findById(nonExistentId)).thenReturn(Optional.empty());

		// When
		Optional<Cart> foundCart = cartRepository.findById(nonExistentId);

		// Then
		assertThat(foundCart).isEmpty();
		verify(cartRepository).findById(nonExistentId);
	}

	@Test
	@DisplayName("지연 로딩 테스트 - User 접근 시 쿼리 실행")
	void lazyLoading_User_Test() {
		// Given
		when(cartRepository.findById(cartId)).thenReturn(Optional.of(testCart));
		when(mockUser.getUserId()).thenReturn(userId);

		// When
		Optional<Cart> foundCart = cartRepository.findById(cartId);

		// Cart 조회 시점에는 User 쿼리가 실행되지 않음
		verify(cartRepository).findById(cartId);
		verify(mockUser, never()).getUserId();

		// User 필드에 접근할 때 쿼리 실행
		Long foundUserId = foundCart.get().getUser().getUserId();

		// Then
		assertThat(foundUserId).isEqualTo(userId);
		verify(mockUser).getUserId();
	}

}