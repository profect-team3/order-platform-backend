package app.domain.store;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import app.domain.store.model.dto.request.StoreApproveRequest;
import app.domain.store.model.dto.request.StoreInfoUpdateRequest;
import app.domain.store.model.dto.response.StoreApproveResponse;
import app.domain.store.model.dto.response.StoreInfoUpdateResponse;
import app.domain.store.model.entity.Region;
import app.domain.store.repository.RegionRepository;
import app.domain.store.repository.StoreRepository;
import app.domain.store.status.StoreErrorCode;
import app.domain.store.status.StoreException;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/store")
@RequiredArgsConstructor
public class StoreController {

	private final StoreService storeService;
	private final StoreRepository storeRepository;
	private final RegionRepository regionRepository;

	@PostMapping
	public ResponseEntity<StoreApproveResponse> createStore(@RequestBody StoreApproveRequest request) {
		validateCreateStoreRequest(request);

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Long userId = Long.parseLong(authentication.getName());

		StoreApproveResponse response = storeService.createStore(userId, request);
		return ResponseEntity.ok(response);
	}

	private void validateCreateStoreRequest(StoreApproveRequest request) {
		if (request.getRegionId() == null) {
			throw new StoreException(StoreErrorCode.REGION_ID_NULL);
		}
		Region region = regionRepository.findById(request.getRegionId())
			.orElseThrow(() -> new StoreException(StoreErrorCode.REGION_NOT_FOUND));

		if (request.getCategoryId() == null) {
			throw new StoreException(StoreErrorCode.CATEGORY_ID_NULL);
		}
		if (request.getAddress() == null) {
			throw new StoreException(StoreErrorCode.ADDRESS_NULL);
		}
		if (request.getStoreName() == null) {
			throw new StoreException(StoreErrorCode.STORE_NAME_NULL);
		}
		if (request.getMinOrderAmount() == null) {
			throw new StoreException(StoreErrorCode.MIN_ORDER_AMOUNT_NULL);
		}
		if (request.getMinOrderAmount() < 0) {
			throw new StoreException(StoreErrorCode.MIN_ORDER_AMOUNT_INVALID);
		}
		if (storeRepository.existsByStoreNameAndRegion(request.getStoreName(), region)) {
			throw new StoreException(StoreErrorCode.DUPLICATE_STORE_NAME_IN_REGION);
		}
	}

	@PutMapping("/store")
	public ResponseEntity<StoreInfoUpdateResponse> updateStore(@RequestBody StoreInfoUpdateRequest request) {
		if (request.getStoreId() == null)
			throw new StoreException(StoreErrorCode.STORE_ID_NULL);

		if (request.getMinOrderAmount() != null && request.getMinOrderAmount() < 0)
			throw new StoreException(StoreErrorCode.MIN_ORDER_AMOUNT_INVALID);

		if (request.getCategoryId() == null)
			throw new StoreException(StoreErrorCode.CATEGORY_ID_NULL);

		StoreInfoUpdateResponse response = storeService.updateStoreInfo(request);
		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/store/{storeId}")
	public ResponseEntity<String> deleteStore(@PathVariable UUID storeId) {
		storeService.deleteStore(storeId);
		return ResponseEntity.ok("가게 삭제가 완료되었습니다.");
	}
}