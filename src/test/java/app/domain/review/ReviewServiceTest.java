package app.domain.review;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import app.domain.order.model.OrdersRepository;
import app.domain.order.model.entity.Orders;
import app.domain.review.model.ReviewRepository;
import app.domain.review.model.dto.request.CreateReviewRequest;
import app.domain.review.model.dto.response.GetReviewResponse;
import app.domain.review.model.entity.Review;
import app.domain.store.model.entity.Store;
import app.domain.user.model.UserRepository;
import app.domain.user.model.entity.User;
import app.global.apiPayload.code.status.ErrorStatus;
import app.global.apiPayload.exception.GeneralException;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReviewService 테스트")
class ReviewServiceTest {

	@Mock
	private ReviewRepository reviewRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private OrdersRepository ordersRepository;

	@InjectMocks
	private ReviewService reviewService;

	private User user;
	private User otherUser;
	private Store store;
	private Orders order;
	private Review review;

	@BeforeEach
	void setUp() {
		user = User.builder().userId(1L).username("testuser").nickname("testnick").build();
		otherUser = User.builder().userId(2L).username("otheruser").nickname("othernick").build();
		store = Store.builder().storeId(UUID.randomUUID()).storeName("teststore").build();
		order = Orders.builder().ordersId(UUID.randomUUID()).user(user).store(store).build();
		review = Review.builder()
			.reviewId(UUID.randomUUID())
			.user(user)
			.store(store)
			.orders(order)
			.rating(5L)
			.content("Great!")
			.build();
	}

	@Test
	@DisplayName("리뷰 생성 - 성공")
	void createReview_Success() {
		CreateReviewRequest request = new CreateReviewRequest(order.getOrdersId(), 5L, "Great!");
		when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
		when(ordersRepository.findById(order.getOrdersId())).thenReturn(Optional.of(order));
		when(reviewRepository.existsByOrders(order)).thenReturn(false);
		when(reviewRepository.save(any(Review.class))).thenReturn(review);

		String result = reviewService.createReview(user.getUserId(), request);

		assertNotNull(result);
		assertTrue(result.contains(review.getReviewId().toString()));
		assertTrue(result.contains("가 생성되었습니다."));
		verify(reviewRepository, times(1)).save(any(Review.class));
	}

	@Test
	@DisplayName("리뷰 생성 - 실패 (사용자 없음)")
	void createReview_Fail_UserNotFound() {
		Long nonExistentUserId = 999L;
		CreateReviewRequest request = new CreateReviewRequest(order.getOrdersId(), 5L, "Great!");
		when(userRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());

		GeneralException exception = assertThrows(GeneralException.class,
			() -> reviewService.createReview(nonExistentUserId, request));
		assertEquals(ErrorStatus.USER_NOT_FOUND, exception.getErrorStatus());
	}

	@Test
	@DisplayName("리뷰 생성 - 실패 (주문 없음)")
	void createReview_Fail_OrderNotFound() {
		UUID nonExistentOrderId = UUID.randomUUID();
		CreateReviewRequest request = new CreateReviewRequest(nonExistentOrderId, 5L, "Great!");
		when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
		when(ordersRepository.findById(nonExistentOrderId)).thenReturn(Optional.empty());

		GeneralException exception = assertThrows(GeneralException.class,
			() -> reviewService.createReview(user.getUserId(), request));
		assertEquals(ErrorStatus.ORDER_NOT_FOUND, exception.getErrorStatus());
	}

	@Test
	@DisplayName("리뷰 생성 - 실패 (주문한 사용자가 아님)")
	void createReview_Fail_Forbidden() {
		CreateReviewRequest request = new CreateReviewRequest(order.getOrdersId(), 5L, "Great!");
		when(userRepository.findById(otherUser.getUserId())).thenReturn(Optional.of(otherUser));
		when(ordersRepository.findById(request.ordersId())).thenReturn(Optional.of(order));
		
		GeneralException exception = assertThrows(GeneralException.class,
			() -> reviewService.createReview(otherUser.getUserId(), request));

		assertEquals(ErrorStatus._FORBIDDEN, exception.getErrorStatus());
	}

	@Test
	@DisplayName("리뷰 생성 - 실패 (해당 주문에 이미 리뷰가 존재)")
	void createReview_Fail_ReviewAlreadyExists() {
		CreateReviewRequest request = new CreateReviewRequest(order.getOrdersId(), 5L, "Great!");

		when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
		when(ordersRepository.findById(order.getOrdersId())).thenReturn(Optional.of(order));
		when(reviewRepository.existsByOrders(order)).thenReturn(true);

		GeneralException exception = assertThrows(GeneralException.class,
			() -> reviewService.createReview(user.getUserId(), request));

		assertEquals(ErrorStatus.REVIEW_ALREADY_EXISTS, exception.getErrorStatus());
	}

	@Test
	@DisplayName("사용자 리뷰 조회 - 성공")
	void getReviews_Success() {
		when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
		when(reviewRepository.findByUser(user)).thenReturn(Collections.singletonList(review));

		List<GetReviewResponse> responses = reviewService.getReviews(user.getUserId());

		assertNotNull(responses);
		assertEquals(1, responses.size());
		GetReviewResponse response = responses.get(0);
		assertEquals(review.getReviewId(), response.reviewId());
		assertEquals(user.getUsername(), response.customerName());
		assertEquals(store.getStoreName(), response.storeName());
		assertEquals(review.getRating(), response.rating());
		assertEquals(review.getContent(), response.content());
		verify(reviewRepository, times(1)).findByUser(user);
	}

	@Test
	@DisplayName("사용자 리뷰 조회 - 실패 (사용자 없음)")
	void getReviews_Fail_UserNotFound() {
		Long nonExistentUserId = 999L;
		when(userRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());

		GeneralException exception = assertThrows(GeneralException.class,
			() -> reviewService.getReviews(nonExistentUserId));
		assertEquals(ErrorStatus.USER_NOT_FOUND, exception.getErrorStatus());
	}

	@Test
	@DisplayName("사용자 리뷰 조회 - 실패 (리뷰 없음)")
	void getReviews_Fail_NoReviewsFound() {
		when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
		when(reviewRepository.findByUser(user)).thenReturn(Collections.emptyList());

		GeneralException exception = assertThrows(GeneralException.class,
			() -> reviewService.getReviews(user.getUserId()));
		assertEquals(ErrorStatus.NO_REVIEWS_FOUND_FOR_USER, exception.getErrorStatus());
	}
}