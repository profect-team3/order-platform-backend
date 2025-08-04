package app.unit.domain.menu;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
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

import app.domain.menu.CustomerMenuService;
import app.domain.menu.model.dto.response.GetMenuListResponse;
import app.domain.menu.model.entity.Menu;
import app.domain.menu.model.repository.MenuRepository;
import app.domain.store.repository.StoreRepository;
import app.global.apiPayload.PagedResponse;
import app.global.apiPayload.code.status.ErrorStatus;
import app.global.apiPayload.exception.GeneralException;

@ExtendWith(MockitoExtension.class)
class CustomerMenuServiceTest {

	@InjectMocks
	private CustomerMenuService menuService;

	@Mock
	private MenuRepository menuRepository;

	@Mock
	private StoreRepository storeRepository;

	private UUID storeId;

	@BeforeEach
	void setUp() {
		storeId = UUID.randomUUID();
	}

	@Test
	@DisplayName("메뉴 리스트 조회 - 성공")
	void getMenusByStoreId_success() {
		// given
		Pageable pageable = PageRequest.of(0, 10);
		List<Menu> menus = List.of(
			Menu.builder()
				.menuId(UUID.randomUUID())
				.name("김치찌개")
				.price(9000L)
				.isHidden(false)
				.build()
		);
		Page<Menu> menuPage = new PageImpl<>(menus, pageable, menus.size());

		given(storeRepository.existsByStoreIdAndDeletedAtIsNull(storeId)).willReturn(true);
		given(menuRepository.findByStoreStoreIdAndHiddenFalse(storeId, pageable)).willReturn(menuPage);

		// when
		PagedResponse<GetMenuListResponse> response = menuService.getMenusByStoreId(storeId, pageable);

		// then
		assertThat(response.getContent()).hasSize(1);
		assertThat(response.getContent().get(0).getName()).isEqualTo("김치찌개");
	}

	@Test
	@DisplayName("메뉴 리스트 조회 - 가게가 존재하지 않음")
	void getMenusByStoreId_storeNotFound() {
		// given
		UUID storeId = UUID.randomUUID(); // storeId 명시
		Pageable pageable = PageRequest.of(0, 10);
		given(storeRepository.existsByStoreIdAndDeletedAtIsNull(storeId)).willReturn(false);

		// when
		GeneralException ex = assertThrows(GeneralException.class,
			() -> menuService.getMenusByStoreId(storeId, pageable));

		// then
		assertThat(ex.getErrorReasonHttpStatus().getHttpStatus()).isEqualTo(ErrorStatus.STORE_NOT_FOUND);
		assertThat(ex.getErrorReasonHttpStatus().getMessage()).isEqualTo("해당 가맹점을 찾을 수 없습니다.");
	}

	@Test
	@DisplayName("가게에 노출 가능한 메뉴가 없는 경우 빈 리스트 반환")
	void getMenusByStoreId_returnsEmpty_whenNoVisibleMenus() {
		UUID storeId = UUID.randomUUID();
		Pageable pageable = PageRequest.of(0, 10);

		given(storeRepository.existsByStoreIdAndDeletedAtIsNull(storeId)).willReturn(true);
		given(menuRepository.findByStoreStoreIdAndHiddenFalse(storeId, pageable))
			.willReturn(Page.empty(pageable));

		PagedResponse<GetMenuListResponse> result = menuService.getMenusByStoreId(storeId, pageable);

		assertThat(result.getContent()).isEmpty();
		assertThat(result.getTotalElements()).isZero();
	}

}