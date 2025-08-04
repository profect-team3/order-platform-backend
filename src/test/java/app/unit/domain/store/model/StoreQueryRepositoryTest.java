package app.unit.domain.store.model;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import app.domain.customer.dto.response.GetStoreListResponse;
import app.domain.store.model.StoreQueryRepository;
import app.domain.store.status.StoreAcceptStatus;
import app.global.apiPayload.PagedResponse;

@ExtendWith(MockitoExtension.class)
class StoreQueryRepositoryTest {

	@Mock
	StoreQueryRepository storeQueryRepository;

	@Test
	@DisplayName("searchStoresWithAvgRating - 키워드 검색 성공")
	void searchStoresWithAvgRating_success() {
		// given
		UUID storeId = UUID.randomUUID();
		Pageable pageable = PageRequest.of(0, 10);
		GetStoreListResponse dto = GetStoreListResponse.builder()
			.storeId(storeId)
			.storeName("맛있는 족발집")
			.address("서울시 마포구")
			.minOrderAmount(15000L)
			.averageRating(4.5)
			.build();
		Page<GetStoreListResponse> page = new PageImpl<>(List.of(dto), pageable, 1);
		PagedResponse<GetStoreListResponse> response = PagedResponse.from(page);

		given(storeQueryRepository.searchStoresWithAvgRating("족발", StoreAcceptStatus.APPROVE, pageable))
			.willReturn(response);

		// when
		PagedResponse<GetStoreListResponse> result = storeQueryRepository.searchStoresWithAvgRating("족발",
			StoreAcceptStatus.APPROVE, pageable);

		// then
		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getContent().get(0).getStoreName()).isEqualTo("맛있는 족발집");
		assertThat(result.getContent().get(0).getAverageRating()).isEqualTo(4.5);
	}

	@Test
	@DisplayName("getApprovedStore - 승인된 가게 목록 조회 성공")
	void getApprovedStore_success() {
		// given
		UUID storeId = UUID.randomUUID();
		Pageable pageable = PageRequest.of(0, 10);
		GetStoreListResponse dto = GetStoreListResponse.builder()
			.storeId(storeId)
			.storeName("족발천국")
			.address("서울시 강남구")
			.minOrderAmount(12000L)
			.averageRating(4.3)
			.build();
		Page<GetStoreListResponse> page = new PageImpl<>(List.of(dto), pageable, 1);
		PagedResponse<GetStoreListResponse> response = PagedResponse.from(page);

		given(storeQueryRepository.getApprovedStore(pageable)).willReturn(response);

		// when
		PagedResponse<GetStoreListResponse> result = storeQueryRepository.getApprovedStore(pageable);

		// then
		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getContent().get(0).getStoreName()).isEqualTo("족발천국");
		assertThat(result.getContent().get(0).getAverageRating()).isEqualTo(4.3);
	}

	@Test
	@DisplayName("getAllStore - 전체 가게 목록 조회 성공")
	void getAllStore_success() {
		// given
		UUID storeId = UUID.randomUUID();
		Pageable pageable = PageRequest.of(0, 10);
		GetStoreListResponse dto = GetStoreListResponse.builder()
			.storeId(storeId)
			.storeName("순대국집")
			.address("부산광역시 해운대구")
			.minOrderAmount(10000L)
			.averageRating(4.8)
			.build();
		Page<GetStoreListResponse> page = new PageImpl<>(List.of(dto), pageable, 1);
		PagedResponse<GetStoreListResponse> response = PagedResponse.from(page);

		given(storeQueryRepository.getAllStore(StoreAcceptStatus.APPROVE, pageable)).willReturn(response);

		// when
		PagedResponse<GetStoreListResponse> result = storeQueryRepository.getAllStore(StoreAcceptStatus.APPROVE,
			pageable);

		// then
		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getContent().get(0).getStoreName()).isEqualTo("순대국집");
		assertThat(result.getContent().get(0).getAverageRating()).isEqualTo(4.8);
	}
}