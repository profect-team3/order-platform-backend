package app.unit.domain.manager;

import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import app.domain.customer.dto.response.GetCustomerAddressListResponse;
import app.domain.customer.dto.response.GetStoreListResponse;
import app.domain.manager.ManagerController;
import app.domain.manager.ManagerService;
import app.domain.manager.dto.response.GetCustomerDetailResponse;
import app.domain.manager.dto.response.GetCustomerListResponse;
import app.domain.manager.dto.response.GetStoreDetailResponse;
import app.domain.order.model.dto.response.OrderDetailResponse;
import app.domain.order.model.entity.enums.OrderChannel;
import app.domain.order.model.entity.enums.OrderStatus;
import app.domain.order.model.entity.enums.PaymentMethod;
import app.domain.order.model.entity.enums.ReceiptMethod;
import app.domain.store.status.StoreAcceptStatus;
import app.global.apiPayload.PagedResponse;
import app.global.apiPayload.code.status.ErrorStatus;
import app.global.apiPayload.exception.GeneralException;
import app.global.config.MockSecurityConfig;

@WebMvcTest(ManagerController.class)
@Import({MockSecurityConfig.class})
class ManagerControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private ManagerService managerService;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
			.apply(springSecurity())
			.build();
	}

	@DisplayName("사용자 전체 조회 테스트")
	@WithMockUser(username = "1", roles = "MANAGER")
	@Test
	void getAllCustomerTest() throws Exception {
		// given
		List<GetCustomerListResponse> content = List.of(
			GetCustomerListResponse.builder()
				.id(2L)
				.email("te@naver.com")
				.name("김감자")
				.createdAt(LocalDateTime.parse("2025-07-29T15:32:11"))
				.build(),

			GetCustomerListResponse.builder()
				.id(1L)
				.email("test@example.com")
				.name("홍길동")
				.createdAt(LocalDateTime.parse("2025-07-28T17:18:29.971213"))
				.build()
		);
		PagedResponse<GetCustomerListResponse> response = new PagedResponse<>(content, 0, 20, 2, 1, true);

		// BDD 스타일로 변경
		given(managerService.getAllCustomer(any(Pageable.class))).willReturn(response);

		System.out.println("Mock response content size: " + response.getContent().size());

		// when & then
		mockMvc.perform(get("/manager/customer")
				.param("page", "0")
				.param("size", "20")
				.param("sort", "createdAt,desc")
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.result.content.length()").value(2))
			.andExpect(jsonPath("$.result.content[0].email").value("te@naver.com"))
			.andExpect(jsonPath("$.result.content[1].email").value("test@example.com"));
	}

	@DisplayName("사용자 상세 조회 API 테스트 - 주소 포함")
	@WithMockUser(username = "1", roles = "MANAGER")
	@Test
	void getUserDetailWithAddressTest() throws Exception {
		// given
		Long userId = 1L;

		GetCustomerAddressListResponse address = GetCustomerAddressListResponse.builder()
			.alias("집")
			.address("서울특별시 강남구 테헤란로")
			.addressDetail("101동 202호")
			.isDefault(true)
			.build();

		GetCustomerDetailResponse response = GetCustomerDetailResponse.builder()
			.userId(userId)
			.email("test@example.com")
			.userName("kkk7391")
			.name("aaa")
			.nickName("길동이")
			.phoneNumber("01012345678")
			.createdAt(LocalDateTime.now())
			.updatedAt(LocalDateTime.now())
			.address(List.of(address))
			.build();

		when(managerService.getCustomerDetailById(userId)).thenReturn(response);

		// when & then
		mockMvc.perform(get("/manager/customer/{userId}", userId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.result.userId").value(userId))
			.andExpect(jsonPath("$.result.email").value("test@example.com"))
			.andExpect(jsonPath("$.result.address[0].alias").value("집"))
			.andExpect(jsonPath("$.result.address[0].address").value("서울특별시 강남구 테헤란로"))
			.andExpect(jsonPath("$.result.address[0].addressDetail").value("101동 202호"))
			.andExpect(jsonPath("$.result.address[0].default").value(true));
	}

	@DisplayName("유저 주문 내역 조회 API 테스트")
	@WithMockUser(username = "1", roles = "MANAGER")
	@Test
	void getUserOrderListTest() throws Exception {
		// given
		Long userId = 1L;

		List<OrderDetailResponse.Menu> menus = List.of(
			new OrderDetailResponse.Menu("불고기 도시락", 2, 7000L),
			new OrderDetailResponse.Menu("치킨마요", 1, 8000L)
		);

		List<OrderDetailResponse> contents = List.of(
			new OrderDetailResponse(
				"한솥도시락",
				menus,
				22000L,
				"서울시 강남구",
				PaymentMethod.SIMPLE_PAY,
				OrderChannel.OFFLINE,
				ReceiptMethod.DELIVERY,
				OrderStatus.IN_DELIVERY,
				"문 앞에 두세요"
			)
		);

		PagedResponse<OrderDetailResponse> response =
			new PagedResponse<>(contents, 0, 20, 1, 1, true);

		when(managerService.getCustomerOrderListById(eq(userId), any(Pageable.class)))
			.thenReturn(response);

		// when & then
		mockMvc.perform(get("/manager/customer/{userId}/order", userId)
				.param("page", "0")
				.param("size", "20"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.result.content.length()").value(1))
			.andExpect(jsonPath("$.result.content[0].storeName").value("한솥도시락"))
			.andExpect(jsonPath("$.result.content[0].totalPrice").value(22000))
			.andExpect(jsonPath("$.result.content[0].deliveryAddress").value("서울시 강남구"))
			.andExpect(jsonPath("$.result.content[0].menuList.length()").value(2))
			.andExpect(jsonPath("$.result.content[0].menuList[0].menuName").value("불고기 도시락"))
			.andExpect(jsonPath("$.result.content[0].menuList[0].quantity").value(2))
			.andExpect(jsonPath("$.result.content[0].menuList[0].price").value(7000));
	}

	@DisplayName("사용자 검색 API 테스트")
	@WithMockUser(username = "1", roles = "MANAGER")
	@Test
	void searchCustomerTest() throws Exception {
		// given
		String keyword = "홍길동";
		Pageable pageable = PageRequest.of(0, 20);
		List<GetCustomerListResponse> content = List.of(
			GetCustomerListResponse.builder()
				.id(1L)
				.email("user1@example.com")
				.name("홍길동")
				.createdAt(LocalDateTime.now())
				.build()
		);
		PagedResponse<GetCustomerListResponse> response = new PagedResponse<>(content, 0, 20, 2, 1, true);

		when(managerService.searchCustomer(eq(keyword), any(Pageable.class))).thenReturn(response);

		// when & then
		mockMvc.perform(get("/manager/customer/search")
				.param("keyWord", keyword)
				.param("page", "0")
				.param("size", "20"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.result.content[0].name").value("홍길동"));
	}

	@DisplayName("존재하지 않는 사용자 조회 시 예외 테스트")
	@WithMockUser(username = "1", roles = "MANAGER")
	@Test
	void getUserDetail_NotFoundException_Test() throws Exception {
		// given
		Long invalidUserId = 999L;

		when(managerService.getCustomerDetailById(invalidUserId))
			.thenThrow(new GeneralException(ErrorStatus.USER_NOT_FOUND)); // 예외를 던짐

		// when & then
		mockMvc.perform(get("/manager/customer/{userId}", invalidUserId))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.isSuccess").value(false))
			.andExpect(jsonPath("$.code").value(ErrorStatus.USER_NOT_FOUND.getCode()))
			.andExpect(jsonPath("$.message").value(ErrorStatus.USER_NOT_FOUND.getMessage()));
	}


	@DisplayName("존재하지 않는 사용자 주문목록 조회 시 예외 테스트")
	@WithMockUser(username = "1", roles = "MANAGER")
	@Test
	void getCustomerOrderList_NotFoundException_Test() throws Exception {
		// given
		Long invalidUserId = 999L;

		when(managerService.getCustomerOrderListById(eq(invalidUserId), any(Pageable.class)))
			.thenThrow(new GeneralException(ErrorStatus.USER_NOT_FOUND));

		// when & then
		mockMvc.perform(get("/manager/customer/{userId}/order", invalidUserId)
				.param("page", "0")
				.param("size", "20"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.isSuccess").value(false))
			.andExpect(jsonPath("$.code").value(ErrorStatus.USER_NOT_FOUND.getCode()))
			.andExpect(jsonPath("$.message").value(ErrorStatus.USER_NOT_FOUND.getMessage()));
	}

	@Test
	@DisplayName("가게 리스트 조회 - 승인 상태로 필터링")
	@WithMockUser(username = "1", roles = "MANAGER")
	void testGetAllStoreWithStatus() throws Exception {
		// given
		PagedResponse<GetStoreListResponse> mockResponse =
			new PagedResponse<>(List.of(), 0, 0, 0, 0, true);

		when(managerService.getAllStore(eq(StoreAcceptStatus.PENDING), any(Pageable.class)))
			.thenReturn(mockResponse);

		// when & then
		mockMvc.perform(get("/manager/store")
				.param("status", "PENDING")
				.param("page", "0")
				.param("size", "20"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.result").exists());
	}

	@Test
	@DisplayName("가게 상세 조회")
	@WithMockUser(username = "1", roles = "MANAGER")
	void testGetStoreById() throws Exception {
		// given
		UUID storeId = UUID.randomUUID();

		GetStoreDetailResponse response = GetStoreDetailResponse.builder()
			.storeId(storeId)
			.storeName("감자탕 명가")
			.description("진한 국물의 감자탕")
			.address("서울시 종로구 종로1가")
			.phoneNumber("010-2222-3333")
			.minOrderAmount(15000L)
			.regionName("종로구")
			.categoryName("한식")
			.averageRating(4.3)
			.ownerId(2L)
			.ownerEmail("adfjf@naver")
			.ownerName("akdfj1234")
			.ownerRealName("김길동")
			.ownerPhone("010123434")
			.build();
		when(managerService.getStoreDetail(storeId)).thenReturn(response);

		// when & then
		mockMvc.perform(get("/manager/store/{storeId}", storeId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.result").exists());
	}

	@Test
	@DisplayName("가게 승인 처리")
	@WithMockUser(username = "1", roles = "MANAGER")
	void testApproveStore() throws Exception {
		// given
		UUID storeId = UUID.randomUUID();
		StoreAcceptStatus status = StoreAcceptStatus.APPROVE;
		String message = "가게 상태가 APPROVED 처리되었습니다.";

		when(managerService.approveStore(storeId, status)).thenReturn(message);

		// when & then
		mockMvc.perform(patch("/manager/store/{storeId}/accept", storeId)
				.param("status", status.name()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.result").value(message));
	}

}