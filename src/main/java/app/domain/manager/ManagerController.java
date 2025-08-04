package app.domain.manager;

import static org.springframework.data.domain.Sort.Direction.*;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import app.domain.manager.dto.response.GetCustomerDetailResponse;
import app.domain.manager.dto.response.GetCustomerListResponse;
import app.domain.manager.dto.response.GetStoreDetailResponse;
import app.domain.customer.dto.response.GetStoreListResponse;
import app.domain.manager.status.ManagerSuccessStatus;
import app.domain.order.model.dto.response.OrderDetailResponse;
import app.domain.store.status.StoreAcceptStatus;
import app.global.apiPayload.ApiResponse;
import app.global.apiPayload.PagedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/manager")
@RequiredArgsConstructor
@Tag(name = "관리자 API", description = "관리자의 사용자 관리 API")
public class ManagerController {

	private final ManagerService managerService;

	@GetMapping("/customer")
	@Operation(
		summary = "전체 사용자 목록 조회",
		description = "가입한 사용자 목록을 페이지 별로 조회합니다. 생성일 또는 수정일 기준으로 정렬할 수 있습니다.")
	public ApiResponse<PagedResponse<GetCustomerListResponse>> getAllCustomer(
		@PageableDefault(size = 20, sort = "createdAt", direction = DESC) Pageable pageable,
		@AuthenticationPrincipal UserDetails principal
	) {
		return ApiResponse.onSuccess(ManagerSuccessStatus.MANAGER_GET_CUSTOMER_OK,managerService.getAllCustomer(pageable));
	}

	@GetMapping("/customer/{userId}")
	@Operation(
		summary = "선택한 유저 정보 조회",
		description = "선택한 유저의 자세한 정보와 등록한 주소를 확인 합니다."
	)
	public ApiResponse<GetCustomerDetailResponse> getUsersDetailById(
		@PathVariable("userId") Long userId
	) {
		return ApiResponse.onSuccess(ManagerSuccessStatus.MANAGER_GET_CUSTOMER_DETAIL_OK,managerService.getCustomerDetailById(userId));
	}

	@GetMapping("/customer/{userId}/order")
	@Operation(
		summary = "선택한 사용자 주문내역 조회",
		description = "선택한 사용자의 주문 정보를 확인 합니다."
	)
	public ApiResponse<PagedResponse<OrderDetailResponse>> getCustomerOrderListById(
		@PageableDefault(size = 20, sort = "createdAt", direction = DESC) Pageable pageable,
		@PathVariable("userId") Long userId
	) {
		return ApiResponse.onSuccess(ManagerSuccessStatus.MANAGER_GET_CUSTOMER_ORDER_OK,managerService.getCustomerOrderListById(userId, pageable));
	}

	@GetMapping("/customer/search")
	@Operation(
		summary = "사용자 검색",
		description = "키워드를 사용하여 가입한 사용자를 검색하고, 결과를 페이지 별로 조회합니다. 생성일 또는 수정일 기준으로 정렬할 수 있습니다.")
	public ApiResponse<PagedResponse<GetCustomerListResponse>> searchCustomer(
		Pageable pageable,
		@RequestParam String keyWord
	) {
		return ApiResponse.onSuccess(ManagerSuccessStatus.MANAGER_SEARCH_CUSTOMER_OK,managerService.searchCustomer(keyWord, pageable));
	}

	@GetMapping("/store")
	@Operation(
		summary = "전체 가게 목록 조회",
		description = "가게 목록을 페이지 별로 조회합니다. 생성일 또는 수정일 기준으로 정렬할 수 있습니다.")
	public ApiResponse<PagedResponse<GetStoreListResponse>> getAllStore(
		@PageableDefault(size = 20, sort = "createdAt", direction = DESC) Pageable pageable,
		@RequestParam(defaultValue = "APPROVE") StoreAcceptStatus status
	) {
		return ApiResponse.onSuccess(ManagerSuccessStatus.MANAGER_GET_STORE_LIST_OK,managerService.getAllStore( status,pageable));
	}


	@GetMapping("/store/{storeId}")
	@Operation(
		summary = "선택한 가게 상세 정보 조회",
		description = "가게의 점주의 정보 및 가게의 정보를 조회 합니다")
	public ApiResponse<GetStoreDetailResponse> getStoreById(
		@PathVariable UUID storeId
	){
		return ApiResponse.onSuccess(ManagerSuccessStatus.MANAGER_GET_STORE_DETAIL_OK,managerService.getStoreDetail(storeId));
	}

	@PatchMapping("/store/{storeId}/accept")
	@Operation(
		summary = "선택한 가게 상태를 수정 합니다.",
		description = "가게의 등록요청을 거절하거나 승인 합니다.")
	public ApiResponse<String> approveStore(
		@PathVariable UUID storeId,
		@RequestParam StoreAcceptStatus status
	) {
		return ApiResponse.onSuccess(ManagerSuccessStatus.MANAGER_UPDATE_STORE_STATUS_OK,managerService.approveStore(storeId, status));
	}

	@GetMapping("/store/search")
	@Operation(
		summary = "가게를 검색합니다.",
		description = "가게 제목에 따라서 검색 합니다.")
	public ApiResponse<PagedResponse<GetStoreListResponse>> getAllStore(
		@PageableDefault(size = 20, sort = "createdAt", direction = DESC) Pageable pageable,
		@RequestParam(required = false) String keyword,
		@RequestParam(required = false) StoreAcceptStatus status
	) {
		return ApiResponse.onSuccess(ManagerSuccessStatus.MANAGER_SEARCH_STORE_OK,managerService.searchStore(status, keyword, pageable));
	}

}