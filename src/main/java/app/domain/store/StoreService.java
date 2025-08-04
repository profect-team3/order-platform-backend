package app.domain.store;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import app.domain.menu.model.entity.Category;
import app.domain.menu.model.repository.CategoryRepository;
import app.domain.store.model.dto.request.StoreApproveRequest;
import app.domain.store.model.dto.request.StoreInfoUpdateRequest;
import app.domain.store.model.dto.response.StoreApproveResponse;
import app.domain.store.model.dto.response.StoreInfoUpdateResponse;
import app.domain.store.model.entity.Region;
import app.domain.store.model.entity.Store;
import app.domain.store.repository.RegionRepository;
import app.domain.store.repository.StoreRepository;
import app.domain.store.status.StoreAcceptStatus;
import app.domain.store.status.StoreErrorCode;
import app.domain.user.model.entity.User;
import app.global.SecurityUtil;
import app.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StoreService {

	private final StoreRepository storeRepository;
	private final RegionRepository regionRepository;
	private final CategoryRepository categoryRepository;
	private final SecurityUtil securityUtil;

	@Transactional
	public StoreApproveResponse createStore(StoreApproveRequest request) {

		User user = securityUtil.getCurrentUser();

		Region region = regionRepository.findById(request.getRegionId())
			.orElseThrow(() -> new GeneralException(StoreErrorCode.REGION_NOT_FOUND));

		Category category = categoryRepository.findById(request.getCategoryId())
			.orElseThrow(() -> new GeneralException(StoreErrorCode.CATEGORY_NOT_FOUND));

		Store store = new Store(null, user, region, category, request.getStoreName(), request.getDesc(),
			request.getAddress(), request.getPhoneNumber(), request.getMinOrderAmount(), StoreAcceptStatus.PENDING);

		Store savedStore = storeRepository.save(store);

		return new StoreApproveResponse(savedStore.getStoreId(), savedStore.getStoreAcceptStatus().name());
	}

	@Transactional
	public StoreInfoUpdateResponse updateStoreInfo(StoreInfoUpdateRequest request) {

		User user = securityUtil.getCurrentUser();
		Long userId = user.getUserId();

		Store store = storeRepository.findById(request.getStoreId())
			.orElseThrow(() -> new GeneralException(StoreErrorCode.STORE_NOT_FOUND));

		if (!store.getUser().getUserId().equals(userId)) {
			throw new GeneralException(StoreErrorCode.STORE_NOT_FOUND);
		}

		if (request.getCategoryId() != null) {
			Category category = categoryRepository.findById(request.getCategoryId())
				.orElseThrow(() -> new GeneralException(StoreErrorCode.CATEGORY_NOT_FOUND));
			store.setCategory(category);
		}
		if (request.getName() != null) {
			store.setStoreName(request.getName());
		}
		if (request.getAddress() != null) {
			store.setAddress(request.getAddress());
		}
		if (request.getPhoneNumber() != null) {
			store.setPhoneNumber(request.getPhoneNumber());
		}
		if (request.getMinOrderAmount() != null) {
			store.setMinOrderAmount(request.getMinOrderAmount());
		}
		if (request.getDesc() != null) {
			store.setDescription(request.getDesc());
		}

		Store updatedStore = storeRepository.save(store);
		return new StoreInfoUpdateResponse(updatedStore.getStoreId());
	}

	@Transactional
	public void deleteStore(UUID storeId) {
		User user = securityUtil.getCurrentUser();
		Long userId = user.getUserId();

		Store store = storeRepository.findById(storeId)
			.orElseThrow(() -> new GeneralException(StoreErrorCode.STORE_NOT_FOUND));

		if (!store.getUser().getUserId().equals(userId)) {
			throw new GeneralException(StoreErrorCode.STORE_NOT_FOUND);
		}

		store.markAsDeleted();
	}
}
