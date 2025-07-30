package app.domain.customer;

import app.domain.customer.dto.CreateReviewRequest;
import app.domain.customer.dto.CreateReviewResponse;
import app.domain.customer.dto.CustomerOrderResponse;
import app.domain.customer.dto.CustomerReviewResponse;
import app.domain.customer.service.CustomerService;
import app.domain.order.entity.OrderItem;
import app.domain.order.entity.Orders;
import app.domain.order.entity.enums.OrderStatus;
import app.domain.order.entity.enums.PaymentMethod;
import app.domain.order.repository.OrderItemRepository;
import app.domain.order.repository.OrdersRepository;
import app.domain.review.entity.Review;
import app.domain.review.repository.ReviewRepository;
import app.domain.store.entity.Store;
import app.domain.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @Mock
    private OrdersRepository ordersRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private CustomerService customerService;

    private User testUser;
    private Store testStore;
    private Orders testOrder;
    private OrderItem testOrderItem;
    private Review testReview;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .userId(1L)
                .username("testuser")
                .email("test@example.com")
                .build();

        testStore = Store.builder()
                .storeId(UUID.randomUUID())
                .storeName("Test Store")
                .build();

        testOrder = Orders.builder()
                .OrdersId(UUID.randomUUID())
                .user(testUser)
                .store(testStore)
                .totalPrice(15000)
                .deliveryAddress("Test Address")
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .OrderStatus(OrderStatus.COMPLETED)
                .build();

        testOrderItem = OrderItem.builder()
                .OrderItemId(UUID.randomUUID())
                .orders(testOrder)
                .menuName("Test Menu")
                .price(15000)
                .quantity(1)
                .build();

        testReview = Review.builder()
                .reviewId(UUID.randomUUID())
                .Orders(testOrder)
                .user(testUser)
                .store(testStore)
                .rating(5)
                .context("Great food!")
                .build();
    }

    @Test
    void getCustomerOrders_Success() {
        // Given
        when(ordersRepository.findByCustomerId(1L)).thenReturn(Arrays.asList(testOrder));
        when(orderItemRepository.findByOrderId(testOrder.getOrdersId())).thenReturn(Arrays.asList(testOrderItem));

        // When
        List<CustomerOrderResponse> result = customerService.getCustomerOrders(1L, null, null, null);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStoreName()).isEqualTo("Test Store");
        assertThat(result.get(0).getTotalPrice()).isEqualTo(15000);
        assertThat(result.get(0).getItems()).hasSize(1);
    }

    @Test
    void getCustomerReviews_Success() {
        // Given
        when(reviewRepository.findByCustomerId(1L)).thenReturn(Arrays.asList(testReview));

        // When
        List<CustomerReviewResponse> result = customerService.getCustomerReviews(1L, null);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStoreName()).isEqualTo("Test Store");
        assertThat(result.get(0).getRating()).isEqualTo(5);
        assertThat(result.get(0).getContent()).isEqualTo("Great food!");
    }

    @Test
    void createReview_Success() {
        // Given
        CreateReviewRequest request = CreateReviewRequest.builder()
                .customerId(1L)
                .orderId(testOrder.getOrdersId())
                .rating(5)
                .content("Great food!")
                .build();

        when(reviewRepository.findByOrderId(testOrder.getOrdersId())).thenReturn(Arrays.asList());
        when(ordersRepository.findById(testOrder.getOrdersId())).thenReturn(Optional.of(testOrder));
        when(reviewRepository.save(any(Review.class))).thenReturn(testReview);

        // When
        CreateReviewResponse result = customerService.createReview(request);

        // Then
        assertThat(result.getReviewId()).isEqualTo(testReview.getReviewId());
        assertThat(result.getMessage()).isEqualTo("리뷰가 성공적으로 작성되었습니다.");
    }

    @Test
    void createReview_DuplicateReview_ThrowsException() {
        // Given
        CreateReviewRequest request = CreateReviewRequest.builder()
                .customerId(1L)
                .orderId(testOrder.getOrdersId())
                .rating(5)
                .content("Great food!")
                .build();

        when(reviewRepository.findByOrderId(testOrder.getOrdersId())).thenReturn(Arrays.asList(testReview));

        // When & Then
        assertThatThrownBy(() -> customerService.createReview(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 해당 주문에 대한 리뷰가 작성되었습니다.");
    }

    @Test
    void createReview_OrderNotFound_ThrowsException() {
        // Given
        CreateReviewRequest request = CreateReviewRequest.builder()
                .customerId(1L)
                .orderId(UUID.randomUUID())
                .rating(5)
                .content("Great food!")
                .build();

        when(reviewRepository.findByOrderId(any())).thenReturn(Arrays.asList());
        when(ordersRepository.findById(any())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> customerService.createReview(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("주문을 찾을 수 없습니다.");
    }
}