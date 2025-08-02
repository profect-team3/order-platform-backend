// app.domain.review.ReviewService.java
package app.domain.review;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import app.domain.order.model.OrdersRepository;
import app.domain.order.model.entity.Orders;
import app.domain.review.model.ReviewRepository;
import app.domain.review.model.dto.request.CreateReviewRequest;
import app.domain.review.model.dto.response.GetReviewResponse;
import app.domain.review.model.entity.Review;
import app.domain.review.status.ReviewErrorStatus;
import app.domain.store.model.entity.StoreRepository;
import app.domain.user.model.UserRepository;
import app.domain.user.model.entity.User;
import app.global.apiPayload.code.status.ErrorStatus;
import app.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

	private final ReviewRepository reviewRepository;
	private final UserRepository userRepository;
	private final StoreRepository storeRepository;
	private final OrdersRepository ordersRepository;

	@Transactional
	public String createReview(Long userId, CreateReviewRequest request) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

		Orders order = ordersRepository.findById(request.ordersId())
			.orElseThrow(() -> new GeneralException(ReviewErrorStatus.ORDER_NOT_FOUND));

		if (!order.getUser().equals(user)) {
			throw new GeneralException(ErrorStatus._FORBIDDEN);
		}

		if (reviewRepository.existsByOrders(order)) {
			throw new GeneralException(ReviewErrorStatus.REVIEW_ALREADY_EXISTS);
		}

		Review review = Review.builder()
			.user(user)
			.store(order.getStore())
			.orders(order)
			.rating(request.rating())
			.content(request.content())
			.build();

		Review savedReview = reviewRepository.save(review);

		return "리뷰 : " + savedReview.getReviewId() + " 가 생성되었습니다.";
	}

	public List<GetReviewResponse> getReviews(Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

		List<Review> userReviews = reviewRepository.findByUser(user);

		if (userReviews.isEmpty()) {
			throw new GeneralException(ReviewErrorStatus.NO_REVIEWS_FOUND_FOR_USER);
		}

		List<GetReviewResponse> responses = userReviews.stream()
			.map(review -> new GetReviewResponse(
				review.getReviewId(),
				review.getUser().getUsername(),
				review.getStore().getStoreName(),
				review.getRating(),
				review.getContent(),
				review.getCreatedAt()
			))
			.collect(Collectors.toList());

		return responses;
	}
}