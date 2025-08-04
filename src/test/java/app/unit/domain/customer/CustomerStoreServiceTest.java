package app.unit.domain.customer;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
import org.springframework.data.domain.Sort;

import app.domain.customer.CustomerStoreService;
import app.domain.customer.dto.response.GetCustomerStoreDetailResponse;
import app.domain.customer.dto.response.GetStoreListResponse;
import app.domain.menu.model.entity.Category;
import app.domain.review.model.ReviewRepository;
import app.domain.store.model.StoreQueryRepository;
import app.domain.store.model.entity.Store;
import app.domain.store.repository.StoreRepository;
import app.domain.store.status.StoreAcceptStatus;
import app.global.apiPayload.PagedResponse;
import app.global.apiPayload.code.status.ErrorStatus;
import app.global.apiPayload.exception.GeneralException;

@ExtendWith(MockitoExtension.class)
class CustomerStoreServiceTest {

	@Mock
	private StoreRepository storeRepository;

	@Mock
	private StoreQueryRepository storeQueryRepository;

	@Mock
	private ReviewRepository reviewRepository;

	@InjectMocks
	private CustomerStoreService customerStoreService;

	UUID storeId = UUID.randomUUID();

	@Test
	@DisplayName("승인된 가게 목록 조회 성공")
	void getApprovedStore_success() {
		// given
		UUID storeId = UUID.randomUUID();
		Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());

		GetStoreListResponse dto = GetStoreListResponse.builder()
			.storeId(storeId)
			.storeName("맛있는 족발집")
			.address("서울시 마포구")
			.minOrderAmount(15000L)
			.averageRating(4.5)
			.build();

		Page<GetStoreListResponse> page = new PageImpl<>(List.of(dto), pageable, 1);
		given(storeQueryRepository.getApprovedStore(pageable))
			.willReturn(PagedResponse.from(page));

		// when
		PagedResponse<GetStoreListResponse> result = customerStoreService.getApprovedStore(pageable);

		// then
		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getContent().get(0).getStoreId()).isEqualTo(storeId);
		assertThat(result.getContent().get(0).getAverageRating()).isEqualTo(4.5);
	}

	@Test
	@DisplayName("가게 상세 조회 성공")
	void getApproveStoreDetail_success() {
		// given
		Store store = mock(Store.class);
		Category category = mock(Category.class); // 필요한 필드 추가
		given(category.getCategoryName()).willReturn("족발");
		given(store.getCategory()).willReturn(category);
		given(store.getStoreId()).willReturn(storeId);
		given(storeRepository.findByStoreIdAndStoreAcceptStatusAndDeletedAtIsNull(storeId, StoreAcceptStatus.APPROVE))
			.willReturn(Optional.of(store));
		given(reviewRepository.getAverageRatingByStore(storeId)).willReturn(3.8);

		// when
		GetCustomerStoreDetailResponse response = customerStoreService.getApproveStoreDetail(storeId);

		// then
		assertThat(response.getStoreId()).isEqualTo(storeId);
		assertThat(response.getAverageRating()).isEqualTo(3.8);
		assertThat(response.getCategoryName()).isEqualTo("족발");
	}

	@Test
	@DisplayName("가게 상세 조회 실패 - 존재하지 않음")
	void getApproveStoreDetail_fail_notFound() {
		// given
		given(storeRepository.findByStoreIdAndStoreAcceptStatusAndDeletedAtIsNull(storeId, StoreAcceptStatus.APPROVE))
			.willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> customerStoreService.getApproveStoreDetail(storeId))
			.isInstanceOf(GeneralException.class)
			.satisfies(ex -> {
				GeneralException ge = (GeneralException)ex;
				assertThat(((GeneralException)ex).getCode()).isEqualTo(ErrorStatus.STORE_NOT_FOUND);
			});
	}

	@Test
	@DisplayName("가게 검색 성공")
	void searchApproveStores_success() {
		// given
		UUID storeId = UUID.randomUUID();
		Pageable pageable = PageRequest.of(0, 10);

		GetStoreListResponse dto = GetStoreListResponse.builder()
			.storeId(storeId)
			.storeName("맛있는 족발집")
			.address("서울시 마포구")
			.minOrderAmount(15000L)
			.averageRating(5.0)
			.build();

		Page<GetStoreListResponse> page = new PageImpl<>(List.of(dto), pageable, 1);
		PagedResponse<GetStoreListResponse> pagedResponse = PagedResponse.from(page);

		given(storeQueryRepository.searchStoresWithAvgRating("족발", StoreAcceptStatus.APPROVE, pageable))
			.willReturn(pagedResponse);

		// when
		PagedResponse<GetStoreListResponse> response = customerStoreService.searchApproveStores("족발", pageable);

		// then
		assertThat(response.getContent()).hasSize(1);
		assertThat(response.getContent().get(0).getStoreId()).isEqualTo(storeId);
		assertThat(response.getContent().get(0).getAverageRating()).isEqualTo(5.0);
	}

	@Test
	@DisplayName("승인된 가게 키워드 검색 성공 - 결과 없음")
	void searchApproveStores_emptyResult() {
		// given
		Pageable pageable = PageRequest.of(0, 10);
		Page<GetStoreListResponse> page = new PageImpl<>(Collections.emptyList(), pageable, 0);
		given(storeQueryRepository.searchStoresWithAvgRating("없는 키워드", StoreAcceptStatus.APPROVE, pageable))
			.willReturn(PagedResponse.from(page));

		// when
		PagedResponse<GetStoreListResponse> response = customerStoreService.searchApproveStores("없는 키워드", pageable);

		// then
		assertThat(response.getContent()).isEmpty();
	}

	@Test
	@DisplayName("승인된 가게 상세 조회 실패 - 가게 없음")
	void getApproveStoreDetail_storeNotFound() {
		// given
		UUID storeId = UUID.randomUUID();
		given(storeRepository.findByStoreIdAndStoreAcceptStatusAndDeletedAtIsNull(storeId, StoreAcceptStatus.APPROVE))
			.willReturn(Optional.empty());

		// when
		GeneralException ex = catchThrowableOfType(
			() -> customerStoreService.getApproveStoreDetail(storeId),
			GeneralException.class
		);

		// then
		assertThat(ex.getErrorReasonHttpStatus().getCode()).isEqualTo(ErrorStatus.STORE_NOT_FOUND);
	}

}