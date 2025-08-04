package app.unit.domain.menu.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import app.domain.menu.model.entity.Menu;
import app.domain.menu.model.repository.MenuRepository;
import app.domain.store.model.entity.Store;
import app.domain.user.model.entity.User;
import app.domain.user.model.entity.enums.UserRole;

@ExtendWith(MockitoExtension.class)
public class MenuRepositoryTest {

	@Mock
	private MenuRepository menuRepository;

	private User createUser(String username, String email) {
		User user = User.builder()
			.username(username)
			.email(email)
			.password("password")
			.nickname(username)
			.realName(username)
			.phoneNumber("01012345678")
			.userRole(UserRole.OWNER)
			.build();
		return user;
	}

	private Store createStore(User user, String storeName) {
		Store store = Store.builder()
			.user(user)
			.storeName(storeName)
			.address("테스트 주소")
			.minOrderAmount(0)
			.build();
		return store;
	}

	private Menu createMenu(Store store, String name, Long price, String description, boolean isHidden) {
		Menu menu = Menu.builder()
			.store(store)
			.name(name)
			.price(price)
			.description(description)
			.isHidden(isHidden)
			.build();
		return menu;
	}

	@Test
	@DisplayName("findByStoreAndDeletedAtIsNull 테스트 - 삭제되지 않은 메뉴 조회")
	void findByStoreAndDeletedAtIsNullTest() {
		User user = createUser("testuser1", "test1@example.com");
		Store store = createStore(user, "테스트 가게1");

		Menu menu1 = createMenu(store, "메뉴1", 10000L, "설명1", false);
		Menu menu2 = createMenu(store, "메뉴2", 12000L, "설명2", false);
		Menu menu3 = createMenu(store, "삭제된 메뉴", 8000L, "설명3", false);
		menu3.setDeletedAt(LocalDateTime.now());

		List<Menu> mockMenus = List.of(menu1, menu2);
		when(menuRepository.findByStoreAndDeletedAtIsNull(store)).thenReturn(mockMenus);

		List<Menu> foundMenus = menuRepository.findByStoreAndDeletedAtIsNull(store);

		assertNotNull(foundMenus);
		assertEquals(2, foundMenus.size());
		assertTrue(foundMenus.contains(menu1));
		assertTrue(foundMenus.contains(menu2));
		assertFalse(foundMenus.contains(menu3));
	}

	@Test
	@DisplayName("findByMenuIdAndDeletedAtIsNull 테스트 - 삭제되지 않은 특정 메뉴 조회")
	void findByMenuIdAndDeletedAtIsNullTest() {
		User user = createUser("testuser2", "test2@example.com");
		Store store = createStore(user, "테스트 가게2");

		Menu menu = createMenu(store, "단일 메뉴", 15000L, "단일 메뉴 설명", false);

		when(menuRepository.findByMenuIdAndDeletedAtIsNull(menu.getMenuId())).thenReturn(Optional.of(menu));

		Optional<Menu> foundMenu = menuRepository.findByMenuIdAndDeletedAtIsNull(menu.getMenuId());

		assertTrue(foundMenu.isPresent());
		assertEquals(menu.getMenuId(), foundMenu.get().getMenuId());
	}

	@Test
	@DisplayName("findByMenuIdAndDeletedAtIsNull 테스트 - 삭제된 메뉴 조회 실패")
	void findByMenuIdAndDeletedAtIsNullDeletedMenuTest() {
		User user = createUser("testuser3", "test3@example.com");
		Store store = createStore(user, "테스트 가게3");

		Menu menu = createMenu(store, "삭제된 단일 메뉴", 10000L, "삭제된 메뉴 설명", false);
		menu.setDeletedAt(LocalDateTime.now());

		when(menuRepository.findByMenuIdAndDeletedAtIsNull(menu.getMenuId())).thenReturn(Optional.empty());

		Optional<Menu> foundMenu = menuRepository.findByMenuIdAndDeletedAtIsNull(menu.getMenuId());

		assertFalse(foundMenu.isPresent());
	}

	@Test
	@DisplayName("existsByStoreAndNameAndDeletedAtIsNull 테스트 - 중복 이름 존재")
	void existsByStoreAndNameAndDeletedAtIsNullDuplicateTest() {
		User user = createUser("testuser4", "test4@example.com");
		Store store = createStore(user, "테스트 가게4");

		createMenu(store, "중복 메뉴", 5000L, "중복 메뉴 설명", false);

		when(menuRepository.existsByStoreAndNameAndDeletedAtIsNull(store, "중복 메뉴")).thenReturn(true);

		boolean exists = menuRepository.existsByStoreAndNameAndDeletedAtIsNull(store, "중복 메뉴");

		assertTrue(exists);
	}

	@Test
	@DisplayName("existsByStoreAndNameAndDeletedAtIsNull 테스트 - 중복 이름 없음")
	void existsByStoreAndNameAndDeletedAtIsNullNoDuplicateTest() {
		User user = createUser("testuser5", "test5@example.com");
		Store store = createStore(user, "테스트 가게5");

		createMenu(store, "고유 메뉴", 7000L, "고유 메뉴 설명", false);

		when(menuRepository.existsByStoreAndNameAndDeletedAtIsNull(store, "다른 메뉴")).thenReturn(false);

		boolean exists = menuRepository.existsByStoreAndNameAndDeletedAtIsNull(store, "다른 메뉴");

		assertFalse(exists);
	}

	@Test
	@DisplayName("existsByStoreAndNameAndDeletedAtIsNull 테스트 - 삭제된 메뉴는 중복으로 간주 안함")
	void existsByStoreAndNameAndDeletedAtIsNullDeletedNotDuplicateTest() {
		User user = createUser("testuser6", "test6@example.com");
		Store store = createStore(user, "테스트 가게6");

		Menu deletedMenu = createMenu(store, "삭제된 중복 메뉴", 9000L, "삭제된 중복 메뉴 설명", false);
		deletedMenu.setDeletedAt(LocalDateTime.now());

		when(menuRepository.existsByStoreAndNameAndDeletedAtIsNull(store, "삭제된 중복 메뉴")).thenReturn(false);

		boolean exists = menuRepository.existsByStoreAndNameAndDeletedAtIsNull(store, "삭제된 중복 메뉴");

		assertFalse(exists);
	}
}
