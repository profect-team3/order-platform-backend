package app.domain.admin.controller;

import static org.springframework.data.domain.Sort.Direction.*;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import app.domain.admin.model.dto.response.GetUserDetailResponse;
import app.domain.admin.model.dto.response.GetUserListResponse;
import app.domain.admin.service.AdminUserService;
import app.domain.order.model.dto.response.GetOrderListResponse;
import app.global.apiPayload.ApiResponse;
import app.global.apiPayload.PagedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@Tag(name = "관리자 API", description = "관리자만 이용할 수 있는 API")
public class AdminUserController {

	private final AdminUserService adminUserService;

	@GetMapping
	@Operation(
		summary = "전체 유저 목록 조회",
		description = "가입한 유저 목록을 페이지 별로 조회합니다. 생성일 또는 수정일 기준으로 정렬 할수 있습ㅈ니다."
	)
	public ApiResponse<PagedResponse<GetUserListResponse>> getAllUsers(
		@PageableDefault(size = 20, sort = "createdAt", direction = DESC) Pageable pageable
	) {
		return ApiResponse.onSuccess(adminUserService.getAllUsers(pageable));
	}

	@GetMapping("/{userId}")
	@Operation(
		summary = "선택한 유저 정보 조회",
		description = "선택한 유저의 자세한 정보와 등록한 주소를 확인 합니다."
	)
	public ApiResponse<GetUserDetailResponse> getUsersDetailById(
		@PathVariable("userId") Long userId
	) {
		return ApiResponse.onSuccess(adminUserService.getUserDetailById(userId));
	}

	@GetMapping("/{userId}/order")
	@Operation(
		summary = "선택한 유저 주문내역 조회",
		description = "선택한 유저의 주문 정보를 확인 합니다."
	)
	public ApiResponse<PagedResponse<GetOrderListResponse>> getUsersOrderListById(
		@PageableDefault(size = 20, sort = "createdAt", direction = DESC) Pageable pageable,
		@PathVariable("userId") Long userId
	) {
		return ApiResponse.onSuccess(adminUserService.getUserOrderListById(userId, pageable));
	}

}