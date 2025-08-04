// package app.unit.domain.menu.service;
//
// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.ArgumentMatchers.*;
// import static org.mockito.Mockito.*;
//
// import java.time.LocalDateTime;
// import java.util.Arrays;
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
//
// import app.domain.menu.MenuService;
// import app.domain.menu.model.dto.request.MenuCreateRequest;
// import app.domain.menu.model.dto.request.MenuDeleteRequest;
// import app.domain.menu.model.dto.request.MenuListRequest;
// import app.domain.menu.model.dto.request.MenuUpdateRequest;
// import app.domain.menu.model.dto.response.MenuCreateResponse;
// import app.domain.menu.model.dto.response.MenuDeleteResponse;
// import app.domain.menu.model.dto.response.MenuListResponse;
// import app.domain.menu.model.dto.response.MenuUpdateResponse;
// import app.domain.menu.model.entity.Menu;
// import app.domain.menu.model.repository.MenuRepository;
// import app.domain.menu.status.MenuErrorCode;
// import app.domain.store.model.entity.Store;
// import app.domain.store.repository.StoreRepository;
// import app.domain.store.status.StoreErrorCode;
// import app.domain.user.model.UserRepository;
// import app.domain.user.model.entity.User;
// import app.global.apiPayload.exception.GeneralException;
//
// @ExtendWith(MockitoExtension.class)
// public class MenuServiceTest {
//
// 	@InjectMocks
// 	private MenuService menuService;
//
// 	@Mock
// 	private MenuRepository menuRepository;
//
// 	@Mock
// 	private StoreRepository storeRepository;
//
// 	@Mock
// 	private UserRepository userRepository;
//
// 	private final Long TEST_USER_ID = 1L;
// 	private final UUID TEST_STORE_ID = UUID.randomUUID();
// 	private final UUID TEST_MENU_ID = UUID.randomUUID();
//
// 	@Nested
// 	@DisplayName("메뉴 등록 테스트")
// 	class CreateMenuTest {
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
//
// 			User mockUser = mock(User.class);
// 			Store mockStore = mock(Store.class);
// 			Menu mockMenu = Menu.builder()
// 				.menuId(TEST_MENU_ID)
// 				.name("테스트 메뉴")
// 				.build();
//
// 			when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(mockUser));
// 			when(storeRepository.findById(TEST_STORE_ID)).thenReturn(Optional.of(mockStore));
// 			when(menuRepository.existsByStoreAndNameAndDeletedAtIsNull(mockStore, "테스트 메뉴")).thenReturn(false);
// 			when(menuRepository.save(any(Menu.class))).thenReturn(mockMenu);
//
// 			MenuCreateResponse response = menuService.createMenu(TEST_USER_ID, request);
//
// 			assertNotNull(response);
// 			assertEquals(TEST_MENU_ID, response.getMenuId());
// 			assertEquals("테스트 메뉴", response.getName());
//
// 			verify(userRepository, times(1)).findById(TEST_USER_ID);
// 			verify(storeRepository, times(1)).findById(TEST_STORE_ID);
// 			verify(menuRepository, times(1)).existsByStoreAndNameAndDeletedAtIsNull(mockStore, "테스트 메뉴");
// 			verify(menuRepository, times(1)).save(any(Menu.class));
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
//
// 			User mockUser = mock(User.class);
// 			Store mockStore = mock(Store.class);
// 			Menu mockMenu = Menu.builder()
// 				.menuId(TEST_MENU_ID)
// 				.name("테스트 메뉴 (설명 없음)")
// 				.build();
//
// 			when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(mockUser));
// 			when(storeRepository.findById(TEST_STORE_ID)).thenReturn(Optional.of(mockStore));
// 			when(menuRepository.existsByStoreAndNameAndDeletedAtIsNull(mockStore, "테스트 메뉴 (설명 없음)")).thenReturn(false);
// 			when(menuRepository.save(any(Menu.class))).thenReturn(mockMenu);
//
// 			MenuCreateResponse response = menuService.createMenu(TEST_USER_ID, request);
//
// 			assertNotNull(response);
// 			assertEquals(TEST_MENU_ID, response.getMenuId());
// 			assertEquals("테스트 메뉴 (설명 없음)", response.getName());
//
// 			verify(userRepository, times(1)).findById(TEST_USER_ID);
// 			verify(storeRepository, times(1)).findById(TEST_STORE_ID);
// 			verify(menuRepository, times(1)).existsByStoreAndNameAndDeletedAtIsNull(mockStore, "테스트 메뉴 (설명 없음)");
// 			verify(menuRepository, times(1)).save(any(Menu.class));
// 		}
//
// 		@Test
// 		@DisplayName("실패: 사용자 없음")
// 		void createMenuFailUserNotFound() {
// 			MenuCreateRequest request = MenuCreateRequest.builder().build();
// 			when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.empty());
//
// 			GeneralException exception = assertThrows(GeneralException.class, () -> {
// 				menuService.createMenu(TEST_USER_ID, request);
// 			});
//
// 			assertEquals(MenuErrorCode.USER_NOT_FOUND_FOR_MENU, exception.getCode());
// 		}
//
// 		@Test
// 		@DisplayName("실패: 가게 없음")
// 		void createMenuFailStoreNotFound() {
// 			MenuCreateRequest request = MenuCreateRequest.builder().storeId(TEST_STORE_ID).build();
// 			User mockUser = mock(User.class);
//
// 			when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(mockUser));
// 			when(storeRepository.findById(TEST_STORE_ID)).thenReturn(Optional.empty());
//
// 			GeneralException exception = assertThrows(GeneralException.class, () -> {
// 				menuService.createMenu(TEST_USER_ID, request);
// 			});
//
// 			assertEquals(MenuErrorCode.STORE_NOT_FOUND_FOR_MENU, exception.getCode());
// 		}
//
// 		@Test
// 		@DisplayName("실패: 메뉴 이름 중복")
// 		void createMenuFailDuplicateName() {
// 			MenuCreateRequest request = MenuCreateRequest.builder()
// 				.storeId(TEST_STORE_ID)
// 				.name("중복 메뉴")
// 				.build();
// 			User mockUser = mock(User.class);
// 			Store mockStore = mock(Store.class);
//
// 			when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(mockUser));
// 			when(storeRepository.findById(TEST_STORE_ID)).thenReturn(Optional.of(mockStore));
// 			when(menuRepository.existsByStoreAndNameAndDeletedAtIsNull(mockStore, "중복 메뉴")).thenReturn(true);
//
// 			GeneralException exception = assertThrows(GeneralException.class, () -> {
// 				menuService.createMenu(TEST_USER_ID, request);
// 			});
//
// 			assertEquals(MenuErrorCode.MENU_NAME_DUPLICATE, exception.getCode());
// 		}
// 	}
//
// 	@Nested
// 	@DisplayName("메뉴 수정 테스트")
// 	class UpdateMenuTest {
//
// 		@Test
// 		@DisplayName("성공: 메뉴 수정 - 모든 필드 포함")
// 		void updateMenuSuccessAllFields() {
// 			MenuUpdateRequest request = MenuUpdateRequest.builder()
// 				.menuId(TEST_MENU_ID)
// 				.name("수정된 메뉴")
// 				.price(12000L)
// 				.description("수정된 설명")
// 				.isHidden(true)
// 				.build();
//
// 			User mockUser = User.builder().userId(TEST_USER_ID).build();
// 			Store mockStore = Store.builder().user(mockUser).build();
// 			Menu mockMenu = Menu.builder()
// 				.menuId(TEST_MENU_ID)
// 				.store(mockStore)
// 				.name("원래 메뉴")
// 				.price(10000L)
// 				.description("원래 설명")
// 				.isHidden(false)
// 				.build();
//
// 			when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(mockUser));
// 			when(menuRepository.findByMenuIdAndDeletedAtIsNull(TEST_MENU_ID)).thenReturn(Optional.of(mockMenu));
// 			when(menuRepository.existsByStoreAndNameAndDeletedAtIsNull(mockStore, "수정된 메뉴")).thenReturn(false);
// 			when(menuRepository.save(any(Menu.class))).thenReturn(mockMenu);
//
// 			MenuUpdateResponse response = menuService.updateMenu(TEST_USER_ID, request);
//
// 			assertNotNull(response);
// 			assertEquals(TEST_MENU_ID, response.getMenuId());
// 			assertEquals("수정된 메뉴", response.getName());
//
// 			verify(userRepository, times(1)).findById(TEST_USER_ID);
// 			verify(menuRepository, times(1)).findByMenuIdAndDeletedAtIsNull(TEST_MENU_ID);
// 			verify(menuRepository, times(1)).existsByStoreAndNameAndDeletedAtIsNull(mockStore, "수정된 메뉴");
// 			verify(menuRepository, times(1)).save(any(Menu.class));
// 		}
//
// 		@Test
// 		@DisplayName("성공: 메뉴 수정 - 선택적 필드 null")
// 		void updateMenuSuccessWithNullOptionalFields() {
// 			MenuUpdateRequest request = MenuUpdateRequest.builder()
// 				.menuId(TEST_MENU_ID)
// 				.name(null)
// 				.price(null)
// 				.description(null)
// 				.isHidden(null)
// 				.build();
//
// 			User mockUser = User.builder().userId(TEST_USER_ID).build();
// 			Store mockStore = Store.builder().user(mockUser).build();
// 			Menu mockMenu = Menu.builder()
// 				.menuId(TEST_MENU_ID)
// 				.store(mockStore)
// 				.name("원래 메뉴")
// 				.price(10000L)
// 				.description("원래 설명")
// 				.isHidden(false)
// 				.build();
//
// 			when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(mockUser));
// 			when(menuRepository.findByMenuIdAndDeletedAtIsNull(TEST_MENU_ID)).thenReturn(Optional.of(mockMenu));
// 			when(menuRepository.save(any(Menu.class))).thenReturn(mockMenu);
//
// 			MenuUpdateResponse response = menuService.updateMenu(TEST_USER_ID, request);
//
// 			assertNotNull(response);
// 			assertEquals(TEST_MENU_ID, response.getMenuId());
// 			assertEquals("원래 메뉴", mockMenu.getName());
// 			assertEquals(10000L, mockMenu.getPrice());
// 			assertEquals("원래 설명", mockMenu.getDescription());
// 			assertEquals(false, mockMenu.isHidden());
//
// 			verify(userRepository, times(1)).findById(TEST_USER_ID);
// 			verify(menuRepository, times(1)).findByMenuIdAndDeletedAtIsNull(TEST_MENU_ID);
// 			verify(menuRepository, never()).existsByStoreAndNameAndDeletedAtIsNull(any(),
// 				any());
// 			verify(menuRepository, times(1)).save(any(Menu.class));
// 		}
//
// 		@Test
// 		@DisplayName("실패: 사용자 없음")
// 		void updateMenuFailUserNotFound() {
// 			MenuUpdateRequest request = MenuUpdateRequest.builder().build();
// 			when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.empty());
//
// 			GeneralException exception = assertThrows(GeneralException.class, () -> {
// 				menuService.updateMenu(TEST_USER_ID, request);
// 			});
//
// 			assertEquals(MenuErrorCode.USER_NOT_FOUND_FOR_MENU, exception.getCode());
// 		}
//
// 		@Test
// 		@DisplayName("실패: 메뉴 없음")
// 		void updateMenuFailMenuNotFound() {
// 			MenuUpdateRequest request = MenuUpdateRequest.builder().menuId(TEST_MENU_ID).build();
// 			User mockUser = mock(User.class);
//
// 			when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(mockUser));
// 			when(menuRepository.findByMenuIdAndDeletedAtIsNull(TEST_MENU_ID)).thenReturn(Optional.empty());
//
// 			GeneralException exception = assertThrows(GeneralException.class, () -> {
// 				menuService.updateMenu(TEST_USER_ID, request);
// 			});
//
// 			assertEquals(MenuErrorCode.MENU_NOT_FOUND, exception.getCode());
// 		}
//
// 		@Test
// 		@DisplayName("실패: 메뉴 소유자 불일치")
// 		void updateMenuFailUnauthorized() {
// 			MenuUpdateRequest request = MenuUpdateRequest.builder().menuId(TEST_MENU_ID).build();
// 			User mockUser = User.builder().userId(TEST_USER_ID).build();
// 			User anotherUser = User.builder().userId(2L).build();
// 			Store mockStore = Store.builder().user(anotherUser).build();
// 			Menu mockMenu = Menu.builder()
// 				.menuId(TEST_MENU_ID)
// 				.store(mockStore)
// 				.build();
//
// 			when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(mockUser));
// 			when(menuRepository.findByMenuIdAndDeletedAtIsNull(TEST_MENU_ID)).thenReturn(Optional.of(mockMenu));
//
// 			GeneralException exception = assertThrows(GeneralException.class, () -> {
// 				menuService.updateMenu(TEST_USER_ID, request);
// 			});
//
// 			assertEquals(MenuErrorCode.MENU_NOT_FOUND,
// 				exception.getCode());
// 		}
//
// 		@Test
// 		@DisplayName("실패: 수정 메뉴 이름 중복")
// 		void updateMenuFailDuplicateName() {
// 			MenuUpdateRequest request = MenuUpdateRequest.builder()
// 				.menuId(TEST_MENU_ID)
// 				.name("중복 메뉴")
// 				.build();
// 			User mockUser = User.builder().userId(TEST_USER_ID).build();
// 			Store mockStore = Store.builder().user(mockUser).build();
// 			Menu mockMenu = Menu.builder()
// 				.menuId(TEST_MENU_ID)
// 				.store(mockStore)
// 				.name("원래 메뉴")
// 				.build();
//
// 			when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(mockUser));
// 			when(menuRepository.findByMenuIdAndDeletedAtIsNull(TEST_MENU_ID)).thenReturn(Optional.of(mockMenu));
// 			when(menuRepository.existsByStoreAndNameAndDeletedAtIsNull(mockStore, "중복 메뉴")).thenReturn(true);
//
// 			GeneralException exception = assertThrows(GeneralException.class, () -> {
// 				menuService.updateMenu(TEST_USER_ID, request);
// 			});
//
// 			assertEquals(MenuErrorCode.MENU_NAME_DUPLICATE, exception.getCode());
// 		}
// 	}
//
// 	@Nested
// 	@DisplayName("메뉴 삭제 테스트")
// 	class DeleteMenuTest {
//
// 		@Test
// 		@DisplayName("성공: 메뉴 삭제")
// 		void deleteMenuSuccess() {
// 			MenuDeleteRequest request = MenuDeleteRequest.builder().menuId(TEST_MENU_ID).build();
//
// 			User mockUser = User.builder().userId(TEST_USER_ID).build();
// 			Store mockStore = Store.builder().user(mockUser).build();
// 			Menu mockMenu = Menu.builder()
// 				.menuId(TEST_MENU_ID)
// 				.store(mockStore)
// 				.build();
// 			mockMenu.setDeletedAt(null);
//
// 			when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(mockUser));
// 			when(menuRepository.findByMenuIdAndDeletedAtIsNull(TEST_MENU_ID)).thenReturn(Optional.of(mockMenu));
// 			when(menuRepository.save(any(Menu.class))).thenReturn(mockMenu);
//
// 			MenuDeleteResponse response = menuService.deleteMenu(TEST_USER_ID, request);
//
// 			assertNotNull(response);
// 			assertEquals(TEST_MENU_ID, response.getMenuId());
// 			assertEquals("DELETED", response.getStatus());
// 			assertNotNull(mockMenu.getDeletedAt());
//
// 			verify(userRepository, times(1)).findById(TEST_USER_ID);
// 			verify(menuRepository, times(1)).findByMenuIdAndDeletedAtIsNull(TEST_MENU_ID);
// 			verify(menuRepository, times(1)).save(any(Menu.class));
// 		}
//
// 		@Test
// 		@DisplayName("실패: 사용자 없음")
// 		void deleteMenuFailUserNotFound() {
// 			MenuDeleteRequest request = MenuDeleteRequest.builder().menuId(TEST_MENU_ID).build();
// 			when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.empty());
//
// 			GeneralException exception = assertThrows(GeneralException.class, () -> {
// 				menuService.deleteMenu(TEST_USER_ID, request);
// 			});
//
// 			assertEquals(MenuErrorCode.USER_NOT_FOUND_FOR_MENU, exception.getCode());
// 		}
//
// 		@Test
// 		@DisplayName("실패: 메뉴 없음")
// 		void deleteMenuFailMenuNotFound() {
// 			MenuDeleteRequest request = MenuDeleteRequest.builder().menuId(TEST_MENU_ID).build();
// 			User mockUser = mock(User.class);
//
// 			when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(mockUser));
// 			when(menuRepository.findByMenuIdAndDeletedAtIsNull(TEST_MENU_ID)).thenReturn(Optional.empty());
//
// 			GeneralException exception = assertThrows(GeneralException.class, () -> {
// 				menuService.deleteMenu(TEST_USER_ID, request);
// 			});
//
// 			assertEquals(MenuErrorCode.MENU_NOT_FOUND, exception.getCode());
// 		}
//
// 		@Test
// 		@DisplayName("실패: 메뉴 소유자 불일치")
// 		void deleteMenuFailUnauthorized() {
// 			MenuDeleteRequest request = MenuDeleteRequest.builder().menuId(TEST_MENU_ID).build();
// 			User mockUser = User.builder().userId(TEST_USER_ID).build();
// 			User anotherUser = User.builder().userId(2L).build();
// 			Store mockStore = Store.builder().user(anotherUser).build();
// 			Menu mockMenu = Menu.builder()
// 				.menuId(TEST_MENU_ID)
// 				.store(mockStore)
// 				.build();
//
// 			when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(mockUser));
// 			when(menuRepository.findByMenuIdAndDeletedAtIsNull(TEST_MENU_ID)).thenReturn(Optional.of(mockMenu));
//
// 			GeneralException exception = assertThrows(GeneralException.class, () -> {
// 				menuService.deleteMenu(TEST_USER_ID, request);
// 			});
//
// 			assertEquals(MenuErrorCode.MENU_NOT_FOUND,
// 				exception.getCode());
// 		}
//
// 		@Test
// 		@DisplayName("실패: 이미 삭제된 메뉴")
// 		void deleteMenuFailAlreadyDeleted() {
// 			MenuDeleteRequest request = MenuDeleteRequest.builder().menuId(TEST_MENU_ID).build();
// 			User mockUser = User.builder().userId(TEST_USER_ID).build();
// 			Store mockStore = Store.builder().user(mockUser).build();
// 			Menu mockMenu = Menu.builder()
// 				.menuId(TEST_MENU_ID)
// 				.store(mockStore)
// 				.build();
// 			mockMenu.setDeletedAt(LocalDateTime.now());
// 			when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(mockUser));
// 			when(menuRepository.findByMenuIdAndDeletedAtIsNull(TEST_MENU_ID)).thenReturn(Optional.of(mockMenu));
//
// 			GeneralException exception = assertThrows(GeneralException.class, () -> {
// 				menuService.deleteMenu(TEST_USER_ID, request);
// 			});
//
// 			assertEquals(MenuErrorCode.MENU_ALREADY_DELETED, exception.getCode());
// 		}
// 	}
//
// 	@Nested
// 	@DisplayName("메뉴 리스트 조회 테스트")
// 	class GetMenuListTest {
//
// 		@Test
// 		@DisplayName("성공: 메뉴 리스트 조회")
// 		void getMenuListSuccess() {
// 			MenuListRequest request = MenuListRequest.builder().storeId(TEST_STORE_ID).build();
//
// 			Store mockStore = mock(Store.class);
// 			when(mockStore.getStoreId()).thenReturn(TEST_STORE_ID);
//
// 			Menu menu1 = Menu.builder()
// 				.menuId(UUID.randomUUID())
// 				.name("메뉴1")
// 				.price(1000L)
// 				.description("설명1")
// 				.isHidden(false)
// 				.build();
// 			Menu menu2 = Menu.builder()
// 				.menuId(UUID.randomUUID())
// 				.name("메뉴2")
// 				.price(2000L)
// 				.description("설명2")
// 				.isHidden(true)
// 				.build();
// 			List<Menu> mockMenus = Arrays.asList(menu1, menu2);
//
// 			when(storeRepository.findById(TEST_STORE_ID)).thenReturn(Optional.of(mockStore));
// 			when(menuRepository.findByStoreAndDeletedAtIsNull(mockStore)).thenReturn(mockMenus);
//
// 			MenuListResponse response = menuService.getMenuList(request);
//
// 			assertNotNull(response);
// 			assertEquals(TEST_STORE_ID, response.getStoreId());
// 			assertEquals(2, response.getMenus().size());
// 			assertEquals("메뉴1", response.getMenus().get(0).getName());
// 			assertEquals("메뉴2", response.getMenus().get(1).getName());
//
// 			verify(storeRepository, times(1)).findById(TEST_STORE_ID);
// 			verify(menuRepository, times(1)).findByStoreAndDeletedAtIsNull(mockStore);
// 		}
//
// 		@Test
// 		@DisplayName("실패: 가게 없음")
// 		void getMenuListFailStoreNotFound() {
// 			MenuListRequest request = MenuListRequest.builder().storeId(TEST_STORE_ID).build();
//
// 			when(storeRepository.findById(TEST_STORE_ID)).thenReturn(Optional.empty());
//
// 			GeneralException exception = assertThrows(GeneralException.class, () -> {
// 				menuService.getMenuList(request);
// 			});
//
// 			assertEquals(StoreErrorCode.STORE_NOT_FOUND, exception.getCode());
// 		}
// 	}
//
// 	@Nested
// 	@DisplayName("메뉴 숨김/노출 테스트")
// 	class UpdateMenuVisibilityTest {
//
// 		@Test
// 		@DisplayName("성공: 메뉴 숨김/노출")
// 		void updateMenuVisibilitySuccess() {
// 			User mockUser = User.builder().userId(TEST_USER_ID).build();
// 			Store mockStore = Store.builder().user(mockUser).build();
// 			Menu mockMenu = Menu.builder()
// 				.menuId(TEST_MENU_ID)
// 				.store(mockStore)
// 				.name("테스트 메뉴")
// 				.isHidden(false)
// 				.build();
//
// 			when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(mockUser));
// 			when(menuRepository.findByMenuIdAndDeletedAtIsNull(TEST_MENU_ID)).thenReturn(Optional.of(mockMenu));
// 			when(menuRepository.save(any(Menu.class))).thenReturn(mockMenu);
//
// 			MenuUpdateResponse response = menuService.updateMenuVisibility(TEST_USER_ID, TEST_MENU_ID, true);
//
// 			assertNotNull(response);
// 			assertEquals(TEST_MENU_ID, response.getMenuId());
// 			assertEquals("테스트 메뉴", response.getName());
// 			assertTrue(mockMenu.isHidden());
//
// 			verify(userRepository, times(1)).findById(TEST_USER_ID);
// 			verify(menuRepository, times(1)).findByMenuIdAndDeletedAtIsNull(TEST_MENU_ID);
// 			verify(menuRepository, times(1)).save(any(Menu.class));
// 		}
//
// 		@Test
// 		@DisplayName("실패: 사용자 없음")
// 		void updateMenuVisibilityFailUserNotFound() {
// 			when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.empty());
//
// 			GeneralException exception = assertThrows(GeneralException.class, () -> {
// 				menuService.updateMenuVisibility(TEST_USER_ID, TEST_MENU_ID, true);
// 			});
//
// 			assertEquals(MenuErrorCode.USER_NOT_FOUND_FOR_MENU, exception.getCode());
// 		}
//
// 		@Test
// 		@DisplayName("실패: 메뉴 없음")
// 		void updateMenuVisibilityFailMenuNotFound() {
// 			User mockUser = mock(User.class);
//
// 			when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(mockUser));
// 			when(menuRepository.findByMenuIdAndDeletedAtIsNull(TEST_MENU_ID)).thenReturn(Optional.empty());
//
// 			GeneralException exception = assertThrows(GeneralException.class, () -> {
// 				menuService.updateMenuVisibility(TEST_USER_ID, TEST_MENU_ID, true);
// 			});
//
// 			assertEquals(MenuErrorCode.MENU_NOT_FOUND, exception.getCode());
// 		}
//
// 		@Test
// 		@DisplayName("실패: 메뉴 소유자 불일치")
// 		void updateMenuVisibilityFailUnauthorized() {
// 			User mockUser = User.builder().userId(TEST_USER_ID).build();
// 			User anotherUser = User.builder().userId(2L).build();
// 			Store mockStore = Store.builder().user(anotherUser).build();
// 			Menu mockMenu = Menu.builder()
// 				.menuId(TEST_MENU_ID)
// 				.store(mockStore)
// 				.build();
//
// 			when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(mockUser));
// 			when(menuRepository.findByMenuIdAndDeletedAtIsNull(TEST_MENU_ID)).thenReturn(Optional.of(mockMenu));
//
// 			GeneralException exception = assertThrows(GeneralException.class, () -> {
// 				menuService.updateMenuVisibility(TEST_USER_ID, TEST_MENU_ID, true);
// 			});
//
// 			assertEquals(MenuErrorCode.MENU_NOT_FOUND, exception.getCode());
// 		}
// 	}
// }
//
