package app.domain.review.model.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public class CreateReviewRequest {

	@NotNull(message = "주문 ID는 필수입니다.")
	private UUID ordersId;

	@NotNull(message = "평점은 필수입니다.")
	@Min(value = 1, message = "평점은 1 이상이어야 합니다.")
	@Max(value = 5, message = "평점은 5 이하여야 합니다.")
	private Long rating;

	@NotBlank(message = "리뷰 내용은 필수입니다.")
	private String content;

	public CreateReviewRequest() {
	}

	public CreateReviewRequest(UUID ordersId, Long rating, String content) {
		this.ordersId = ordersId;
		this.rating = rating;
		this.content = content;
	}

	public UUID getOrdersId() {
		return ordersId;
	}

	public void setOrdersId(UUID ordersId) {
		this.ordersId = ordersId;
	}

	public Long getRating() {
		return rating;
	}

	public void setRating(Long rating) {
		this.rating = rating;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
