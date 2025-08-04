package app.unit.domain.customer;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import app.domain.customer.CustomerStoreController;
import app.domain.customer.CustomerStoreService;
import app.domain.customer.dto.response.GetCustomerStoreDetailResponse;
import app.domain.customer.dto.response.GetStoreListResponse;
import app.global.apiPayload.PagedResponse;
import app.global.apiPayload.code.status.ErrorStatus;
import app.global.apiPayload.exception.GeneralException;
import app.global.config.MockSecurityConfig;
import app.global.jwt.JwtAccessDeniedHandler;
import app.global.jwt.JwtAuthenticationEntryPoint;
import app.global.jwt.JwtTokenProvider;

@WebMvcTest(CustomerStoreController.class)
@Import({MockSecurityConfig.class})
class CustomerStoreControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private CustomerStoreService customerStoreService;

	@MockitoBean
	private JwtTokenProvider jwtTokenProvider;

	@MockitoBean
	private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

	@MockitoBean
	private JwtAccessDeniedHandler jwtAccessDeniedHandler;

	private final UUID storeId = UUID.randomUUID();

	@Test
	@DisplayName("가게 목록 조회 성공")
	void getApprovedStoreList() throws Exception {
		List<GetStoreListResponse> stores = List.of(
			new GetStoreListResponse(storeId, "맛집1", "서울 강남구", 3000, 4.5),
			new GetStoreListResponse(UUID.randomUUID(), "맛집2", "부산 해운대구", 2000, 3.9)
		);

		Page<GetStoreListResponse> page = new PageImpl<>(stores, PageRequest.of(0, 20), stores.size());
		given(customerStoreService.getApprovedStore(any())).willReturn(PagedResponse.from(page));

		mockMvc.perform(get("/customer/store")
				.param("page", "0")
				.param("size", "20")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.result.content.length()").value(2))
			.andExpect(jsonPath("$.result.content[0].storeName").value("맛집1"))
			.andExpect(jsonPath("$.result.content[0].address").value("서울 강남구"))
			.andExpect(jsonPath("$.result.content[0].averageRating").value(4.5));
	}

	@Test
	@DisplayName("가게 상세 조회 성공")
	void getApprovedStoreDetail() throws Exception {
		GetCustomerStoreDetailResponse response = new GetCustomerStoreDetailResponse(
			storeId, "맛집1", "한식 맛집", "서울 강남", "010-1234-5678", 3000L, "서울", 4.5
		);
		given(customerStoreService.getApproveStoreDetail(storeId)).willReturn(response);

		mockMvc.perform(get("/customer/store/{storeId}", storeId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.result.storeName").value("맛집1"))
			.andExpect(jsonPath("$.result.phoneNumber").value("010-1234-5678"));
	}

	@Test
	@DisplayName("가게 검색 성공")
	void searchApprovedStore() throws Exception {
		String keyword = "치킨";
		List<GetStoreListResponse> stores = List.of(
			new GetStoreListResponse(storeId, "치킨집", "서울 강남구", 3000, 4.5)
		);
		Page<GetStoreListResponse> page = new PageImpl<>(stores, PageRequest.of(0, 10), 1);
		given(customerStoreService.searchApproveStores(eq(keyword), any())).willReturn(PagedResponse.from(page));

		mockMvc.perform(get("/customer/store/search")
				.param("keyword", keyword)
				.param("page", "0")
				.param("size", "10"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.result.content.length()").value(1))
			.andExpect(jsonPath("$.result.content[0].storeName").value("치킨집"));
	}

	@Test
	@DisplayName("가게 검색 결과 없음 - 빈 리스트 반환")
	void searchApprovedStore_emptyResult() throws Exception {
		// given
		Page<GetStoreListResponse> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);
		given(customerStoreService.searchApproveStores(eq("없는키워드"), any()))
			.willReturn(PagedResponse.from(emptyPage));

		// when & then
		mockMvc.perform(get("/customer/store/search")
				.param("keyword", "없는키워드")
				.param("page", "0")
				.param("size", "10"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.result.content").isEmpty())
			.andExpect(jsonPath("$.result.totalElements").value(0));
	}

	@Test
	@DisplayName("가게 상세 조회 실패 - 존재하지 않는 가게")
	void getStoreDetail_storeNotFound() throws Exception {
		// given
		given(customerStoreService.getApproveStoreDetail(storeId))
			.willThrow(new GeneralException(ErrorStatus.STORE_NOT_FOUND));

		// when & then
		mockMvc.perform(get("/customer/store/{storeId}", storeId))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value(ErrorStatus.STORE_NOT_FOUND.getMessage()))
			.andExpect(jsonPath("$.code").value(ErrorStatus.STORE_NOT_FOUND.getCode()));
	}
}