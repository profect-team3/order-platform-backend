package app.unit.domain.customer.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import app.domain.customer.CustomerAddressService;
import app.domain.customer.dto.response.GetCustomerAddressListResponse;
import app.domain.user.model.UserAddressRepository;
import app.domain.customer.dto.request.AddCustomerAddressRequest;
import app.domain.customer.dto.response.AddCustomerAddressResponse;
import app.domain.user.model.entity.User;
import app.domain.user.model.entity.UserAddress;
import app.domain.user.model.entity.enums.UserRole;
import app.domain.customer.status.CustomerErrorStatus;
import app.global.apiPayload.exception.GeneralException;
import app.global.SecurityUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;

import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class CustomerAddressServiceTest {

	@InjectMocks
	private CustomerAddressService customerAddressService;

	@Mock
	private UserAddressRepository userAddressRepository;

	@Mock
	private SecurityUtil securityUtil;

	private User testUser;

	@BeforeEach
	void setUp() {
		testUser = User.builder()
			.userId(1L)
			.username("tester")
			.email("testuser@example.com")
			.password("password123")
			.nickname("test_nickname")
			.realName("tester_name")
			.phoneNumber("01012345678")
			.userRole(UserRole.CUSTOMER)
			.build();

		when(securityUtil.getCurrentUser()).thenReturn(testUser);
	}

	private List<UserAddress> createMockAddressList(User user) {
		UserAddress address1 = UserAddress.builder()
			.addressId(UUID.fromString("11111111-1111-1111-1111-111111111111"))
			.user(user)
			.alias("우리집")
			.address("서울시 강남구")
			.addressDetail("101호")
			.isDefault(true)
			.build();
		UserAddress address2 = UserAddress.builder()
			.addressId(UUID.fromString("22222222-2222-2222-2222-222222222222"))
			.user(user)
			.alias("회사")
			.address("서울시 서초구")
			.addressDetail("202호")
			.isDefault(false)
			.build();
		return List.of(address1, address2);
	}

	@Nested
	@DisplayName("주소 추가 성공 케이스")
	class addAddressSuccessCases {

		@Test
		@DisplayName("1. isDefault = true & 기존 존재 없음")
		void addUserAddress_isDefault_true_NormalCase() {
			AddCustomerAddressRequest request = new AddCustomerAddressRequest(

				"우리집",
				"서울시 강남구 테헤란로 212",
				"1501호",
				true
			);

			when(userAddressRepository.save(any(UserAddress.class)))
				.thenAnswer(invocation -> {
					UserAddress useraddress = invocation.getArgument(0);
					if (useraddress.getAddressId() == null) {
						return UserAddress.builder()
							.addressId(UUID.randomUUID())
							.user(useraddress.getUser())
							.alias(useraddress.getAlias())
							.address(useraddress.getAddress())
							.addressDetail(useraddress.getAddressDetail())
							.isDefault(useraddress.isDefault())
							.build();
					}
					return useraddress;
				});

			AddCustomerAddressResponse response = customerAddressService.addCustomerAddress(request);

			assertNotNull(response);
			assertNotNull(response.getAddress_id());

			ArgumentCaptor<UserAddress> captor = ArgumentCaptor.forClass(UserAddress.class);
			verify(userAddressRepository, times(1)).save(captor.capture());
			UserAddress savedArg = captor.getValue();

			assertThat(savedArg.getUser().getUserId()).isEqualTo(testUser.getUserId());
			assertThat(savedArg.getAlias()).isEqualTo("우리집");
			assertThat(savedArg.getAddress()).isEqualTo("서울시 강남구 테헤란로 212");
			assertThat(savedArg.getAddressDetail()).isEqualTo("1501호");
			assertThat(savedArg.isDefault()).isTrue();
		}

		@Test
		@DisplayName("2. isDefault = false & 기존 존재 없음")
		void addUserAddress_isDefault_false_NormalCase() {
			AddCustomerAddressRequest request = new AddCustomerAddressRequest(

				"우리집",
				"서울시 강남구 테헤란로 212",
				"1501호",
				false
			);

			when(userAddressRepository.save(any(UserAddress.class)))
				.thenAnswer(invocation -> {
					UserAddress useraddress = invocation.getArgument(0);
					if (useraddress.getAddressId() == null) {
						return UserAddress.builder()
							.addressId(UUID.randomUUID())
							.user(useraddress.getUser())
							.alias(useraddress.getAlias())
							.address(useraddress.getAddress())
							.addressDetail(useraddress.getAddressDetail())
							.isDefault(useraddress.isDefault())
							.build();
					}
					return useraddress;
				});

			AddCustomerAddressResponse response = customerAddressService.addCustomerAddress(request);

			assertNotNull(response);
			assertNotNull(response.getAddress_id());

			ArgumentCaptor<UserAddress> captor = ArgumentCaptor.forClass(UserAddress.class);
			verify(userAddressRepository, times(1)).save(captor.capture());
			UserAddress savedArg = captor.getValue();

			assertThat(savedArg.getUser().getUserId()).isEqualTo(testUser.getUserId());
			assertThat(savedArg.getAlias()).isEqualTo("우리집");
			assertThat(savedArg.getAddress()).isEqualTo("서울시 강남구 테헤란로 212");
			assertThat(savedArg.getAddressDetail()).isEqualTo("1501호");
			assertThat(savedArg.isDefault()).isTrue();
		}

		@Test
		@DisplayName("3. isDefault=true & 기존 기본 존재 → 기존 기본 해제 후 신규 기본 설정")
		void addUserAddress_SetNewDefault_DemotesPreviousDefault() {
			AddCustomerAddressRequest request = new AddCustomerAddressRequest(
				"새로운 우리집",
				"서울시 서초구",
				"202호",
				true
			);

			UserAddress previousDefaultAddress = UserAddress.builder()
				.addressId(UUID.randomUUID())
				.user(testUser)
				.alias("옛날 집")
				.address("서울시 강남구")
				.addressDetail("101동 101호")
				.isDefault(true)
				.build();

			when(userAddressRepository.findByUser_UserIdAndIsDefaultTrue(testUser.getUserId()))
				.thenReturn(Optional.of(previousDefaultAddress));

			when(userAddressRepository.save(any(UserAddress.class))).thenAnswer(invocation -> {
				UserAddress savedAddress = invocation.getArgument(0);
				if (savedAddress.getAddressId() == null) {
					return UserAddress.builder()
						.addressId(UUID.randomUUID())
						.user(savedAddress.getUser())
						.alias(savedAddress.getAlias())
						.address(savedAddress.getAddress())
						.addressDetail(savedAddress.getAddressDetail())
						.isDefault(savedAddress.isDefault())
						.build();
				}
				return savedAddress;
			});

			customerAddressService.addCustomerAddress(request);

			ArgumentCaptor<UserAddress> addressCaptor = ArgumentCaptor.forClass(UserAddress.class);
			verify(userAddressRepository, times(2)).save(addressCaptor.capture());

			List<UserAddress> savedAddresses = addressCaptor.getAllValues();
			UserAddress demotedAddress = savedAddresses.get(0);
			assertThat(demotedAddress.getAddressId()).isEqualTo(previousDefaultAddress.getAddressId());
			assertThat(demotedAddress.isDefault()).isFalse();

			UserAddress newDefaultAddress = savedAddresses.get(1);
			assertThat(newDefaultAddress.getUser().getUserId()).isEqualTo(testUser.getUserId());
			assertThat(newDefaultAddress.getAlias()).isEqualTo("새로운 우리집");
			assertThat(newDefaultAddress.getAddress()).isEqualTo("서울시 서초구");
			assertThat(newDefaultAddress.getAddressDetail()).isEqualTo("202호");
			assertThat(newDefaultAddress.isDefault()).isTrue();
		}

		@Test
		@DisplayName("4. isDefault=false & 기존 기본 존재 → 기존 기본 유지, 신규 false 저장")
		void addUserAddress_AddNonDefault_KeepsPreviousDefault() {
			AddCustomerAddressRequest request = new AddCustomerAddressRequest(
				"회사",
				"서울시 서초구",
				"202호",
				false
			);

			when(userAddressRepository.findAllByUserUserId(testUser.getUserId()))
				.thenReturn(List.of(UserAddress.builder().build()));

			when(userAddressRepository.save(any(UserAddress.class))).thenAnswer(invocation -> {
				UserAddress savedAddress = invocation.getArgument(0);
				return UserAddress.builder()
					.addressId(UUID.randomUUID())
					.user(savedAddress.getUser())
					.alias(savedAddress.getAlias())
					.address(savedAddress.getAddress())
					.addressDetail(savedAddress.getAddressDetail())
					.isDefault(savedAddress.isDefault())
					.build();
			});

			customerAddressService.addCustomerAddress(request);

			ArgumentCaptor<UserAddress> addressCaptor = ArgumentCaptor.forClass(UserAddress.class);
			verify(userAddressRepository, times(1)).save(addressCaptor.capture());

			UserAddress savedAddress = addressCaptor.getValue();
			assertThat(savedAddress.getUser().getUserId()).isEqualTo(testUser.getUserId());
			assertThat(savedAddress.getAlias()).isEqualTo("회사");
			assertThat(savedAddress.getAddress()).isEqualTo("서울시 서초구");
			assertThat(savedAddress.getAddressDetail()).isEqualTo("202호");
			assertThat(savedAddress.isDefault()).isFalse();

			verify(userAddressRepository, never()).findByUser_UserIdAndIsDefaultTrue(anyLong());
		}
	}

	@Nested
	@DisplayName("주소 추가 실패 케이스")
	class addAddressFailureCases {
		@Test
		@DisplayName("동일한 주소(주소+상세주소)를 중복 등록 시 예외 발생")
		void addUserAddress_Fail_WhenAddressIsDuplicate() {
			AddCustomerAddressRequest request = new AddCustomerAddressRequest(

				"우리집",
				"서울시 강남구 테헤란로 212",
				"1501호",
				false
			);

			when(userAddressRepository.existsByUserAndAddressAndAddressDetail(
				testUser,
				request.getAddress(),
				request.getAddressDetail()
			)).thenReturn(true);

			GeneralException ex = assertThrows(GeneralException.class,
				() -> customerAddressService.addCustomerAddress(request)
			);

			assertThat(ex.getCode()).isEqualTo(CustomerErrorStatus.ADDRESS_ALREADY_EXISTS);

			verify(userAddressRepository, never()).countByUser(any());
			verify(userAddressRepository, never()).save(any(UserAddress.class));
		}
	}

	@Nested
	@DisplayName("주소 추가 예외 케이스")
	class addAddressExceptionCases {

		@Test
		@DisplayName("1. DB 저장 실패 시 _INTERNAL_SERVER_ERROR 예외 발생")
		void addUserAddress_Fail_DbSaveError() {
			AddCustomerAddressRequest request = new AddCustomerAddressRequest(
				"우리집",
				"서울시",
				"101동",
				true
			);

			when(userAddressRepository.save(any(UserAddress.class)))
				.thenThrow(new DataAccessResourceFailureException("Simulated DB error"));

			GeneralException ex = assertThrows(
				GeneralException.class,
				() -> customerAddressService.addCustomerAddress(request)
			);

			assertThat(ex.getCode()).isEqualTo(CustomerErrorStatus.ADDRESS_ADD_FAILED);

			verify(userAddressRepository, times(1)).save(any(UserAddress.class));
		}

		@Test
		@DisplayName("2. UUID 생성 실패 시 _INTERNAL_SERVER_ERROR 예외 발생")
		void addUserAddress_Fail_UuidNotAssigned() {

			when(userAddressRepository.save(any(UserAddress.class)))
				.thenAnswer(inv -> {
					UserAddress ua = inv.getArgument(0);
					return UserAddress.builder()
						.addressId(null)
						.user(ua.getUser()).alias(ua.getAlias())
						.address(ua.getAddress()).addressDetail(ua.getAddressDetail())
						.isDefault(ua.isDefault())
						.build();
				});

			AddCustomerAddressRequest request = new AddCustomerAddressRequest(

				"우리집",
				"서울시",
				"101동",
				true
			);

			GeneralException ex = assertThrows(
				GeneralException.class,
				() -> customerAddressService.addCustomerAddress(request)
			);
			assertThat(ex.getCode()).isEqualTo(CustomerErrorStatus.ADDRESS_ADD_FAILED);
			verify(userAddressRepository, times(1)).save(any(UserAddress.class));
		}
	}

	//	  --- getCustomerAddresses 테스트 시작 ---

	@Nested
	@DisplayName("주소 목록 조회 성공 케이스")
	class getAddressSuccessCases {

		@Test
		@DisplayName("1. 사용자의 주소 목록 1건 정상적으로 조회")
		void getCustomerAddresses_Success_ReturnsSingleDto() {
			UserAddress singleAddress = UserAddress.builder()
				.addressId(UUID.fromString("11111111-1111-1111-1111-111111111111"))
				.user(testUser)
				.alias("우리집")
				.address("서울시 강남구")
				.addressDetail("101호")
				.isDefault(true)
				.build();
			List<UserAddress> mockAddressList = List.of(singleAddress);

			when(userAddressRepository.findAllByUserUserId(testUser.getUserId())).thenReturn(mockAddressList);

			List<GetCustomerAddressListResponse> result = customerAddressService.getCustomerAddresses();

			assertThat(result).isNotNull();
			assertThat(result.size()).isEqualTo(1);

			GetCustomerAddressListResponse result1 = result.get(0);
			assertThat(result1.alias()).isEqualTo(singleAddress.getAlias());
			assertThat(result1.address()).isEqualTo(singleAddress.getAddress());
			assertThat(result1.addressDetail()).isEqualTo(singleAddress.getAddressDetail());
			assertThat(result1.isDefault()).isEqualTo(singleAddress.isDefault());

			verify(userAddressRepository, times(1)).findAllByUserUserId(testUser.getUserId());
		}

		@Test
		@DisplayName("2. 사용자의 주소 목록 여러개 정상적으로 조회")
		void getCustomerAddresses_Success_ReturnsDtoList() {
			List<UserAddress> mockAddressList = createMockAddressList(testUser);
			UserAddress address1 = mockAddressList.get(0);
			UserAddress address2 = mockAddressList.get(1);

			when(userAddressRepository.findAllByUserUserId(testUser.getUserId())).thenReturn(mockAddressList);

			List<GetCustomerAddressListResponse> result = customerAddressService.getCustomerAddresses();

			assertThat(result).isNotNull();
			assertThat(result.size()).isEqualTo(2);

			GetCustomerAddressListResponse result1 = result.get(0);
			assertThat(result1.alias()).isEqualTo(address1.getAlias());
			assertThat(result1.address()).isEqualTo(address1.getAddress());
			assertThat(result1.addressDetail()).isEqualTo(address1.getAddressDetail());
			assertThat(result1.isDefault()).isEqualTo(address1.isDefault());

			GetCustomerAddressListResponse result2 = result.get(1);
			assertThat(result2.alias()).isEqualTo(address2.getAlias());
			assertThat(result2.address()).isEqualTo(address2.getAddress());
			assertThat(result2.addressDetail()).isEqualTo(address2.getAddressDetail());
			assertThat(result2.isDefault()).isEqualTo(address2.isDefault());

			verify(userAddressRepository, times(1)).findAllByUserUserId(testUser.getUserId());
		}

		@Test
		@DisplayName("3. 주소가 없는 사용자의 경우 빈 리스트를 반환함")
		void getCustomerAddresses_Success_WhenNoAddressesExist_ReturnsEmptyList() {
			when(userAddressRepository.findAllByUserUserId(testUser.getUserId())).thenReturn(List.of());

			List<GetCustomerAddressListResponse> result = customerAddressService.getCustomerAddresses();

			assertThat(result).isNotNull();
			assertThat(result).isEmpty();

			verify(userAddressRepository, times(1)).findAllByUserUserId(testUser.getUserId());
		}
	}

	@Nested
	@DisplayName("주소 목록 조회 예외 케이스")
	class getAddressExceptionCases {

		@Test
		@DisplayName("1. DB 조회 중 오류 발생 시 _INTERNAL_SERVER_ERROR 예외 발생")
		void getCustomerAddresses_Exception_DbError() {
			when(userAddressRepository.findAllByUserUserId(testUser.getUserId()))
				.thenThrow(new DataAccessResourceFailureException("Simulated DB Connection Failure"));

			GeneralException ex = assertThrows(GeneralException.class,
				() -> customerAddressService.getCustomerAddresses());

			assertThat(ex.getCode()).isEqualTo(CustomerErrorStatus.ADDRESS_READ_FAILED);
		}
	}
}