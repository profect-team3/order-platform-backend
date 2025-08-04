// package app.unit.domain.store.controller;
//
// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.Mockito.*;
//
// import java.util.List;
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
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.context.SecurityContextHolder;
//
// import app.domain.store.StoreController;
// import app.domain.store.StoreService;
// import app.domain.store.model.dto.request.StoreApproveRequest;
// import app.domain.store.model.dto.request.StoreInfoUpdateRequest;
// import app.domain.store.model.dto.response.StoreApproveResponse;
// import app.domain.store.model.dto.response.StoreInfoUpdateResponse;
// import app.domain.store.model.entity.Region;
// import app.domain.store.repository.RegionRepository;
// import app.domain.store.repository.StoreRepository;
// import app.domain.store.status.StoreErrorCode;
// import app.global.apiPayload.exception.GeneralException;
//
//
// @ExtendWith(MockitoExtension.class)
// public class StoreControllerTest {
//
// 	@InjectMocks
// 	private StoreController storeController;
//
// 	@Mock
// 	private StoreService storeService;
//
// 	@Mock
// 	private StoreRepository storeRepository;
//
// 	@Mock
// 	private RegionRepository regionRepository;
//
// 	UUID categoryId = UUID.randomUUID();
//
// 	@Nested
// 	@DisplayName("Create Store Test")
// 	class CreateStoreTest {
//
// 		@Test
// 		@DisplayName("Success : 가게 등록 요청 성공")
// 		void CreateStoreSuccess() {
//
// 			Long fakeUserId = 1L;
// 			UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
// 				fakeUserId.toString(),
// 				null,
// 				List.of()
// 			);
//
// 			SecurityContextHolder.getContext().setAuthentication(auth);
//
// 			UUID regionId = UUID.randomUUID();
// 			Region mockRegion = mock(Region.class);
//
// 			StoreApproveRequest request = StoreApproveRequest.builder()
// 				.regionId(regionId)
// 				.categoryId(categoryId)
// 				.address("주소")
// 				.storeName("가게 명")
// 				.desc("설명")
// 				.phoneNumber("01012345678")
// 				.minOrderAmount(1000L)
// 				.build();
//
// 			StoreApproveResponse expectedResponse = StoreApproveResponse.builder()
// 				.storeId(UUID.randomUUID())
// 				.storeApprovalStatus("PENDING")
// 				.build();
//
// 			when(regionRepository.findById(regionId)).thenReturn(Optional.of(mockRegion));
// 			when(storeRepository.existsByStoreNameAndRegion(anyString(), any())).thenReturn(false);
// 			when(storeService.createStore(eq(fakeUserId), eq(request))).thenReturn(expectedResponse);
//
// 			ResponseEntity<StoreApproveResponse> result = storeController.createStore(request);
//
// 			assertEquals(HttpStatus.OK, result.getStatusCode());
// 			assertEquals(expectedResponse, result.getBody());
//
// 			verify(regionRepository, times(1)).findById(regionId);
// 			verify(storeRepository, times(1)).existsByStoreNameAndRegion(request.getStoreName(), mockRegion);
// 			verify(storeService, times(1)).createStore(anyLong(), eq(request));
//
// 			SecurityContextHolder.clearContext();
// 		}
//
// 		@Test
// 		@DisplayName("Fail : 유효하지 않은 지역 ID 가게 등록 요청")
// 		void CreateStoreInvalidRegionId() {
// 			StoreApproveRequest request = StoreApproveRequest.builder()
// 				.regionId(null)
// 				.categoryId(categoryId)
// 				.address("주소")
// 				.storeName("가게 명")
// 				.desc("설명")
// 				.phoneNumber("01012345678")
// 				.minOrderAmount(10000L)
// 				.build();
//
// 			GeneralException ex = assertThrows(GeneralException.class, () -> {
// 				storeController.createStore(request);
// 			});
// 			assertEquals(StoreErrorCode.REGION_ID_NULL, ex.getCode());
// 		}
//
// 		@Test
// 		@DisplayName("Fail : 존재하지 않는 지역")
// 		void CreateStoreRegionNotFound() {
// 			UUID regionId = UUID.randomUUID();
// 			StoreApproveRequest request = StoreApproveRequest.builder()
// 				.regionId(regionId)
// 				.categoryId(categoryId)
// 				.address("주소")
// 				.storeName("가게 명")
// 				.desc("설명")
// 				.phoneNumber("01012345678")
// 				.minOrderAmount(10000L)
// 				.build();
//
// 			when(regionRepository.findById(regionId)).thenReturn(Optional.empty());
//
// 			GeneralException ex = assertThrows(GeneralException.class, () -> {
// 				storeController.createStore(request);
// 			});
// 			assertEquals(StoreErrorCode.REGION_NOT_FOUND, ex.getCode());
// 		}
//
// 		@Test
// 		@DisplayName("Fail : 주소 없음")
// 		void CreateStoreAddressNotFound() {
// 			UUID regionId = UUID.randomUUID();
// 			Region mockRegion = mock(Region.class);
// 			StoreApproveRequest request = StoreApproveRequest.builder()
// 				.regionId(regionId)
// 				.categoryId(categoryId)
// 				.address(null)
// 				.storeName("가게 명")
// 				.desc("설명")
// 				.phoneNumber("01012345678")
// 				.minOrderAmount(10000L)
// 				.build();
//
// 			when(regionRepository.findById(regionId)).thenReturn(Optional.of(mockRegion));
//
// 			GeneralException ex = assertThrows(GeneralException.class, () -> storeController.createStore(request));
// 			assertEquals(StoreErrorCode.ADDRESS_NULL, ex.getCode());
// 		}
//
// 		@Test
// 		@DisplayName("Fail : 가게 명 없음")
// 		void CreateStoreStoreNameNotFound() {
// 			UUID regionId = UUID.randomUUID();
// 			Region mockRegion = mock(Region.class);
// 			StoreApproveRequest request = StoreApproveRequest.builder()
// 				.regionId(regionId)
// 				.categoryId(categoryId)
// 				.address("주소")
// 				.storeName(null)
// 				.desc("설명")
// 				.phoneNumber("01012345678")
// 				.minOrderAmount(10000L)
// 				.build();
//
// 			when(regionRepository.findById(regionId)).thenReturn(Optional.of(mockRegion));
//
// 			GeneralException ex = assertThrows(GeneralException.class, () -> storeController.createStore(request));
// 			assertEquals(StoreErrorCode.STORE_NAME_NULL, ex.getCode());
// 		}
//
// 		@Test
// 		@DisplayName("Fail : 최소 주문 금액 오류")
// 		void CreateStoreMinOrderAmountError() {
// 			UUID regionId = UUID.randomUUID();
// 			Region mockRegion = mock(Region.class);
// 			StoreApproveRequest request = StoreApproveRequest.builder()
// 				.regionId(regionId)
// 				.categoryId(categoryId)
// 				.address("주소")
// 				.storeName("가게 명")
// 				.desc("설명")
// 				.phoneNumber("01012345678")
// 				.minOrderAmount(-1000L)
// 				.build();
//
// 			when(regionRepository.findById(regionId)).thenReturn(Optional.of(mockRegion));
//
// 			GeneralException ex = assertThrows(GeneralException.class, () -> storeController.createStore(request));
// 			assertEquals(StoreErrorCode.MIN_ORDER_AMOUNT_INVALID, ex.getCode());
// 		}
//
// 		@Test
// 		@DisplayName("Fail : 동일 지역에 동일한 가게 이름 존재")
// 		void CreateStoreDuplicateStoreNameInRegion() {
// 			UUID regionId = UUID.randomUUID();
// 			Region mockRegion = mock(Region.class);
// 			StoreApproveRequest request = StoreApproveRequest.builder()
// 				.regionId(regionId)
// 				.categoryId(categoryId)
// 				.address("주소")
// 				.storeName("가게 명")
// 				.desc("설명")
// 				.phoneNumber("01012345678")
// 				.minOrderAmount(1000L)
// 				.build();
//
// 			when(regionRepository.findById(regionId)).thenReturn(Optional.of(mockRegion));
// 			when(storeRepository.existsByStoreNameAndRegion(request.getStoreName(), mockRegion)).thenReturn(true);
//
// 			GeneralException ex = assertThrows(GeneralException.class, () -> storeController.createStore(request));
// 			assertEquals(StoreErrorCode.DUPLICATE_STORE_NAME_IN_REGION, ex.getCode());
// 		}
//
// 		@Test
// 		@DisplayName("Fail : 카테고리 ID 없음")
// 		void CreateStoreCategoryIdNull() {
// 			UUID regionId = UUID.randomUUID();
// 			Region mockRegion = mock(Region.class);
// 			StoreApproveRequest request = StoreApproveRequest.builder()
// 				.regionId(regionId)
// 				.categoryId(null)
// 				.address("주소")
// 				.storeName("가게 명")
// 				.desc("설명")
// 				.phoneNumber("01012345678")
// 				.minOrderAmount(1000L)
// 				.build();
//
// 			when(regionRepository.findById(regionId)).thenReturn(Optional.of(mockRegion));
//
// 			GeneralException ex = assertThrows(GeneralException.class, () -> storeController.createStore(request));
// 			assertEquals(StoreErrorCode.CATEGORY_ID_NULL, ex.getCode());
// 		}
// 	}
//
// 	@Nested
// 	@DisplayName("Store Info Update Test")
// 	class StoreInfoUpdateTest {
//
// 		@Test
// 		@DisplayName("Success : Full Request")
// 		void StoreInfoUpdateSuccess() {
// 			StoreInfoUpdateRequest request = StoreInfoUpdateRequest.builder()
// 				.storeId(UUID.randomUUID())
// 				.categoryId(categoryId)
// 				.name("가게 명")
// 				.address("주소")
// 				.phoneNumber("01012345678")
// 				.minOrderAmount(1000L)
// 				.desc("설명")
// 				.build();
//
// 			StoreInfoUpdateResponse response = StoreInfoUpdateResponse.builder().storeId(UUID.randomUUID()).build();
//
// 			when(storeService.updateStoreInfo(request)).thenReturn(response);
//
// 			ResponseEntity<StoreInfoUpdateResponse> result = storeController.updateStore(request);
//
// 			assertEquals(HttpStatus.OK, result.getStatusCode());
// 			assertEquals(response, result.getBody());
// 		}
//
// 		@Test
// 		@DisplayName("Success: 선택 Null")
// 		void StoreInfoUpdateOptionalNull() {
// 			StoreInfoUpdateRequest request = StoreInfoUpdateRequest.builder()
// 				.storeId(UUID.randomUUID())
// 				.categoryId(categoryId)
// 				.name(null)
// 				.address(null)
// 				.phoneNumber(null)
// 				.minOrderAmount(null)
// 				.desc(null)
// 				.build();
//
// 			StoreInfoUpdateResponse response = StoreInfoUpdateResponse.builder().storeId(UUID.randomUUID()).build();
//
// 			when(storeService.updateStoreInfo(request)).thenReturn(response);
//
// 			ResponseEntity<StoreInfoUpdateResponse> result = storeController.updateStore(request);
//
// 			assertEquals(HttpStatus.OK, result.getStatusCode());
// 			assertEquals(response, result.getBody());
// 		}
//
// 		@Test
// 		@DisplayName("Fail : 유효하지 않은 storeId")
// 		void StoreInfoUpdateInvalidStoreId() {
// 			StoreInfoUpdateRequest request = StoreInfoUpdateRequest.builder()
// 				.storeId(null)
// 				.categoryId(categoryId)
// 				.name("가게 명")
// 				.address("주소")
// 				.phoneNumber("01012345678")
// 				.minOrderAmount(1000L)
// 				.desc("설명")
// 				.build();
//
// 			GeneralException ex = assertThrows(GeneralException.class, () -> storeController.updateStore(request));
// 			assertEquals(StoreErrorCode.STORE_ID_NULL, ex.getCode());
// 		}
//
// 		@Test
// 		@DisplayName("Fail : 최소 주문 금액 에러")
// 		void StoreInfoUpdateMinOrderAmountError() {
// 			StoreInfoUpdateRequest request = StoreInfoUpdateRequest.builder()
// 				.storeId(UUID.randomUUID())
// 				.categoryId(categoryId)
// 				.name("가게 명")
// 				.address("주소")
// 				.phoneNumber("01012345678")
// 				.minOrderAmount(-1000L)
// 				.desc("설명")
// 				.build();
//
// 			GeneralException ex = assertThrows(GeneralException.class, () -> storeController.updateStore(request));
// 			assertEquals(StoreErrorCode.MIN_ORDER_AMOUNT_INVALID, ex.getCode());
// 		}
//
// 		@Test
// 		@DisplayName("Fail : 카테고리 ID 없음")
// 		void StoreInfoUpdateCategoryIdNull() {
// 			StoreInfoUpdateRequest request = StoreInfoUpdateRequest.builder()
// 				.storeId(UUID.randomUUID())
// 				.categoryId(null)
// 				.name("가게 명")
// 				.address("주소")
// 				.phoneNumber("01012345678")
// 				.minOrderAmount(1000L)
// 				.desc("설명")
// 				.build();
//
// 			GeneralException ex = assertThrows(GeneralException.class, () -> storeController.updateStore(request));
// 			assertEquals(StoreErrorCode.CATEGORY_ID_NULL, ex.getCode());
// 		}
// 	}
//
// 	@Nested
// 	@DisplayName("DeleteStoreTest")
// 	class DeleteStoreTest {
//
// 		@Test
// 		@DisplayName("Success")
// 		void DeleteStoreTestSuccess() {
// 			UUID storeId = UUID.randomUUID();
//
// 			ResponseEntity<String> result = storeController.deleteStore(storeId);
//
// 			assertEquals(HttpStatus.OK, result.getStatusCode());
// 			assertEquals("가게 삭제가 완료되었습니다.", result.getBody());
// 		}
// 	}
// }
