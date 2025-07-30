package app.domain.customer.service;

import app.domain.customer.dto.*;
import app.domain.order.entity.OrderItem;
import app.domain.order.entity.Orders;
import app.domain.order.repository.OrderItemRepository;
import app.domain.order.repository.OrdersRepository;
import app.domain.review.entity.Review;
import app.domain.review.repository.ReviewRepository;
import app.domain.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class CustomerService {

    private final OrdersRepository ordersRepository;
    private final OrderItemRepository orderItemRepository;
    private final ReviewRepository reviewRepository;

    @Autowired
    public CustomerService(OrdersRepository ordersRepository, 
                          OrderItemRepository orderItemRepository,
                          ReviewRepository reviewRepository) {
        this.ordersRepository = ordersRepository;
        this.orderItemRepository = orderItemRepository;
        this.reviewRepository = reviewRepository;
    }

    public List<CustomerOrderResponse> getCustomerOrders(Long customerId, UUID orderId, 
                                                        LocalDateTime startDate, LocalDateTime endDate) {
        List<Orders> orders;
        
        if (orderId != null) {
            orders = ordersRepository.findById(orderId)
                    .filter(order -> order.getUser() != null && order.getUser().getUserId().equals(customerId))
                    .map(List::of)
                    .orElse(List.of());
        } else if (startDate != null && endDate != null) {
            orders = ordersRepository.findByCustomerIdAndDateRange(customerId, startDate, endDate);
        } else {
            orders = ordersRepository.findByCustomerId(customerId);
        }

        return orders.stream()
                .map(this::convertToCustomerOrderResponse)
                .collect(Collectors.toList());
    }

    public List<CustomerReviewResponse> getCustomerReviews(Long customerId, UUID storeId) {
        List<Review> reviews;
        
        if (storeId != null) {
            reviews = reviewRepository.findByStoreId(storeId);
        } else {
            reviews = reviewRepository.findByCustomerId(customerId);
        }

        return reviews.stream()
                .map(this::convertToCustomerReviewResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public CreateReviewResponse createReview(CreateReviewRequest request) {
        // Check if review already exists for this order
        List<Review> existingReviews = reviewRepository.findByOrderId(request.getOrderId());
        if (!existingReviews.isEmpty()) {
            throw new IllegalArgumentException("이미 해당 주문에 대한 리뷰가 작성되었습니다.");
        }

        // Find the order to get store information
        Orders order = ordersRepository.findById(request.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

        // Verify the order belongs to the customer
        if (order.getUser() == null || !order.getUser().getUserId().equals(request.getCustomerId())) {
            throw new IllegalArgumentException("해당 주문에 대한 권한이 없습니다.");
        }

        Review review = Review.builder()
                .Orders(order)
                .user(order.getUser())
                .store(order.getStore())
                .rating(request.getRating())
                .context(request.getContent())
                .build();

        Review savedReview = reviewRepository.save(review);

        return CreateReviewResponse.builder()
                .reviewId(savedReview.getReviewId())
                .message("리뷰가 성공적으로 작성되었습니다.")
                .build();
    }

    private CustomerOrderResponse convertToCustomerOrderResponse(Orders order) {
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getOrdersId());
        
        List<CustomerOrderResponse.OrderItemDto> itemDtos = orderItems.stream()
                .map(item -> CustomerOrderResponse.OrderItemDto.builder()
                        .menuName(item.getMenuName())
                        .price(item.getPrice())
                        .quantity(item.getQuantity())
                        .build())
                .collect(Collectors.toList());

        return CustomerOrderResponse.builder()
                .orderId(order.getOrdersId())
                .storeName(order.getStore() != null ? order.getStore().getStoreName() : "Unknown Store")
                .totalPrice(order.getTotalPrice())
                .deliveryAddress(order.getDeliveryAddress())
                .paymentMethod(order.getPaymentMethod().name())
                .orderStatus(order.getOrderStatus().name())
                .orderDate(order.getCreatedAt())
                .items(itemDtos)
                .build();
    }

    private CustomerReviewResponse convertToCustomerReviewResponse(Review review) {
        return CustomerReviewResponse.builder()
                .reviewId(review.getReviewId())
                .orderId(review.getOrders().getOrdersId())
                .storeName(review.getStore() != null ? review.getStore().getStoreName() : "Unknown Store")
                .rating(review.getRating())
                .content(review.getContext())
                .createdDate(review.getCreatedAt())
                .build();
    }
}