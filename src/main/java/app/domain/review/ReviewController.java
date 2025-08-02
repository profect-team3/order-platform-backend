package app.domain.review;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import app.domain.review.model.dto.request.CreateReviewRequest;
import app.domain.review.model.dto.response.GetReviewResponse;
import app.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "review", description = "리뷰 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/customer/review")
public class ReviewController {

	private final ReviewService reviewService;

	@PostMapping
	public ApiResponse<String> createReview(
		@AuthenticationPrincipal UserDetails principal,
		@Valid @RequestBody CreateReviewRequest request
	) {
		Long userId = Long.parseLong(principal.getUsername());
		return ApiResponse.onSuccess(reviewService.createReview(userId, request));
	}

	@GetMapping
	public ApiResponse<List<GetReviewResponse>> getReviews(
		@AuthenticationPrincipal UserDetails principal
	) {
		Long userId = Long.parseLong(principal.getUsername());
		return ApiResponse.onSuccess(reviewService.getReviews(userId));
	}
}