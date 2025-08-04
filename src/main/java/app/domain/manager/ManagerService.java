package app.domain.manager;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import app.domain.customer.dto.response.GetCustomerAddressListResponse;
import app.domain.manager.dto.response.GetCustomerListResponse;
import app.domain.manager.dto.response.GetStoreDetailResponse;
import app.domain.manager.dto.response.GetCustomerDetailResponse;
import app.domain.customer.dto.response.GetStoreListResponse;
import app.domain.order.model.repository.OrderItemRepository;
import app.domain.order.model.repository.OrdersRepository;
import app.domain.manager.status.ManagerErrorStatus;
import app.domain.order.model.dto.response.OrderDetailResponse;
import app.domain.order.model.entity.OrderItem;
import app.domain.order.model.entity.Orders;
import app.domain.review.model.ReviewRepository;
import app.domain.store.model.StoreQueryRepository;
import app.domain.store.model.entity.Store;
import app.domain.store.repository.StoreRepository;
import app.domain.store.status.StoreAcceptStatus;
import app.domain.user.UserSearchRepository;
import app.domain.user.model.UserAddressRepository;
import app.domain.user.model.UserRepository;
import app.domain.user.model.entity.User;
import app.domain.user.model.entity.enums.UserRole;
import app.global.apiPayload.PagedResponse;
import app.global.apiPayload.code.status.ErrorStatus;
import app.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ManagerService {

	private final UserRepository userRepository;
	private final UserSearchRepository userSearchRepository;
	private final UserAddressRepository userAddressRepository;
	private final OrdersRepository ordersRepository;
	private final OrderItemRepository orderItemRepository;
	private final StoreRepository storeRepository;
	private final ReviewRepository reviewRepository;
	private final StoreQueryRepository storeQueryRepository;

	@Transactional(readOnly = true)
	public PagedResponse<GetCustomerListResponse> getAllCustomer(Pageable pageable) {
		Page<GetCustomerListResponse> page = userRepository.findAllByUserRole(UserRole.CUSTOMER, pageable)
			.map(GetCustomerListResponse::from);

		return PagedResponse.from(page);
	}

	@Transactional(readOnly = true)
	public GetCustomerDetailResponse getCustomerDetailById(Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
		List<GetCustomerAddressListResponse> addressList = userAddressRepository.findAllByUserUserId(userId)
			.stream().map(GetCustomerAddressListResponse::from).toList();

		return GetCustomerDetailResponse.from(user, addressList);
	}

	@Transactional(readOnly = true)
	public PagedResponse<OrderDetailResponse> getCustomerOrderListById(Long userId, Pageable pageable) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

		Page<Orders> ordersPage = ordersRepository.findAllByUserAndDeliveryAddressIsNotNull(user, pageable);

		Page<OrderDetailResponse> mapped = ordersPage.map(order -> {
			List<OrderItem> orderItems = orderItemRepository.findByOrders(order);
			return OrderDetailResponse.from(order, orderItems);
		});

		return PagedResponse.from(mapped);
	}

	@Transactional(readOnly = true)
	public PagedResponse<GetCustomerListResponse> searchCustomer(String keyWord, Pageable pageable) {
		Page<User> users = userSearchRepository.searchUser(keyWord, pageable);

		Page<GetCustomerListResponse> content = users.map(GetCustomerListResponse::from);

		return PagedResponse.from(content);
	}

	@Transactional(readOnly = true)
	public PagedResponse<GetStoreListResponse> getAllStore(StoreAcceptStatus status, Pageable pageable) {
		return storeQueryRepository.getAllStore(status, pageable);
	}

	@Transactional(readOnly = true)
	public GetStoreDetailResponse getStoreDetail(UUID storeId) {
		Store store = storeRepository.findByStoreIdAndDeletedAtIsNull(storeId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.STORE_NOT_FOUND));

		Double avgRating = reviewRepository.getAverageRatingByStore(storeId);

		return GetStoreDetailResponse.from(store, avgRating != null ? avgRating : 0.0);
	}

	@Transactional
	public String approveStore(UUID storeId, StoreAcceptStatus status) {
		Store store = storeRepository.findByStoreIdAndDeletedAtIsNull(storeId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.STORE_NOT_FOUND));
		if (status == store.getStoreAcceptStatus()) {
			throw new GeneralException(ManagerErrorStatus.INVALID_STORE_STATUS);
		}
		store.updateAcceptStatus(status);
		return store.getStoreName() + "의 상태가 변경 되었습니다.";
	}

	@Transactional(readOnly = true)
	public PagedResponse<GetStoreListResponse> searchStore(StoreAcceptStatus status, String keyword,
		Pageable pageable) {
		return storeQueryRepository.searchStoresWithAvgRating(keyword, status, pageable);
	}

}