package app.domain.menu;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import app.domain.menu.model.dto.response.GetMenuListResponse;
import app.domain.menu.model.entity.Menu;
import app.domain.menu.model.repository.MenuRepository;
import app.domain.store.repository.StoreRepository;
import app.global.apiPayload.PagedResponse;
import app.global.apiPayload.code.status.ErrorStatus;
import app.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerMenuService {

	private final MenuRepository menuRepository;
	private final StoreRepository storeRepository;

	@Transactional(readOnly = true)
	public PagedResponse<GetMenuListResponse> getMenusByStoreId(UUID storeId, Pageable pageable) {
		boolean exists = storeRepository.existsByStoreIdAndDeletedAtIsNull(storeId);
		if (!exists) {
			throw new GeneralException(ErrorStatus.STORE_NOT_FOUND);
		}
		Page<Menu> menuPage = menuRepository.findByStoreStoreIdAndHiddenFalse(storeId, pageable);
		Page<GetMenuListResponse> mapped = menuPage.map(GetMenuListResponse::from);

		return PagedResponse.from(mapped);
	}
}