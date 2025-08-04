package app.domain.menu;

import static org.springframework.data.domain.Sort.Direction.*;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import app.domain.menu.model.dto.response.GetMenuListResponse;
import app.domain.menu.status.MenuSuccessStatus;
import app.global.apiPayload.ApiResponse;
import app.global.apiPayload.PagedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
@Tag(name = "사용자 API", description = "사용자의 메뉴관련 API")
public class CustomerMenuController {

	private final CustomerMenuService customerMenuService;

	@GetMapping("/{storeId}/menus")
	@Operation(
		summary = "메뉴 조회",
		description = "사용자가 가게 메뉴들을 조회 합니다.")
	public ApiResponse<PagedResponse<GetMenuListResponse>> getMenus(
		@PathVariable UUID storeId,
		@PageableDefault(size = 20, sort = "createdAt", direction = DESC) Pageable pageable) {
		return ApiResponse.onSuccess(
			MenuSuccessStatus.CUSTOMER_GET_STORE_MENU_LIST_OK, customerMenuService.getMenusByStoreId(storeId, pageable));
	}
}