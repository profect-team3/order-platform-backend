package app.domain.customer;

import static org.springframework.data.domain.Sort.Direction.*;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import app.domain.customer.dto.response.GetCustomerStoreDetailResponse;
import app.domain.customer.dto.response.GetStoreListResponse;
import app.domain.customer.status.CustomerSuccessStatus;
import app.global.apiPayload.ApiResponse;
import app.global.apiPayload.PagedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/customer/store")
@RequiredArgsConstructor
@Tag(name = "사용자 API", description = "사용자의 가게 조회")
public class CustomerStoreController {

	private final CustomerStoreService customerStoreService;


	@GetMapping
	@Operation(
		summary = "승인이 허용된 가게 목록 조회",
		description = "가게 목록을 페이지 별로 조회합니다. 생성일 또는 수정일 기준으로 정렬할 수 있습니다.")
	public ApiResponse<PagedResponse<GetStoreListResponse>> getApprovedStoreList(
		@PageableDefault(size = 20, sort = "createdAt", direction = DESC) Pageable pageable) {
		return ApiResponse.onSuccess(CustomerSuccessStatus.CUSTOMER_GET_STORE_LIST_OK,
			customerStoreService.getApprovedStore(pageable));
	}


	@GetMapping("/{storeId}")
	@Operation(
		summary = "승인이 허용된 가게 상세 조회",
		description = "가게 상세 목록을 조회 합니다")
	public ApiResponse<GetCustomerStoreDetailResponse> getApprovedStoreDetail(@PathVariable UUID storeId) {
		return ApiResponse.onSuccess(CustomerSuccessStatus.CUSTOMER_GET_STORE_DETAIL_OK,
			customerStoreService.getApproveStoreDetail(storeId));
	}


	@GetMapping("/search")
	@Operation(
		summary = "가게 목록 검색",
		description = "가게를 키워드에 따라 검색 합니다 ")
	public ApiResponse<PagedResponse<GetStoreListResponse>> searchApprovedStore(
		@RequestParam String keyword,
		@PageableDefault(size = 10) Pageable pageable) {
		return ApiResponse.onSuccess(CustomerSuccessStatus.CUSTOMER_SEARCH_STORE_OK,
			customerStoreService.searchApproveStores(keyword, pageable));
	}
}