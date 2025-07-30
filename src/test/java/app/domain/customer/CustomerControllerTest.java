package app.domain.customer;

import app.domain.customer.dto.*;
import app.domain.customer.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
public class CustomerControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    private CustomerOrderResponse sampleOrderResponse;
    private CreateReviewRequest sampleCreateReviewRequest;
    private CreateReviewResponse sampleCreateReviewResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        UUID orderId = UUID.randomUUID();
        UUID reviewId = UUID.randomUUID();

        sampleOrderResponse = CustomerOrderResponse.builder()
                .orderId(orderId)
                .storeName("Test Store")
                .totalPrice(15000)
                .deliveryAddress("Test Address")
                .paymentMethod("CREDIT_CARD")
                .orderStatus("COMPLETED")
                .orderDate(LocalDateTime.now())
                .items(Arrays.asList(
                        CustomerOrderResponse.OrderItemDto.builder()
                                .menuName("Test Menu")
                                .price(15000)
                                .quantity(1)
                                .build()
                ))
                .build();

        sampleCreateReviewRequest = CreateReviewRequest.builder()
                .customerId(1L)
                .orderId(orderId)
                .rating(5)
                .content("Great food!")
                .build();

        sampleCreateReviewResponse = CreateReviewResponse.builder()
                .reviewId(reviewId)
                .message("리뷰가 성공적으로 작성되었습니다.")
                .build();
    }

    @Test
    void getCustomerOrders_Success() throws Exception {
        List<CustomerOrderResponse> orders = Arrays.asList(sampleOrderResponse);
        when(customerService.getCustomerOrders(eq(1L), isNull(), isNull(), isNull()))
                .thenReturn(orders);

        mockMvc.perform(get("/customer/order")
                        .param("customerId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].storeName").value("Test Store"))
                .andExpect(jsonPath("$[0].totalPrice").value(15000));
    }

    @Test
    void createReview_Success() throws Exception {
        when(customerService.createReview(any(CreateReviewRequest.class)))
                .thenReturn(sampleCreateReviewResponse);

        mockMvc.perform(post("/customer/review")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleCreateReviewRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("리뷰가 성공적으로 작성되었습니다."));
    }
}