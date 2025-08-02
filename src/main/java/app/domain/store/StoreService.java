package app.domain.store;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import app.domain.menu.model.entity.Category;
import app.domain.menu.model.entity.CategoryRepository;
import app.domain.store.model.dto.request.StoreApproveRequest;
import app.domain.store.model.dto.request.StoreInfoUpdateRequest;
import app.domain.store.model.dto.response.StoreApproveResponse;
import app.domain.store.model.dto.response.StoreInfoUpdateResponse;
import app.domain.store.model.entity.Region;
import app.domain.store.model.entity.Store;
import app.domain.store.repository.RegionRepository;
import app.domain.store.repository.StoreRepository;
import app.domain.store.status.StoreAcceptStatus;
import app.domain.user.model.UserRepository;
import app.domain.user.model.entity.User;
import app.domain.store.status.StoreErrorCode;
import app.domain.store.status.StoreException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StoreService {

	private final StoreRepository storeRepository;
	private final RegionRepository regionRepository;
	private final UserRepository userRepository;
	private final CategoryRepository categoryRepository;

	@Transactional
	public StoreApproveResponse createStore(Long userId, StoreApproveRequest request) {

		Region region = regionRepository.findById(request.getRegionId())
			.orElseThrow(() -> new StoreException(StoreErrorCode.REGION_NOT_FOUND));

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new StoreException(StoreErrorCode.USER_NOT_FOUND));

		Category category = categoryRepository.findById(request.getCategoryId())
			.orElseThrow(() -> new StoreException(StoreErrorCode.CATEGORY_NOT_FOUND));

		Store store = Store.builder()
			.storeName(request.getStoreName())
			.region(region)
			.user(user)
			.category(category)
			.address(request.getAddress())
			.description(request.getDesc())
			.phoneNumber(request.getPhoneNumber())
			.minOrderAmount(request.getMinOrderAmount().intValue())
			.storeAcceptStatus(StoreAcceptStatus.PENDING)
			.build();

		Store savedStore = storeRepository.save(store);

		return StoreApproveResponse.builder()
			.storeId(savedStore.getStoreId())
			.storeApprovalStatus(savedStore.getStoreAcceptStatus().name())
			.build();
	}

	@Transactional
	public StoreInfoUpdateResponse updateStoreInfo(StoreInfoUpdateRequest request) {
		Store store = storeRepository.findById(request.getStoreId())
			.orElseThrow(() -> new StoreException(StoreErrorCode.STORE_NOT_FOUND));

		if (request.getCategoryId() != null) {
			Category category = categoryRepository.findById(request.getCategoryId())
				.orElseThrow(() -> new StoreException(StoreErrorCode.CATEGORY_NOT_FOUND));
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
		return StoreInfoUpdateResponse.builder().storeId(updatedStore.getStoreId()).build();
	}

	@Transactional
	public void deleteStore(UUID storeId) {
		Store store = storeRepository.findById(storeId)
			.orElseThrow(() -> new StoreException(StoreErrorCode.STORE_NOT_FOUND));

		store.markAsDeleted();
	}
}
