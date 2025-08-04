// package app.unit.domain.menu.controller;
//
// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.ArgumentMatchers.*;
// import static org.mockito.Mockito.*;
//
// import java.util.Arrays;
// import java.util.List;
// import java.util.UUID;
//
// import org.junit.jupiter.api.BeforeEach;
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
// import app.domain.menu.StoreMenuController;
// import app.domain.menu.StoreMenuService;
// import app.domain.menu.model.dto.request.MenuCreateRequest;
// import app.domain.menu.model.dto.request.MenuDeleteRequest;
// import app.domain.menu.model.dto.request.MenuListRequest;
// import app.domain.menu.model.dto.request.MenuUpdateRequest;
// import app.domain.menu.model.dto.request.MenuVisibleRequest;
// import app.domain.menu.model.dto.response.MenuCreateResponse;
// import app.domain.menu.model.dto.response.MenuDeleteResponse;
// import app.domain.menu.model.dto.response.MenuListResponse;
// import app.domain.menu.model.dto.response.MenuUpdateResponse;
//
// import app.global.apiPayload.exception.GeneralException;
//
// @ExtendWith(MockitoExtension.class)
// public class StoreMenuControllerTest {
//
// 	@InjectMocks
// 	private StoreMenuController menuController;
//
// 	@Mock
// 	private StoreMenuService menuService;
//
// 	private final Long TEST_USER_ID = 1L;
// 	private final UUID TEST_STORE_ID = UUID.randomUUID();
// 	private final UUID TEST_MENU_ID = UUID.randomUUID();
//
// 	@BeforeEach
// 	void setUp() {
// 		SecurityContextHolder.getContext().setAuthentication(
// 			new UsernamePasswordAuthenticationToken(TEST_USER_ID.toString(), null, List.of())
// 		);
// 	}
//
// 	@Nested
// 	@DisplayName("메뉴 등록 API 테스트")
// 	class CreateMenuApiTest {
//
// 		@Test
// 		@DisplayName("성공: 메뉴 등록 - 모든 필드 포함")
// 		void createMenuSuccessAllFields() {
// 			MenuCreateRequest request = MenuCreateRequest.builder()
// 				.storeId(TEST_STORE_ID)
// 				.name("테스트 메뉴")
// 				.price(10000L)
// 				.description("맛있는 메뉴")
// 				.build();
// 			MenuCreateResponse expectedResponse = MenuCreateResponse.builder()
// 				.menuId(TEST_MENU_ID)
// 				.name("테스트 메뉴")
// 				.build();
//
// 			when(menuService.createMenu(eq(TEST_USER_ID), any(MenuCreateRequest.class)))
// 				.thenReturn(expectedResponse);
//
// 			ResponseEntity<MenuCreateResponse> responseEntity = menuController.createMenu(request);
//
// 			assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
// 			assertEquals(expectedResponse, responseEntity.getBody());
// 			verify(menuService, times(1)).createMenu(eq(TEST_USER_ID), any(MenuCreateRequest.class));
// 		}
//
// 		@Test
// 		@DisplayName("성공: 메뉴 등록 - description 필드 null")
// 		void createMenuSuccessWithNullDescription() {
// 			MenuCreateRequest request = MenuCreateRequest.builder()
// 				.storeId(TEST_STORE_ID)
// 				.name("테스트 메뉴 (설명 없음)")
// 				.price(10000L)
// 				.description(null)
// 				.build();
// 			MenuCreateResponse expectedResponse = MenuCreateResponse.builder()
// 				.menuId(TEST_MENU_ID)
// 				.name("테스트 메뉴 (설명 없음)")
// 				.build();
//
// 			when(menuService.createMenu(eq(TEST_USER_ID), any(MenuCreateRequest.class)))
// 				.thenReturn(expectedResponse);
//
// 			ResponseEntity<MenuCreateResponse> responseEntity = menuController.createMenu(request);
//
// 			assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
// 			assertEquals(expectedResponse, responseEntity.getBody());
// 			verify(menuService, times(1)).createMenu(eq(TEST_USER_ID), any(MenuCreateRequest.class));
// 		}
//
// 		@Test
// 		@DisplayName("실패: 유효하지 않은 요청 (예: 메뉴 이름 누락)")
// 		void createMenuFailInvalidRequest() {
// 			MenuCreateRequest request = MenuCreateRequest.builder()
// 				.storeId(TEST_STORE_ID)
// 				.price(10000L)
// 				.build();
//
// 			when(menuService.createMenu(eq(TEST_USER_ID), any(MenuCreateRequest.class)))
// 				.thenThrow(new GeneralException(MenuErrorCode.MENU_NAME_NULL));
//
// 			GeneralException exception = assertThrows(GeneralException.class, () -> {
// 				menuController.createMenu(request);
// 			});
//
// 			assertEquals(MenuErrorCode.MENU_NAME_NULL, exception.getCode());
// 		}
// 	}
//
// 	@Nested
// 	@DisplayName("메뉴 수정 API 테스트")
// 	class UpdateMenuApiTest {
//
// 		@Test
// 		@DisplayName("성공: 메뉴 수정 - 모든 필드 포함")
// 		void updateMenuSuccessAllFields() {
// 			MenuUpdateRequest request = MenuUpdateRequest.builder()
// 				.name("수정된 메뉴")
// 				.price(12000L)
// 				.build();
// 			MenuUpdateResponse expectedResponse = MenuUpdateResponse.builder()
// 				.menuId(TEST_MENU_ID)
// 				.name("수정된 메뉴")
// 				.build();
//
// 			when(menuService.updateMenu(eq(TEST_USER_ID), any(MenuUpdateRequest.class)))
// 				.thenReturn(expectedResponse);
//
// 			ResponseEntity<MenuUpdateResponse> responseEntity = menuController.updateMenu(request);
//
// 			assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
// 			assertEquals(expectedResponse, responseEntity.getBody());
// 			verify(menuService, times(1)).updateMenu(eq(TEST_USER_ID), any(MenuUpdateRequest.class));
// 		}
//
// 		@Test
// 		@DisplayName("성공: 메뉴 수정 - 선택적 필드 null")
// 		void updateMenuSuccessWithNullOptionalFields() {
// 			MenuUpdateRequest request = MenuUpdateRequest.builder()
// 				.name(null)
// 				.price(null)
// 				.description(null)
// 				.isHidden(null)
// 				.build();
// 			MenuUpdateResponse expectedResponse = MenuUpdateResponse.builder()
// 				.menuId(TEST_MENU_ID)
// 				.name("원래 메뉴")
// 				.build();
//
// 			when(menuService.updateMenu(eq(TEST_USER_ID), any(MenuUpdateRequest.class)))
// 				.thenReturn(expectedResponse);
//
// 			ResponseEntity<MenuUpdateResponse> responseEntity = menuController.updateMenu(request);
//
// 			assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
// 			assertEquals(expectedResponse, responseEntity.getBody());
// 			verify(menuService, times(1)).updateMenu(eq(TEST_USER_ID), any(MenuUpdateRequest.class));
// 		}
//
// 		@Test
// 		@DisplayName("실패: 메뉴 없음")
// 		void updateMenuFailMenuNotFound() {
// 			MenuUpdateRequest request = MenuUpdateRequest.builder().menuId(TEST_MENU_ID).build();
//
// 			when(menuService.updateMenu(eq(TEST_USER_ID), any(MenuUpdateRequest.class)))
// 				.thenThrow(new GeneralException(MenuErrorCode.MENU_NOT_FOUND));
//
// 			GeneralException exception = assertThrows(GeneralException.class, () -> {
// 				menuController.updateMenu(request);
// 			});
//
// 			assertEquals(MenuErrorCode.MENU_NOT_FOUND, exception.getCode());
// 		}
// 	}
//
// 	@Nested
// 	@DisplayName("메뉴 숨김/노출 API 테스트")
// 	class UpdateMenuVisibilityApiTest {
//
// 		@Test
// 		@DisplayName("성공: 메뉴 숨김/노출")
// 		void updateMenuVisibilitySuccess() {
// 			MenuVisibleRequest request = MenuVisibleRequest.builder().visible(true).build();
// 			MenuUpdateResponse expectedResponse = MenuUpdateResponse.builder()
// 				.menuId(TEST_MENU_ID)
// 				.name("테스트 메뉴")
// 				.build();
//
// 			when(menuService.updateMenuVisibility(eq(TEST_USER_ID), eq(TEST_MENU_ID), eq(true)))
// 				.thenReturn(expectedResponse);
//
// 			ResponseEntity<MenuUpdateResponse> responseEntity = menuController.updateMenuVisibility(TEST_MENU_ID,
// 				request);
//
// 			assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
// 			assertEquals(expectedResponse, responseEntity.getBody());
// 			verify(menuService, times(1)).updateMenuVisibility(eq(TEST_USER_ID), eq(TEST_MENU_ID), eq(true));
// 		}
//
// 		@Test
// 		@DisplayName("실패: 메뉴 없음")
// 		void updateMenuVisibilityFailMenuNotFound() {
// 			MenuVisibleRequest request = MenuVisibleRequest.builder().visible(true).build();
//
// 			when(menuService.updateMenuVisibility(eq(TEST_USER_ID), eq(TEST_MENU_ID), eq(true)))
// 				.thenThrow(new GeneralException(MenuErrorCode.MENU_NOT_FOUND));
//
// 			GeneralException exception = assertThrows(GeneralException.class, () -> {
// 				menuController.updateMenuVisibility(TEST_MENU_ID, request);
// 			});
//
// 			assertEquals(MenuErrorCode.MENU_NOT_FOUND, exception.getCode());
// 		}
// 	}
//
// 	@Nested
// 	@DisplayName("메뉴 삭제 API 테스트")
// 	class DeleteMenuApiTest {
//
// 		@Test
// 		@DisplayName("성공: 메뉴 삭제")
// 		void deleteMenuSuccess() {
// 			MenuDeleteRequest request = MenuDeleteRequest.builder().build();
// 			MenuDeleteResponse expectedResponse = MenuDeleteResponse.builder()
// 				.menuId(TEST_MENU_ID)
// 				.status("DELETED")
// 				.build();
//
// 			when(menuService.deleteMenu(eq(TEST_USER_ID), any(MenuDeleteRequest.class)))
// 				.thenReturn(expectedResponse);
//
// 			ResponseEntity<MenuDeleteResponse> responseEntity = menuController.deleteMenu(request);
//
// 			assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
// 			assertEquals(expectedResponse, responseEntity.getBody());
// 			verify(menuService, times(1)).deleteMenu(eq(TEST_USER_ID), any(MenuDeleteRequest.class));
// 		}
//
// 		@Test
// 		@DisplayName("실패: 이미 삭제된 메뉴")
// 		void deleteMenuFailAlreadyDeleted() {
// 			MenuDeleteRequest request = MenuDeleteRequest.builder().build();
//
// 			when(menuService.deleteMenu(eq(TEST_USER_ID), any(MenuDeleteRequest.class)))
// 				.thenThrow(new GeneralException(MenuErrorCode.MENU_ALREADY_DELETED));
//
// 			GeneralException exception = assertThrows(GeneralException.class, () -> {
// 				menuController.deleteMenu(request);
// 			});
//
// 			assertEquals(MenuErrorCode.MENU_ALREADY_DELETED, exception.getCode());
// 		}
// 	}
//
// 	@Nested
// 	@DisplayName("메뉴 리스트 조회 API 테스트")
// 	class GetMenuListApiTest {
//
// 		@Test
// 		@DisplayName("성공: 메뉴 리스트 조회")
// 		void getMenuListSuccess() {
// 			MenuListResponse expectedResponse = MenuListResponse.builder()
// 				.storeId(TEST_STORE_ID)
// 				.menus(Arrays.asList(
// 					MenuListResponse.MenuDetail.builder().menuId(UUID.randomUUID()).name("메뉴1").build(),
// 					MenuListResponse.MenuDetail.builder().menuId(UUID.randomUUID()).name("메뉴2").build()
// 				))
// 				.build();
//
// 			when(menuService.getMenuList(any(MenuListRequest.class)))
// 				.thenReturn(expectedResponse);
//
// 			ResponseEntity<MenuListResponse> responseEntity = menuController.getMenuList(TEST_STORE_ID.toString());
//
// 			assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
// 			assertEquals(expectedResponse, responseEntity.getBody());
// 			verify(menuService, times(1)).getMenuList(any(MenuListRequest.class));
// 		}
//
// 		@Test
// 		@DisplayName("실패: 가게 없음")
// 		void getMenuListFailStoreNotFound() {
// 			when(menuService.getMenuList(any(MenuListRequest.class)))
// 				.thenThrow(new GeneralException(MenuErrorCode.STORE_NOT_FOUND_FOR_MENU));
//
// 			GeneralException exception = assertThrows(GeneralException.class, () -> {
// 				menuController.getMenuList(TEST_STORE_ID.toString());
// 			});
//
// 			assertEquals(MenuErrorCode.STORE_NOT_FOUND_FOR_MENU, exception.getCode());
// 		}
// 	}
// }
//
