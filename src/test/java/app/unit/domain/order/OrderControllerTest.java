// package app.unit.domain.order;
//
// import static org.mockito.ArgumentMatchers.*;
// import static org.mockito.Mockito.*;
// import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
// import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
// import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
// import java.util.List;
// import java.util.UUID;
//
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
// import org.springframework.context.annotation.Import;
// import org.springframework.http.MediaType;
// import org.springframework.security.test.context.support.WithMockUser;
// import org.springframework.test.context.bean.override.mockito.MockitoBean;
// import org.springframework.test.web.servlet.MockMvc;
// import org.springframework.test.web.servlet.setup.MockMvcBuilders;
// import org.springframework.web.context.WebApplicationContext;
//
// import com.fasterxml.jackson.databind.ObjectMapper;
//
// import app.domain.order.OrderController;
// import app.domain.order.model.dto.request.CreateOrderRequest;
// import app.domain.order.model.dto.response.OrderDetailResponse;
// import app.domain.order.model.entity.enums.OrderChannel;
// import app.domain.order.model.entity.enums.OrderStatus;
// import app.domain.order.model.entity.enums.PaymentMethod;
// import app.domain.order.model.entity.enums.ReceiptMethod;
// import app.domain.order.service.OrderService;
// import app.global.apiPayload.code.status.ErrorStatus;
// import app.global.apiPayload.exception.GeneralException;
// import app.global.config.MockSecurityConfig;
//
// @WebMvcTest(OrderController.class)
// @Import({MockSecurityConfig.class})
// @DisplayName("OrderController 테스트")
// class OrderControllerTest {
//
// 	@Autowired
// 	private MockMvc mockMvc;
//
// 	@Autowired
// 	private ObjectMapper objectMapper;
//
// 	@Autowired
// 	private WebApplicationContext context;
//
// 	@MockitoBean
// 	private OrderService orderService;
//
// 	@BeforeEach
// 	void setUp() {
// 		mockMvc = MockMvcBuilders
// 			.webAppContextSetup(context)
// 			.apply(springSecurity())
// 			.build();
// 	}
//
// 	@Test
// 	@DisplayName("주문 생성 - 성공")
// 	@WithMockUser(username = "1", roles = {"CUSTOMER"})
// 	void createOrder_Success() throws Exception {
// 		Long userId = 1L;
// 		UUID orderId = UUID.randomUUID();
// 		CreateOrderRequest request = new CreateOrderRequest(
// 			PaymentMethod.CREDIT_CARD,
// 			OrderChannel.ONLINE,
// 			ReceiptMethod.DELIVERY,
// 			"문 앞에 놓아주세요",
// 			10000L,
// 			"서울시 강남구"
// 		);
//
// 		when(orderService.createOrder(any(CreateOrderRequest.class)))
// 			.thenReturn(orderId);
//
// 		mockMvc.perform(post("/customer/order")
// 				.with(csrf())
// 				.contentType(MediaType.APPLICATION_JSON)
// 				.content(objectMapper.writeValueAsString(request)))
// 			.andExpect(status().isOk())
// 			.andExpect(jsonPath("$.code").value("COMMON200"))
// 			.andExpect(jsonPath("$.message").value("success"))
// 			.andExpect(jsonPath("$.result").value(orderId.toString()));
//
// 		verify(orderService).createOrder(any(CreateOrderRequest.class));
// 	}
//
// 	@Test
// 	@DisplayName("주문 생성 - 총 금액 0 이하 실패")
// 	@WithMockUser(username = "1", roles = {"CUSTOMER"})
// 	void createOrder_InvalidTotalPrice() throws Exception {
// 		CreateOrderRequest request = new CreateOrderRequest(
// 			PaymentMethod.CREDIT_CARD,
// 			OrderChannel.ONLINE,
// 			ReceiptMethod.DELIVERY,
// 			"문 앞에 놓아주세요",
// 			0L,
// 			"서울시 강남구"
// 		);
//
// 		mockMvc.perform(post("/customer/order")
// 				.with(csrf())
// 				.contentType(MediaType.APPLICATION_JSON)
// 				.content(objectMapper.writeValueAsString(request)))
// 			.andExpect(status().isBadRequest())
// 			.andExpect(jsonPath("$.code").value("COMMON400"))
// 			.andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
// 			.andExpect(jsonPath("$.result.totalPrice").value("총 금액은 양의 정수여야 합니다."));
//
// 		verify(orderService, never()).createOrder(any(), any());
// 	}
//
// 	@Test
// 	@DisplayName("주문 생성 - 서비스 에러")
// 	@WithMockUser(username = "1", roles = {"CUSTOMER"})
// 	void createOrder_ServiceError() throws Exception {
// 		Long userId = 1L;
// 		CreateOrderRequest request = new CreateOrderRequest(
// 			PaymentMethod.CREDIT_CARD,
// 			OrderChannel.ONLINE,
// 			ReceiptMethod.DELIVERY,
// 			"문 앞에 놓아주세요",
// 			10000L,
// 			"서울시 강남구"
// 		);
//
// 		when(orderService.createOrder(eq(userId), any(CreateOrderRequest.class)))
// 			.thenThrow(new GeneralException(ErrorStatus.ORDER_CREATE_FAILED));
//
// 		mockMvc.perform(post("/customer/order")
// 				.with(csrf())
// 				.contentType(MediaType.APPLICATION_JSON)
// 				.content(objectMapper.writeValueAsString(request)))
// 			.andExpect(status().isInternalServerError())
// 			.andExpect(jsonPath("$.resultCode").value("ORDER001"))
// 			.andExpect(jsonPath("$.message").value("주문 생성에 실패했습니다."));
// 	}
//
// 	@Test
// 	@DisplayName("주문 상세 조회 - 성공")
// 	@WithMockUser(username = "1", roles = {"CUSTOMER"})
// 	void getOrderDetail_Success() throws Exception {
// 		UUID orderId = UUID.randomUUID();
// 		OrderDetailResponse response = new OrderDetailResponse(
// 			"맛있는 치킨집",
// 			List.of(new OrderDetailResponse.Menu("후라이드 치킨", 2, 18000)),
// 			36000L,
// 			"서울시 강남구",
// 			PaymentMethod.CREDIT_CARD,
// 			OrderChannel.ONLINE,
// 			ReceiptMethod.DELIVERY,
// 			OrderStatus.PENDING,
// 			"문 앞에 놓아주세요"
// 		);
//
// 		when(orderService.getOrderDetail(orderId)).thenReturn(response);
//
// 		mockMvc.perform(get("/customer/order/{orderId}", orderId))
// 			.andExpect(status().isOk())
// 			.andExpect(jsonPath("$.resultCode").value("COMMON200"))
// 			.andExpect(jsonPath("$.message").value("success"))
// 			.andExpect(jsonPath("$.result.storeName").value("맛있는 치킨집"))
// 			.andExpect(jsonPath("$.result.menuList").isArray())
// 			.andExpect(jsonPath("$.result.menuList.length()").value(1))
// 			.andExpect(jsonPath("$.result.menuList[0].menuName").value("후라이드 치킨"))
// 			.andExpect(jsonPath("$.result.menuList[0].quantity").value(2))
// 			.andExpect(jsonPath("$.result.menuList[0].price").value(18000))
// 			.andExpect(jsonPath("$.result.totalPrice").value(36000))
// 			.andExpect(jsonPath("$.result.deliveryAddress").value("서울시 강남구"))
// 			.andExpect(jsonPath("$.result.paymentMethod").value("CREDIT_CARD"))
// 			.andExpect(jsonPath("$.result.orderChannel").value("ONLINE"))
// 			.andExpect(jsonPath("$.result.receiptMethod").value("DELIVERY"))
// 			.andExpect(jsonPath("$.result.orderStatus").value("PENDING"))
// 			.andExpect(jsonPath("$.result.requestMessage").value("문 앞에 놓아주세요"));
//
// 		verify(orderService).getOrderDetail(orderId);
// 	}
//
// 	@Test
// 	@DisplayName("주문 상세 조회 - 주문을 찾을 수 없음")
// 	@WithMockUser(username = "1", roles = {"CUSTOMER"})
// 	void getOrderDetail_OrderNotFound() throws Exception {
// 		UUID orderId = UUID.randomUUID();
//
// 		when(orderService.getOrderDetail(orderId))
// 			.thenThrow(new GeneralException(ErrorStatus.ORDER_NOT_FOUND));
//
// 		mockMvc.perform(get("/customer/order/{orderId}", orderId))
// 			.andExpect(status().isNotFound())
// 			.andExpect(jsonPath("$.resultCode").value("ORDER006"))
// 			.andExpect(jsonPath("$.message").value("주문을 찾을 수 없습니다."));
//
// 		verify(orderService).getOrderDetail(orderId);
// 	}
//
// 	@Test
// 	@DisplayName("주문 상세 조회 - 서버 에러")
// 	@WithMockUser(username = "1", roles = {"CUSTOMER"})
// 	void getOrderDetail_ServerError() throws Exception {
// 		UUID orderId = UUID.randomUUID();
//
// 		when(orderService.getOrderDetail(orderId))
// 			.thenThrow(new GeneralException(ErrorStatus._INTERNAL_SERVER_ERROR));
//
// 		mockMvc.perform(get("/customer/order/{orderId}", orderId))
// 			.andExpect(status().isInternalServerError())
// 			.andExpect(jsonPath("$.resultCode").value("COMMON500"))
// 			.andExpect(jsonPath("$.message").value("서버 에러, 관리자에게 문의 바랍니다."));
//
// 		verify(orderService).getOrderDetail(orderId);
// 	}
//
// 	@Test
// 	@DisplayName("주문 생성 - 잘못된 JSON 형식")
// 	@WithMockUser(username = "1", roles = {"CUSTOMER"})
// 	void createOrder_InvalidJson() throws Exception {
// 		String invalidJson = "{\"paymentMethod\": \"INVALID_METHOD\", \"totalPrice\": 10000}";
//
// 		mockMvc.perform(post("/customer/order")
// 				.with(csrf())
// 				.contentType(MediaType.APPLICATION_JSON)
// 				.content(invalidJson))
// 			.andExpect(status().isBadRequest());
//
// 		verify(orderService, never()).createOrder(any(), any());
// 	}
//
// 	@Test
// 	@DisplayName("필수 파라미터 누락 - paymentMethod 없음")
// 	@WithMockUser(username = "1", roles = {"CUSTOMER"})
// 	void createOrder_MissingPaymentMethod() throws Exception {
// 		String jsonWithoutPaymentMethod = "{\"orderChannel\": \"ONLINE\", \"receiptMethod\": \"DELIVERY\", \"totalPrice\": 10000, \"deliveryAddress\": \"서울시 강남구\"}";
//
// 		mockMvc.perform(post("/customer/order")
// 				.with(csrf())
// 				.contentType(MediaType.APPLICATION_JSON)
// 				.content(jsonWithoutPaymentMethod))
// 			.andExpect(status().isBadRequest())
// 			.andExpect(jsonPath("$.resultCode").value("COMMON400"))
// 			.andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
// 			.andExpect(jsonPath("$.result.paymentMethod").value("결제 방법은 필수입니다."));
//
// 		verify(orderService, never()).createOrder(any(), any());
// 	}
//
// 	@Test
// 	@DisplayName("필수 파라미터 누락 - orderChannel 없음")
// 	@WithMockUser(username = "1", roles = {"CUSTOMER"})
// 	void createOrder_MissingOrderChannel() throws Exception {
// 		String jsonWithoutOrderChannel = "{\"paymentMethod\": \"CREDIT_CARD\", \"receiptMethod\": \"DELIVERY\", \"totalPrice\": 10000, \"deliveryAddress\": \"서울시 강남구\"}";
//
// 		mockMvc.perform(post("/customer/order")
// 				.with(csrf())
// 				.contentType(MediaType.APPLICATION_JSON)
// 				.content(jsonWithoutOrderChannel))
// 			.andExpect(status().isBadRequest())
// 			.andExpect(jsonPath("$.resultCode").value("COMMON400"))
// 			.andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
// 			.andExpect(jsonPath("$.result.orderChannel").value("주문 채널은 필수입니다."));
//
// 		verify(orderService, never()).createOrder(any(), any());
// 	}
//
// 	@Test
// 	@DisplayName("필수 파라미터 누락 - receiptMethod 없음")
// 	@WithMockUser(username = "1", roles = {"CUSTOMER"})
// 	void createOrder_MissingReceiptMethod() throws Exception {
// 		String jsonWithoutReceiptMethod = "{\"paymentMethod\": \"CREDIT_CARD\", \"orderChannel\": \"ONLINE\", \"totalPrice\": 10000, \"deliveryAddress\": \"서울시 강남구\"}";
//
// 		mockMvc.perform(post("/customer/order")
// 				.with(csrf())
// 				.contentType(MediaType.APPLICATION_JSON)
// 				.content(jsonWithoutReceiptMethod))
// 			.andExpect(status().isBadRequest())
// 			.andExpect(jsonPath("$.resultCode").value("COMMON400"))
// 			.andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
// 			.andExpect(jsonPath("$.result.receiptMethod").value("수령 방법은 필수입니다."));
//
// 		verify(orderService, never()).createOrder(any(), any());
// 	}
//
// 	@Test
// 	@DisplayName("필수 파라미터 누락 - totalPrice 없음")
// 	@WithMockUser(username = "1", roles = {"CUSTOMER"})
// 	void createOrder_MissingTotalPrice() throws Exception {
// 		String jsonWithoutTotalPrice = "{\"paymentMethod\": \"CREDIT_CARD\", \"orderChannel\": \"ONLINE\", \"receiptMethod\": \"DELIVERY\", \"deliveryAddress\": \"서울시 강남구\"}";
//
// 		mockMvc.perform(post("/customer/order")
// 				.with(csrf())
// 				.contentType(MediaType.APPLICATION_JSON)
// 				.content(jsonWithoutTotalPrice))
// 			.andExpect(status().isBadRequest())
// 			.andExpect(jsonPath("$.resultCode").value("COMMON400"))
// 			.andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
// 			.andExpect(jsonPath("$.result.totalPrice").value("총 금액은 필수입니다."));
//
// 		verify(orderService, never()).createOrder(any(), any());
// 	}
//
// 	@Test
// 	@DisplayName("필수 파라미터 누락 - deliveryAddress 없음")
// 	@WithMockUser(username = "1", roles = {"CUSTOMER"})
// 	void createOrder_MissingDeliveryAddress() throws Exception {
// 		String jsonWithoutDeliveryAddress = "{\"paymentMethod\": \"CREDIT_CARD\", \"orderChannel\": \"ONLINE\", \"receiptMethod\": \"DELIVERY\", \"totalPrice\": 10000}";
//
// 		mockMvc.perform(post("/customer/order")
// 				.with(csrf())
// 				.contentType(MediaType.APPLICATION_JSON)
// 				.content(jsonWithoutDeliveryAddress))
// 			.andExpect(status().isBadRequest())
// 			.andExpect(jsonPath("$.resultCode").value("COMMON400"))
// 			.andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
// 			.andExpect(jsonPath("$.result.deliveryAddress").value("배송 주소는 필수입니다."));
//
// 		verify(orderService, never()).createOrder(any(), any());
// 	}
//
// 	@Test
// 	@DisplayName("주문 상세 조회 - 잘못된 UUID 형식")
// 	@WithMockUser(username = "1", roles = {"CUSTOMER"})
// 	void getOrderDetail_InvalidUUID() throws Exception {
// 		String invalidUUID = "invalid-uuid";
//
// 		mockMvc.perform(get("/customer/order/{orderId}", invalidUUID))
// 			.andExpect(status().isBadRequest());
//
// 		verify(orderService, never()).getOrderDetail(any());
// 	}
// }