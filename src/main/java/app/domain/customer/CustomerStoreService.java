package app.domain.customer;

import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import app.domain.customer.dto.response.GetCustomerStoreDetailResponse;
import app.domain.customer.dto.response.GetStoreListResponse;
import app.domain.review.model.ReviewRepository;
import app.domain.store.model.StoreQueryRepository;
import app.domain.store.model.entity.Store;
import app.domain.store.repository.StoreRepository;
import app.domain.store.status.StoreAcceptStatus;
import app.global.apiPayload.PagedResponse;
import app.global.apiPayload.code.status.ErrorStatus;
import app.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerStoreService {

	private final StoreRepository storeRepository;
	private final StoreQueryRepository storeQueryRepository;
	private final ReviewRepository reviewRepository;

	@Transactional(readOnly = true)
	public PagedResponse<GetStoreListResponse> getApprovedStore(Pageable pageable) {
		return storeQueryRepository.getApprovedStore(pageable);
	}
	@Transactional(readOnly = true)
	public GetCustomerStoreDetailResponse getApproveStoreDetail(UUID storeId) {
		Store store = storeRepository.findByStoreIdAndStoreAcceptStatusAndDeletedAtIsNull(storeId, StoreAcceptStatus.APPROVE)
			.orElseThrow(() -> new GeneralException(ErrorStatus.STORE_NOT_FOUND));
		Double avgRating = reviewRepository.getAverageRatingByStore(storeId);
		return GetCustomerStoreDetailResponse.from(store, avgRating != null ? avgRating : 0.0);
	}

	@Transactional(readOnly = true)
	public PagedResponse<GetStoreListResponse> searchApproveStores(String keyword, Pageable pageable) {
		return storeQueryRepository.searchStoresWithAvgRating(keyword, StoreAcceptStatus.APPROVE, pageable);
	}
}