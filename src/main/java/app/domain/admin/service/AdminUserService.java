package app.domain.admin.service;

import org.springframework.data.domain.Pageable;

import app.domain.admin.model.dto.response.GetUserDetailResponse;
import app.domain.admin.model.dto.response.GetUserListResponse;
import app.domain.order.model.dto.response.GetOrderListResponse;
import app.global.apiPayload.PagedResponse;

public interface AdminUserService {

	PagedResponse<GetUserListResponse> getAllUsers(Pageable pageable);

	GetUserDetailResponse getUserDetailById(Long id);

	PagedResponse<GetOrderListResponse> getUserOrderListById(Long userId, Pageable pageable);
}
