package app.domain.store;

import java.util.UUID;

import org.springframework.security.access.prepost.PreAuthorize;
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
import app.domain.store.status.StoreSuccessStatus;
import app.global.SecurityUtil;
import app.global.apiPayload.ApiResponse;
import app.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/store")
@RequiredArgsConstructor
public class StoreController {

	private final StoreService storeService;
	private final StoreRepository storeRepository;
	private final RegionRepository regionRepository;
	private final SecurityUtil securityUtil;

	@PostMapping
	public ApiResponse<StoreApproveResponse> createStore(@RequestBody StoreApproveRequest request) {
		validateCreateStoreRequest(request);

		StoreApproveResponse response = storeService.createStore(request);

		return ApiResponse.onSuccess(StoreSuccessStatus.STORE_CREATED_SUCCESS, response);
	}

	private void validateCreateStoreRequest(StoreApproveRequest request) {
		if (request.getRegionId() == null) {
			throw new GeneralException(StoreErrorCode.REGION_ID_NULL);
		}
		Region region = regionRepository.findById(request.getRegionId())
			.orElseThrow(() -> new GeneralException(StoreErrorCode.REGION_NOT_FOUND));

		if (request.getCategoryId() == null) {
			throw new GeneralException(StoreErrorCode.CATEGORY_ID_NULL);
		}
		if (request.getAddress() == null) {
			throw new GeneralException(StoreErrorCode.ADDRESS_NULL);
		}
		if (request.getStoreName() == null) {
			throw new GeneralException(StoreErrorCode.STORE_NAME_NULL);
		}
		if (request.getMinOrderAmount() == null) {
			throw new GeneralException(StoreErrorCode.MIN_ORDER_AMOUNT_NULL);
		}
		if (request.getMinOrderAmount() < 0) {
			throw new GeneralException(StoreErrorCode.MIN_ORDER_AMOUNT_INVALID);
		}
		if (storeRepository.existsByStoreNameAndRegion(request.getStoreName(), region)) {
			throw new GeneralException(StoreErrorCode.DUPLICATE_STORE_NAME_IN_REGION);
		}
	}

	@PutMapping
	@PreAuthorize("hasAuthority('OWNER')")
	public ApiResponse<StoreInfoUpdateResponse> updateStore(@RequestBody StoreInfoUpdateRequest request) {
		if (request.getStoreId() == null)
			throw new GeneralException(StoreErrorCode.STORE_ID_NULL);

		if (request.getMinOrderAmount() != null && request.getMinOrderAmount() < 0)
			throw new GeneralException(StoreErrorCode.MIN_ORDER_AMOUNT_INVALID);

		if (request.getCategoryId() == null)
			throw new GeneralException(StoreErrorCode.CATEGORY_ID_NULL);

		StoreInfoUpdateResponse response = storeService.updateStoreInfo(request);
		return ApiResponse.onSuccess(StoreSuccessStatus.STORE_UPDATED_SUCCESS, response);
	}

	@DeleteMapping("/{storeId}")
	@PreAuthorize("hasAuthority('OWNER')")
	public ApiResponse<String> deleteStore(@PathVariable UUID storeId) {
		storeService.deleteStore(storeId);

		return ApiResponse.onSuccess(StoreSuccessStatus.STORE_DELETED_SUCCESS, "가게 삭제가 완료되었습니다.");
	}
}
