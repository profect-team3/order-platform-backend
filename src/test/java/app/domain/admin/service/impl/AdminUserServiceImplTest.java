package app.domain.admin.service.impl;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;

import app.domain.admin.model.dto.response.GetUserDetailResponse;
import app.domain.admin.model.dto.response.GetUserListResponse;
import app.domain.order.model.dto.response.GetOrderListResponse;
import app.domain.order.model.entity.Orders;
import app.domain.order.model.mapper.OrdersRepository;
import app.domain.user.model.UserAddressRepository;
import app.domain.user.model.UserRepository;
import app.domain.user.model.entity.User;
import app.domain.user.model.entity.UserAddress;
import app.domain.user.model.entity.enums.UserRole;
import app.global.apiPayload.PagedResponse;

@ExtendWith(MockitoExtension.class)
class AdminUserServiceImplTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private OrdersRepository ordersRepository;

	@Mock
	private UserAddressRepository userAddressRepository;

	@InjectMocks
	private AdminUserServiceImpl adminUserService;

	@Test
	@DisplayName("유저 목록을 조회한다")
	void getAllUsersTest() {
		// given
		Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());

		User user = User.builder()
			.userId(1L)
			.email("test@example.com")
			.username("테스트")
			.build();
		ReflectionTestUtils.setField(user, "createdAt", LocalDateTime.now());

		Page<User> userPage = new PageImpl<>(List.of(user), pageable, 1);
		when(userRepository.findAllByRole(UserRole.USER, pageable)).thenReturn(userPage);

		// when
		PagedResponse<GetUserListResponse> response = adminUserService.getAllUsers(pageable);

		// then
		assertThat(response.content().get(0).email()).isEqualTo("test@example.com");
		verify(userRepository, times(1)).findAllByRole(UserRole.USER, pageable);
	}

	@Test
	@DisplayName("유저 기본 정보와 주소 리스트를 조회한다")
	void getUserDetail_basicInfoAndAddress() {
		// given
		Long userId = 1L;
		User user = User.builder()
			.userId(userId)
			.email("test@example.com")
			.username("테스트")
			.build();
		ReflectionTestUtils.setField(user, "createdAt", LocalDateTime.now());

		List<UserAddress> addressEntities = List.of(
			UserAddress.builder().alias("우리집").address("서울시 강남구").addressDetail("303호").isDefault(true).build()
		);

		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(userAddressRepository.findAllByUserUserId(userId)).thenReturn(addressEntities);

		// when
		GetUserDetailResponse result = adminUserService.getUserDetailById(userId);

		// then
		assertThat(result.email()).isEqualTo("test@example.com");
		assertThat(result.address()).hasSize(1);

		verify(userRepository).findById(userId);
		verify(userAddressRepository).findAllByUserUserId(userId);
	}

	@Test
	@DisplayName("유저 주문 목록을 조회한다")
	void getUserOrderList_onlyOrders() {
		// given
		Long userId = 1L;
		User user = User.builder()
			.userId(userId)
			.email("test@example.com")
			.username("테스트")
			.build();

		Pageable pageable = PageRequest.of(0, 5);
		Page<Orders> ordersPage = new PageImpl<>(List.of(
			Orders.builder().ordersId(UUID.fromString("11111111-1111-1111-1111-111111111111")).user(user).build()
		));

		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(ordersRepository.findAllByUserAndDeliveryAddressIsNotNull(user, pageable)).thenReturn(ordersPage);

		// when
		PagedResponse<GetOrderListResponse> result = adminUserService.getUserOrderListById(userId, pageable);

		// then
		assertThat(result.totalElements()).isEqualTo(1);
		assertThat(result.content()).hasSize(1);

		verify(userRepository).findById(userId);
		verify(ordersRepository).findAllByUserAndDeliveryAddressIsNotNull(user, pageable);
	}

}