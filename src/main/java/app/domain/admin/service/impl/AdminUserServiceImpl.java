package app.domain.admin.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import app.domain.admin.model.dto.response.GetUserDetailResponse;
import app.domain.admin.model.dto.response.GetUserListResponse;
import app.domain.admin.service.AdminUserService;
import app.domain.order.model.dto.response.GetOrderListResponse;
import app.domain.order.model.entity.Orders;
import app.domain.order.model.mapper.OrdersRepository;
import app.domain.user.model.UserAddressRepository;
import app.domain.user.model.UserRepository;
import app.domain.user.model.dto.response.GetUserAddressListResponse;
import app.domain.user.model.entity.User;
import app.domain.user.model.entity.enums.UserRole;
import app.global.apiPayload.PagedResponse;
import app.global.apiPayload.code.status.ErrorStatus;
import app.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

	private final UserRepository userRepository;
	private final UserAddressRepository userAddressRepository;
	private final OrdersRepository ordersRepository;

	@Override
	public PagedResponse<GetUserListResponse> getAllUsers(Pageable pageable) {
		Page<GetUserListResponse> page = userRepository.findAllByRole(UserRole.USER, pageable)
			.map(GetUserListResponse::from);

		return PagedResponse.from(page);
	}

	@Override
	public GetUserDetailResponse getUserDetailById(Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
		List<GetUserAddressListResponse> addressList = userAddressRepository.findAllByUserUserId(userId)
			.stream().map(GetUserAddressListResponse::from).toList();

		return GetUserDetailResponse.from(user, addressList);
	}

	@Override
	public PagedResponse<GetOrderListResponse> getUserOrderListById(Long userId, Pageable pageable) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

		Page<Orders> ordersPage = ordersRepository.findAllByUserAndDeliveryAddressIsNotNull(user, pageable);
		Page<GetOrderListResponse> mapped = ordersPage.map(GetOrderListResponse::from);

		return PagedResponse.from(mapped);
	}

}