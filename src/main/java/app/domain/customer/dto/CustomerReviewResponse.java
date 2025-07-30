package app.domain.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerReviewResponse {
    private UUID reviewId;
    private UUID orderId;
    private String storeName;
    private int rating;
    private String content;
    private LocalDateTime createdDate;
}