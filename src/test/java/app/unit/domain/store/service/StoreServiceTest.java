// package app.unit.domain.store.service;
//
// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.ArgumentMatchers.*;
// import static org.mockito.Mockito.*;
//
// import java.util.Optional;
// import java.util.UUID;
//
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Nested;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.springframework.context.annotation.Import;
//
// import app.domain.menu.model.entity.Category;
// import app.domain.menu.model.repository.CategoryRepository;
// import app.domain.store.StoreService;
// import app.domain.store.model.dto.request.StoreApproveRequest;
// import app.domain.store.model.dto.request.StoreInfoUpdateRequest;
// import app.domain.store.model.dto.response.StoreApproveResponse;
// import app.domain.store.model.dto.response.StoreInfoUpdateResponse;
// import app.domain.store.model.entity.Region;
// import app.domain.store.model.entity.Store;
// import app.domain.store.repository.RegionRepository;
// import app.domain.store.repository.StoreRepository;
// import app.domain.store.status.StoreAcceptStatus;
// import app.domain.store.status.StoreErrorCode;
// import app.domain.user.model.UserRepository;
// import app.domain.user.model.entity.User;
// import app.global.apiPayload.exception.GeneralException;
// import app.global.config.MockSecurityConfig;
// import app.global.config.SecurityConfig;
//
// @Import({SecurityConfig.class, MockSecurityConfig.class})
// @ExtendWith(MockitoExtension.class)
// class StoreServiceTest {
//
// 	@InjectMocks
// 	private StoreService storeService;
//
// 	@Mock
// 	private StoreRepository storeRepository;
//
// 	@Mock
// 	private RegionRepository regionRepository;
//
// 	@Mock
// 	private UserRepository userRepository;
//
// 	@Mock
// 	private CategoryRepository categoryRepository;
//
// 	@Nested
// 	@DisplayName("createStore Test")
// 	class CreateStoreTest {
// 		Long authenticatedUserId = 1L;
// 		UUID regionId = UUID.randomUUID();
// 		UUID categoryId = UUID.randomUUID();
//
// 		@Test
// 		@DisplayName("Success")
// 		void createStoreSuccess() {
// 			StoreApproveRequest request = StoreApproveRequest.builder()
// 				.regionId(regionId)
// 				.categoryId(categoryId)
// 				.address("가게주소")
// 				.storeName("가게이름")
// 				.desc("가게설명")
// 				.phoneNumber("010-1111-2222")
// 				.minOrderAmount(10000L)
// 				.build();
//
// 			Region mockRegion = Region.builder().regionId(regionId).regionName("서울").build();
// 			Store mockStore = Store.builder()
// 				.storeId(UUID.randomUUID())
// 				.storeAcceptStatus(StoreAcceptStatus.PENDING)
// 				.build();
//
// 			when(regionRepository.findById(regionId)).thenReturn(Optional.of(mockRegion));
// 			when(userRepository.findById(authenticatedUserId)).thenReturn(Optional.of(mock(User.class)));
// 			when(storeRepository.save(any(Store.class))).thenReturn(mockStore);
// 			when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(mock(Category.class)));
//
// 			StoreApproveResponse response = storeService.createStore(authenticatedUserId, request);
//
// 			assertNotNull(response);
// 			assertEquals(StoreAcceptStatus.PENDING.name(), response.getStoreApprovalStatus());
//
// 			verify(regionRepository, times(1)).findById(regionId);
// 			verify(categoryRepository, times(1)).findById(categoryId);
// 			verify(userRepository, times(1)).findById(authenticatedUserId);
// 			verify(storeRepository, times(1)).save(any(Store.class));
// 		}
//
// 		@Test
// 		@DisplayName("Success : 선택적 필드가 null인 경우")
// 		void createStoreSuccessOptionalFieldsNull() {
// 			StoreApproveRequest request = StoreApproveRequest.builder()
// 				.regionId(regionId)
// 				.categoryId(categoryId)
// 				.address("가게주소")
// 				.storeName("가게이름")
// 				.desc(null)
// 				.phoneNumber(null)
// 				.minOrderAmount(10000L)
// 				.build();
//
// 			Region mockRegion = Region.builder().regionId(regionId).regionName("서울").build();
// 			Store mockStore = Store.builder()
// 				.storeId(UUID.randomUUID())
// 				.storeAcceptStatus(StoreAcceptStatus.PENDING)
// 				.build();
//
// 			when(userRepository.findById(authenticatedUserId)).thenReturn(Optional.of(mock(User.class)));
// 			when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(mock(Category.class)));
// 			when(regionRepository.findById(regionId)).thenReturn(Optional.of(mockRegion));
// 			when(storeRepository.save(any(Store.class))).thenReturn(mockStore);
//
// 			StoreApproveResponse response = storeService.createStore(authenticatedUserId, request);
//
// 			assertNotNull(response);
// 			assertEquals(StoreAcceptStatus.PENDING.name(), response.getStoreApprovalStatus());
//
// 			verify(userRepository, times(1)).findById(authenticatedUserId);
// 			verify(categoryRepository, times(1)).findById(categoryId);
// 			verify(regionRepository, times(1)).findById(regionId);
// 			verify(storeRepository, times(1)).save(any(Store.class));
// 		}
//
// 		@Test
// 		@DisplayName("Fail : 지역이 존재하지 않음")
// 		void createStoreFailRegionNotFound() {
// 			StoreApproveRequest request = StoreApproveRequest.builder()
// 				.regionId(regionId)
// 				.categoryId(categoryId)
// 				.address("가게주소")
// 				.storeName("가게이름")
// 				.desc("가게설명")
// 				.phoneNumber("010-1111-2222")
// 				.minOrderAmount(10000L)
// 				.build();
//
// 			when(regionRepository.findById(regionId)).thenReturn(Optional.empty());
//
// 			GeneralException exception = assertThrows(GeneralException.class, () -> {
// 				storeService.createStore(authenticatedUserId, request);
// 			});
// 			assertEquals(StoreErrorCode.REGION_NOT_FOUND, exception.getCode());
//
// 			verify(regionRepository, times(1)).findById(regionId);
// 			verify(storeRepository, never()).save(any(Store.class));
// 		}
// 	}
//
// 	@Nested
// 	@DisplayName("updateStoreInfo api 테스트")
// 	class UpdateStoreInfoTest {
// 		UUID storeId = UUID.randomUUID();
// 		UUID categoryId = UUID.randomUUID();
//
// 		Category mockCategory = mock(Category.class);
//
// 		@Test
// 		@DisplayName("Success : Full Request")
// 		void updateStoreInfoSuccess() {
// 			StoreInfoUpdateRequest request = StoreInfoUpdateRequest.builder()
// 				.storeId(storeId)
// 				.categoryId(categoryId)
// 				.name("새 가게 이름")
// 				.address("새 주소")
// 				.phoneNumber("010-2222-3333")
// 				.minOrderAmount(5000L)
// 				.desc("새 설명")
// 				.build();
//
// 			Store mockStore = Store.builder()
// 				.storeId(storeId)
// 				.storeName("기존 가게명")
// 				.address("기존 주소")
// 				.phoneNumber("010-1111-1111")
// 				.minOrderAmount(1000L)
// 				.description("기존 설명")
// 				.build();
//
// 			when(storeRepository.findById(storeId)).thenReturn(Optional.of(mockStore));
// 			when(storeRepository.save(any(Store.class))).thenReturn(mockStore);
// 			when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(mockCategory));
//
// 			StoreInfoUpdateResponse response = storeService.updateStoreInfo(request);
//
// 			assertNotNull(response);
// 			assertEquals(storeId, response.getStoreId());
//
// 			verify(storeRepository, times(1)).findById(storeId);
// 			verify(categoryRepository, times(1)).findById(categoryId);
// 			verify(storeRepository, times(1)).save(any(Store.class));
// 		}
//
// 		@Test
// 		@DisplayName("Success 선택적 필드 Null")
// 		void updateStoreInfoSuccessOptionalNull() {
// 			UUID storeId = UUID.randomUUID();
// 			StoreInfoUpdateRequest request = StoreInfoUpdateRequest.builder()
// 				.storeId(storeId)
// 				.categoryId(categoryId)
// 				.name(null)
// 				.address(null)
// 				.phoneNumber(null)
// 				.minOrderAmount(null)
// 				.desc(null)
// 				.build();
//
// 			Store mockStore = Store.builder()
// 				.storeId(storeId)
// 				.storeName("기존 가게")
// 				.address("기존 주소")
// 				.phoneNumber("010-0000-0000")
// 				.minOrderAmount(1000L)
// 				.description("기존 설명")
// 				.build();
//
// 			when(storeRepository.findById(storeId)).thenReturn(Optional.of(mockStore));
// 			when(storeRepository.save(any(Store.class))).thenReturn(mockStore);
// 			when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(mockCategory));
//
// 			StoreInfoUpdateResponse response = storeService.updateStoreInfo(request);
//
// 			assertNotNull(response);
// 			assertEquals(storeId, response.getStoreId());
// 			verify(storeRepository, times(1)).findById(storeId);
// 			verify(categoryRepository, times(1)).findById(categoryId);
// 			verify(storeRepository, times(1)).save(any(Store.class));
//
// 			assertEquals("기존 가게", mockStore.getStoreName());
// 			assertEquals("기존 주소", mockStore.getAddress());
// 			assertEquals("010-0000-0000", mockStore.getPhoneNumber());
// 			assertEquals(1000L, mockStore.getMinOrderAmount());
// 			assertEquals("기존 설명", mockStore.getDescription());
// 		}
//
// 		@Nested
// 		@DisplayName("deleteStore Test")
// 		class DeleteStoreTest {
//
// 			@Test
// 			@DisplayName("Success")
// 			void deleteStoreSuccess() {
// 				UUID storeId = UUID.randomUUID();
// 				Store mockStore = mock(Store.class);
//
// 				when(storeRepository.findById(storeId)).thenReturn(Optional.of(mockStore));
//
// 				assertDoesNotThrow(() -> storeService.deleteStore(storeId));
//
// 				verify(storeRepository, times(1)).findById(storeId);
// 				verify(mockStore, times(1)).markAsDeleted();
// 			}
//
// 			@Test
// 			@DisplayName("Fail : 이미 삭제된 가게")
// 			void deleteStoreAlready() {
// 				UUID storeId = UUID.randomUUID();
// 				Store mockStore = mock(Store.class);
//
// 				when(storeRepository.findById(storeId)).thenReturn(Optional.of(mockStore));
// 				doThrow(new GeneralException(StoreErrorCode.STORE_NOT_FOUND)).when(mockStore).markAsDeleted();
//
// 				GeneralException exception = assertThrows(GeneralException.class, () -> {
// 					storeService.deleteStore(storeId);
// 				});
//
// 				assertEquals(StoreErrorCode.STORE_NOT_FOUND, exception.getCode());
// 			}
// 		}
// 	}
// }
