package app.unit.domain.manager;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;

import app.domain.customer.dto.response.GetStoreListResponse;
import app.domain.manager.ManagerService;
import app.domain.manager.dto.response.GetCustomerDetailResponse;
import app.domain.manager.dto.response.GetCustomerListResponse;
import app.domain.manager.dto.response.GetStoreDetailResponse;
import app.domain.manager.status.ManagerErrorStatus;
import app.domain.menu.model.entity.Category;
import app.domain.order.model.dto.response.OrderDetailResponse;
import app.domain.order.model.entity.Orders;
import app.domain.order.model.repository.OrderItemRepository;
import app.domain.order.model.repository.OrdersRepository;
import app.domain.review.model.ReviewRepository;
import app.domain.store.model.StoreQueryRepository;
import app.domain.store.model.entity.Region;
import app.domain.store.model.entity.Store;
import app.domain.store.repository.StoreRepository;
import app.domain.store.status.StoreAcceptStatus;
import app.domain.user.UserSearchRepository;
import app.domain.user.model.UserAddressRepository;
import app.domain.user.model.UserRepository;
import app.domain.user.model.entity.User;
import app.domain.user.model.entity.UserAddress;
import app.domain.user.model.entity.enums.UserRole;
import app.global.apiPayload.PagedResponse;
import app.global.apiPayload.code.status.ErrorStatus;
import app.global.apiPayload.exception.GeneralException;
import app.global.config.SecurityConfig;

@ExtendWith(MockitoExtension.class)
@Import(SecurityConfig.class)
class ManagerServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private UserSearchRepository userRepositoryCustom;

	@Mock
	private OrdersRepository ordersRepository;

	@Mock
	private UserAddressRepository userAddressRepository;

	@Mock
	private OrderItemRepository orderItemRepository;

	@Mock
	private StoreRepository storeRepository;

	@Mock
	private ReviewRepository reviewRepository;

	@Mock
	private StoreQueryRepository storeQueryRepository;

	@InjectMocks
	private ManagerService managerService;

	@Test
	@DisplayName("사용자 목록 정상 조회")
	void getAllCustomer_shouldReturnMultipleCustomer() {

		User user1 = User.builder()
			.userId(1L)
			.email("user1@example.com")
			.username("테스트1")
			.build();
		User user2 = User.builder()
			.userId(2L)
			.email("user2@example.com")
			.username("테스트2")
			.build();

		Page<User> page = new PageImpl<>(List.of(user1, user2));
		Pageable pageable = PageRequest.of(0, 10);

		when(userRepository.findAllByUserRole(UserRole.CUSTOMER, pageable)).thenReturn(page);

		PagedResponse<GetCustomerListResponse> result = managerService.getAllCustomer(pageable);

		assertThat(result.getContent()).hasSize(2);
		assertThat(result.getContent())
			.extracting(GetCustomerListResponse::getEmail)
			.containsExactly("user1@example.com", "user2@example.com");
	}

	@Test
	@DisplayName("페이지별 사용자 목록을 조회")
	void getAllCustomer_withPagination_shouldLimitResults() {

		List<User> users = IntStream.range(1, 16)
			.mapToObj(i -> User.builder()
				.userId((long)i)
				.username("test" + i)
				.email("test" + i + "@mail.com")
				.password("password")
				.build())
			.toList();

		Page<User> page = new PageImpl<>(users.subList(0, 10), PageRequest.of(0, 10), 15);
		Pageable pageable = PageRequest.of(0, 10);

		when(userRepository.findAllByUserRole(eq(UserRole.CUSTOMER), eq(pageable))).thenReturn(page);

		PagedResponse<GetCustomerListResponse> result = managerService.getAllCustomer(pageable);
		assertThat(result.getContent()).hasSize(10);
	}

	@Test
	@DisplayName("정렬 조건에따라서 사용자 목록을 조회")
	void getAllCustomer_withSorting_shouldReturnSorted() {

		User newUser = User.builder()
			.userId(1L)
			.email("test1@example.com")
			.username("테스트1")
			.build();
		User oldUser = User.builder()
			.userId(2L)
			.email("test2@example.com")
			.username("테스트2")
			.build();

		ReflectionTestUtils.setField(newUser, "createdAt", LocalDateTime.now());
		ReflectionTestUtils.setField(oldUser, "createdAt", LocalDateTime.now().minusDays(1));

		Page<User> page = new PageImpl<>(List.of(newUser, oldUser));
		when(userRepository.findAllByUserRole(eq(UserRole.CUSTOMER),
			eq(PageRequest.of(0, 10, Sort.by("createdAt").descending()))))
			.thenReturn(page);

		PagedResponse<GetCustomerListResponse> result = managerService.getAllCustomer(
			PageRequest.of(0, 10, Sort.by("createdAt").descending()));

		assertThat(result.getContent().get(0).getEmail()).isEqualTo("test1@example.com");
	}

	@Test
	@DisplayName("사용자 상세 정보 및 주소 목록 조회")
	void getCustomerDetail_shouldReturnCustomerAndAddressList() {

		User user = User.builder()
			.userId(1L)
			.realName("홍길동")
			.email("test@example.com")
			.build();

		List<UserAddress> addresses = List.of(
			UserAddress.builder()
				.user(user)
				.alias("집")
				.address("서울시 마포구")
				.addressDetail("101호")
				.isDefault(true)
				.build(),

			UserAddress.builder()
				.user(user)
				.alias("회사")
				.address("서울시 강남구")
				.addressDetail("202호")
				.isDefault(false)
				.build()
		);

		when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
		when(userAddressRepository.findAllByUserUserId(user.getUserId())).thenReturn(addresses);

		GetCustomerDetailResponse result = managerService.getCustomerDetailById(user.getUserId());

		assertThat(result.getName()).isEqualTo("홍길동");
		assertThat(result.getAddress()).hasSize(2);
	}

	@Test
	@DisplayName("사용자 주문 목록을 조회")
	void getCustomerOrderList_onlyOrders() {

		Long userId = 1L;
		User user = User.builder()
			.userId(userId)
			.email("test@example.com")
			.username("테스트")
			.build();

		Store store = Store.builder()
			.storeId(UUID.randomUUID())
			.storeName("테스트매장")
			.build();

		Pageable pageable = PageRequest.of(0, 5);
		Page<Orders> ordersPage = new PageImpl<>(List.of(
			Orders.builder().ordersId(UUID.fromString("11111111-1111-1111-1111-111111111111"))
				.deliveryAddress("강남")
				.user(user)
				.store(store)
				.build()
		));

		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(ordersRepository.findAllByUserAndDeliveryAddressIsNotNull(user, pageable)).thenReturn(ordersPage);

		PagedResponse<OrderDetailResponse> result = managerService.getCustomerOrderListById(userId, pageable);

		assertThat(result.getTotalElements()).isEqualTo(1);
		assertThat(result.getContent()).hasSize(1);
	}

	@DisplayName("사용자 상세 조회 시 주소 목록이 없을 경우 빈 리스트 반환")
	@Test
	void getCustomerDetail_noAddresses_shouldReturnEmptyList() {

		Long userId = 1L;
		User user = User.builder()
			.userId(userId)
			.realName("홍길동")
			.email("hong@test.com")
			.build();

		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(userAddressRepository.findAllByUserUserId(userId)).thenReturn(List.of());

		GetCustomerDetailResponse result = managerService.getCustomerDetailById(userId);

		assertThat(result.getAddress()).isEmpty();
		assertThat(result.getName()).isEqualTo("홍길동");
		assertThat(result.getEmail()).isEqualTo("hong@test.com");

		verify(userRepository, times(1)).findById(userId);
		verify(userAddressRepository, times(1)).findAllByUserUserId(userId);
	}

	@Test
	@DisplayName("사용자 주문 목록 조회 시 주문 목록이 없을 경우 빈 페이지 반환")
	void getCustomerOrderList_noOrders_shouldReturnEmptyList() {

		Long userId = 1L;
		User user = User.builder()
			.userId(userId)
			.email("test@example.com")
			.username("테스트")
			.build();

		Pageable pageable = PageRequest.of(0, 5);
		Page<Orders> emptyOrdersPage = new PageImpl<>(List.of());

		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(ordersRepository.findAllByUserAndDeliveryAddressIsNotNull(user, pageable)).thenReturn(emptyOrdersPage);

		PagedResponse<OrderDetailResponse> result = managerService.getCustomerOrderListById(userId, pageable);

		assertThat(result.getContent()).isEmpty();
		assertThat(result.getTotalElements()).isEqualTo(0);
	}

	@Test
	@DisplayName("입력한 키워드에따라 사용자 조회")
	void searchUsers_withKeyword_shouldReturnFilteredUsers() {

		String keyword = "test";
		Pageable pageable = PageRequest.of(0, 10);

		User user1 = User.builder()
			.userId(1L)
			.username("testUser1")
			.email("test1@mail.com")
			.build();

		User user2 = User.builder()
			.userId(2L)
			.username("anotherUser")
			.email("another@mail.com")
			.build();

		Page<User> page = new PageImpl<>(List.of(user1));
		when(userRepositoryCustom.searchUser(eq(keyword), any(Pageable.class))).thenReturn(page);

		PagedResponse<GetCustomerListResponse> result = managerService.searchCustomer(keyword, pageable);

		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getContent().get(0).getName()).isEqualTo("testUser1");
	}

	@Test
	@DisplayName("사용자 검색 결과가 없으면 빈페이지를 반환")
	void searchCustomer_noResults_shouldReturnEmptyList() {

		String keyword = "unknown";
		Pageable pageable = PageRequest.of(0, 10);

		Page<User> emptyPage = new PageImpl<>(Collections.emptyList());
		when(userRepositoryCustom.searchUser(eq(keyword), any(Pageable.class))).thenReturn(emptyPage);

		PagedResponse<GetCustomerListResponse> result = managerService.searchCustomer(keyword, pageable);

		assertThat(result.getContent()).isEmpty();
		assertThat(result.getTotalElements()).isZero();
	}

	@Test
	@DisplayName("사용자 주문 목록 조회 시 사용자 아이디가 올바르지 않을 경우 예외처리")
	void getCustomerOrderList_userNotFound_throwsException() {

		Long userId = 999L;
		Pageable pageable = PageRequest.of(0, 5);

		when(userRepository.findById(userId)).thenReturn(Optional.empty());

		GeneralException ex = catchThrowableOfType(
			() -> managerService.getCustomerOrderListById(userId, pageable),
			GeneralException.class
		);

		// then
		assertThat(ex.getErrorReasonHttpStatus().getHttpStatus()).isEqualTo(ErrorStatus.USER_NOT_FOUND);
		assertThat(ex.getErrorReasonHttpStatus().getMessage()).isEqualTo("존재하지 않는 사용자입니다.");
	}

	@Test
	@DisplayName("사용자 상세 조회 시 사용자 아이디가 올바르지 않을 경우 예외처리")
	void getUserDetail_userNotFound_shouldThrowException() {

		Long invalidUserId = 999L;
		when(userRepository.findById(invalidUserId)).thenReturn(Optional.empty());

		GeneralException ex = catchThrowableOfType(
			() -> managerService.getCustomerDetailById(invalidUserId),
			GeneralException.class
		);

		assertThat(ex.getErrorReason()).isEqualTo(ErrorStatus.USER_NOT_FOUND);
		assertThat(ex.getErrorReasonHttpStatus().getCode()).isEqualTo("존재하지 않는 사용자입니다.");
	}

	//--------
	@Test
	@DisplayName("가게 상세 조회 성공")
	void getStoreDetail_success() {
		// given
		UUID storeId = UUID.randomUUID();
		Long ownerId = 1L;

		User owner = User.builder()
			.userId(ownerId)
			.email("owner@example.com")
			.username("홍길동")
			.build();

		Region region = Region.builder()
			.regionName("마포구")
			.build();

		Category category = Category.builder()
			.categoryName("한식")
			.build();

		Store store = Store.builder()
			.storeId(storeId)
			.storeName("맛있는 족발집")
			.description("국내산 족발 사용")
			.address("서울시 마포구")
			.phoneNumber("010-1234-5678")
			.minOrderAmount(15000L)
			.storeAcceptStatus(StoreAcceptStatus.APPROVE)
			.region(region)
			.category(category)
			.user(owner)
			.build();

		when(storeRepository.findByStoreIdAndDeletedAtIsNull(storeId)).thenReturn(Optional.of(store));
		when(reviewRepository.getAverageRatingByStore(storeId)).thenReturn(4.7);

		// when
		GetStoreDetailResponse response = managerService.getStoreDetail(storeId);

		// then
		assertThat(response.getStoreId()).isEqualTo(storeId);
		assertThat(response.getStoreName()).isEqualTo("맛있는 족발집");
		assertThat(response.getDescription()).isEqualTo("국내산 족발 사용");
		assertThat(response.getAddress()).isEqualTo("서울시 마포구");
		assertThat(response.getPhoneNumber()).isEqualTo("010-1234-5678");
		assertThat(response.getMinOrderAmount()).isEqualTo(15000L);
		assertThat(response.getRegionName()).isEqualTo("마포구");
		assertThat(response.getCategoryName()).isEqualTo("한식");
		assertThat(response.getAverageRating()).isEqualTo(4.7);
		assertThat(response.getOwnerId()).isEqualTo(ownerId);
		assertThat(response.getOwnerEmail()).isEqualTo("owner@example.com");
		assertThat(response.getOwnerName()).isEqualTo("홍길동");
	}

	@Test
	@DisplayName("가게 승인 처리 성공")
	void approveStore_success() {
		// given
		UUID storeId = UUID.randomUUID();
		Region region = Region.builder().regionName("마포구").build();
		Category category = Category.builder().categoryName("한식").build();
		Store store = Store.builder()
			.storeId(storeId)
			.storeName("맛있는 족발집")
			.description("국내산 족발 사용")
			.address("서울시 마포구")
			.phoneNumber("010-1234-5678")
			.minOrderAmount(15000L)
			.storeAcceptStatus(StoreAcceptStatus.APPROVE)
			.region(region)
			.category(category)
			.build();
		store.updateAcceptStatus(StoreAcceptStatus.PENDING);

		when(storeRepository.findByStoreIdAndDeletedAtIsNull(storeId)).thenReturn(Optional.of(store));

		// when
		String result = managerService.approveStore(storeId, StoreAcceptStatus.APPROVE);

		// then
		assertThat(result).contains("변경 되었습니다");
		assertThat(store.getStoreAcceptStatus()).isEqualTo(StoreAcceptStatus.APPROVE);
	}

	@Test
	@DisplayName("가게 리스트 조회 성공")
	void getAllStore_success() {
		// given
		UUID storeId = UUID.randomUUID();
		Pageable pageable = PageRequest.of(0, 10);

		GetStoreListResponse dto = GetStoreListResponse.builder()
			.storeId(storeId)
			.storeName("맛있는 족발집")
			.address("서울시 마포구")
			.minOrderAmount(15000L)
			.averageRating(4.0)
			.build();

		Page<GetStoreListResponse> page = new PageImpl<>(List.of(dto), pageable, 1);

		given(storeQueryRepository.getAllStore(StoreAcceptStatus.APPROVE, pageable))
			.willReturn(PagedResponse.from(page));

		// when
		PagedResponse<GetStoreListResponse> response = managerService.getAllStore(StoreAcceptStatus.APPROVE, pageable);

		// then
		assertThat(response.getContent()).hasSize(1);
		assertThat(response.getContent().get(0).getStoreName()).isEqualTo("맛있는 족발집");
		assertThat(response.getContent().get(0).getAverageRating()).isEqualTo(4.0);
	}

	@Test
	@DisplayName("가게 키워드 검색 성공")
	void searchStore_success() {
		// given
		UUID storeId = UUID.randomUUID();
		Pageable pageable = PageRequest.of(0, 10);
		GetStoreListResponse responseDto = GetStoreListResponse.builder()
			.storeId(storeId)
			.storeName("맛있는 족발집")
			.address("서울시 마포구")
			.minOrderAmount(15000L)
			.averageRating(4.5)
			.build();

		Page<GetStoreListResponse> page = new PageImpl<>(List.of(responseDto), pageable, 1);
		PagedResponse<GetStoreListResponse> pagedResponse = PagedResponse.from(page);

		when(storeQueryRepository.searchStoresWithAvgRating("족발", StoreAcceptStatus.PENDING, pageable))
			.thenReturn(pagedResponse);

		// when
		PagedResponse<GetStoreListResponse> response = managerService.searchStore(StoreAcceptStatus.PENDING, "족발",
			pageable);

		// then
		assertThat(response.getContent()).hasSize(1);
		GetStoreListResponse dto = response.getContent().get(0);
		assertThat(dto.getStoreId()).isEqualTo(storeId);
		assertThat(dto.getStoreName()).isEqualTo("맛있는 족발집");
		assertThat(dto.getAddress()).isEqualTo("서울시 마포구");
		assertThat(dto.getMinOrderAmount()).isEqualTo(15000L);
		assertThat(dto.getAverageRating()).isEqualTo(4.5);
	}

	@Test
	@DisplayName("가게 상세 조회 실패 - 존재하지 않는 storeId")
	void getStoreDetail_notFound_shouldThrowException() {
		// given
		UUID invalidId = UUID.randomUUID();
		when(storeRepository.findByStoreIdAndDeletedAtIsNull(invalidId)).thenReturn(Optional.empty());

		// when
		GeneralException ex = catchThrowableOfType(
			() -> managerService.getStoreDetail(invalidId),
			GeneralException.class
		);

		// then
		assertThat(ex.getErrorReasonHttpStatus().getCode()).isEqualTo(ErrorStatus.STORE_NOT_FOUND);
	}

	@Test
	@DisplayName("가게 승인 실패 - 존재하지 않는 storeId")
	void approveStore_notFound_shouldThrowException() {
		// given
		UUID invalidId = UUID.randomUUID();
		when(storeRepository.findByStoreIdAndDeletedAtIsNull(invalidId)).thenReturn(Optional.empty());

		// when
		GeneralException ex = catchThrowableOfType(
			() -> managerService.approveStore(invalidId, StoreAcceptStatus.APPROVE),
			GeneralException.class
		);

		// then
		assertThat(ex.getErrorReasonHttpStatus().getCode()).isEqualTo(ErrorStatus.STORE_NOT_FOUND);
	}

	@Test
	@DisplayName("가게 승인 실패 - 이미 같은 상태")
	void approveStore_sameStatus_shouldThrowException() {
		// given
		UUID storeId = UUID.randomUUID();
		Store store = Store.builder()
			.storeId(storeId)
			.storeName("맛있는 족발집")
			.storeAcceptStatus(StoreAcceptStatus.APPROVE)
			.build();

		when(storeRepository.findByStoreIdAndDeletedAtIsNull(storeId)).thenReturn(Optional.of(store));

		// when
		GeneralException ex = catchThrowableOfType(
			() -> managerService.approveStore(storeId, StoreAcceptStatus.APPROVE),
			GeneralException.class
		);

		// then
		assertThat(ex.getErrorReasonHttpStatus().getCode()).isEqualTo(ManagerErrorStatus.INVALID_STORE_STATUS);
	}
}