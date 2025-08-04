package app.unit.domain.cart;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import app.domain.cart.CartController;
import app.domain.cart.model.dto.AddCartItemRequest;
import app.domain.cart.model.dto.RedisCartItem;
import app.domain.cart.service.CartService;
import app.domain.cart.status.CartErrorStatus;
import app.global.apiPayload.exception.GeneralException;
import app.global.config.MockSecurityConfig;

@WebMvcTest(CartController.class)
@Import({MockSecurityConfig.class})
@DisplayName("CartController 테스트")
class CartControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private WebApplicationContext context;

	@MockitoBean
	private CartService cartService;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders
			.webAppContextSetup(context)
			.apply(springSecurity())
			.build();
	}

	@Test
	@DisplayName("장바구니 아이템 추가 - 성공")
	@WithMockUser(username = "1", roles = "CUSTOMER")
	void addItemToCart_Success() throws Exception {
		Long userId = 1L;
		UUID menuId = UUID.randomUUID();
		UUID storeId = UUID.randomUUID();
		AddCartItemRequest request = new AddCartItemRequest(menuId, storeId, 2);
		String resultMessage = "사용자 1의 장바구니가 성공적으로 저장되었습니다.";

		when(cartService.addCartItem(any(AddCartItemRequest.class)))
			.thenReturn(resultMessage);

		mockMvc.perform(post("/customer/cart/item")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value("COMMON200"))
			.andExpect(jsonPath("$.message").value("success"))
			.andExpect(jsonPath("$.result").exists())
			.andExpect(jsonPath("$.result").value(resultMessage));

	}

	@Test
	@DisplayName("장바구니 아이템 추가 - 수량 0 이하 실패")
	@WithMockUser(username = "1", roles = {"CUSTOMER"})
	void addItemToCart_InvalidQuantity() throws Exception {
		UUID menuId = UUID.randomUUID();
		UUID storeId = UUID.randomUUID();
		AddCartItemRequest request = new AddCartItemRequest(menuId, storeId, 0);

		mockMvc.perform(post("/customer/cart/item")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value("COMMON400"))
			.andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
			.andExpect(jsonPath("$.result.quantity").value("수량은 1 이상이어야 합니다."));

		verify(cartService, never()).addCartItem(any());

	}

	@Test
	@DisplayName("장바구니 아이템 추가 - 서비스 에러")
	@WithMockUser(username = "1", roles = {"CUSTOMER"})
	void addItemToCart_ServiceError() throws Exception {
		Long userId = 1L;
		UUID menuId = UUID.randomUUID();
		UUID storeId = UUID.randomUUID();
		AddCartItemRequest request = new AddCartItemRequest(menuId, storeId, 2);

		when(cartService.addCartItem(any(AddCartItemRequest.class)))
			.thenThrow(new GeneralException(CartErrorStatus.CART_REDIS_SAVE_FAILED));

		mockMvc.perform(post("/customer/cart/item")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isInternalServerError())
			.andExpect(jsonPath("$.code").value("CART001"))
			.andExpect(jsonPath("$.message").value("장바구니 Redis 저장에 실패했습니다."));
	}

	@Test
	@DisplayName("장바구니 아이템 수량 수정 - 성공")
	@WithMockUser(username = "1", roles = {"CUSTOMER"})
	void updateItemInCart_Success() throws Exception {
		Long userId = 1L;
		UUID menuId = UUID.randomUUID();
		int quantity = 5;

		when(cartService.updateCartItem(any(UUID.class), anyInt()))
			.thenReturn("사용자 1의 장바구니가 성공적으로 저장되었습니다.");

		mockMvc.perform(patch("/customer/cart/item/{menuId}/{quantity}", menuId, quantity)
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value("COMMON200"))
			.andExpect(jsonPath("$.message").value("success"));

		verify(cartService).updateCartItem(menuId, quantity);
	}

	@Test
	@DisplayName("장바구니 아이템 삭제 - 성공")
	@WithMockUser(username = "1", roles = {"CUSTOMER"})
	void removeItemFromCart_Success() throws Exception {
		Long userId = 1L;
		UUID menuId = UUID.randomUUID();

		when(cartService.removeCartItem(any(UUID.class)))
			.thenReturn("사용자 1의 장바구니에서 메뉴가 성공적으로 삭제되었습니다.");

		mockMvc.perform(delete("/customer/cart/item/{menuId}", menuId)
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value("COMMON200"))
			.andExpect(jsonPath("$.message").value("success"));

		verify(cartService).removeCartItem(menuId);
	}

	@Test
	@DisplayName("장바구니 조회 - 성공")
	@WithMockUser(username = "1", roles = {"CUSTOMER"})
	void getCart_Success() throws Exception {
		Long userId = 1L;
		UUID menuId1 = UUID.randomUUID();
		UUID menuId2 = UUID.randomUUID();
		UUID storeId = UUID.randomUUID();

		List<RedisCartItem> cartItems = List.of(
			RedisCartItem.builder().menuId(menuId1).storeId(storeId).quantity(2).build(),
			RedisCartItem.builder().menuId(menuId2).storeId(storeId).quantity(1).build()
		);

		when(cartService.getCartFromCache()).thenReturn(cartItems);

		mockMvc.perform(get("/customer/cart"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value("COMMON200"))
			.andExpect(jsonPath("$.message").value("success"))
			.andExpect(jsonPath("$.result").isArray())
			.andExpect(jsonPath("$.result.length()").value(2))
			.andExpect(jsonPath("$.result[0].menuId").value(menuId1.toString()))
			.andExpect(jsonPath("$.result[0].quantity").value(2))
			.andExpect(jsonPath("$.result[1].menuId").value(menuId2.toString()))
			.andExpect(jsonPath("$.result[1].quantity").value(1));

		verify(cartService).getCartFromCache();
	}

	@Test
	@DisplayName("장바구니 조회 - 빈 장바구니")
	@WithMockUser(username = "1", roles = {"CUSTOMER"})
	void getCart_Empty() throws Exception {
		Long userId = 1L;

		when(cartService.getCartFromCache()).thenReturn(List.of());

		mockMvc.perform(get("/customer/cart"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.resultCode").value("COMMON200"))
			.andExpect(jsonPath("$.message").value("success"))
			.andExpect(jsonPath("$.result").isArray())
			.andExpect(jsonPath("$.result.length()").value(0));

		verify(cartService).getCartFromCache();
	}

	@Test
	@DisplayName("장바구니 조회 - 서비스 에러")
	@WithMockUser(username = "1", roles = {"CUSTOMER"})
	void getCart_ServiceError() throws Exception {
		Long userId = 1L;

		when(cartService.getCartFromCache())
			.thenThrow(new GeneralException(CartErrorStatus.CART_REDIS_LOAD_FAILED));

		mockMvc.perform(get("/customer/cart"))
			.andExpect(status().isInternalServerError())
			.andExpect(jsonPath("$.code").value("CART002"))
			.andExpect(jsonPath("$.message").value("장바구니 Redis 조회에 실패했습니다."));
	}

	@Test
	@DisplayName("장바구니 전체 삭제 - 성공")
	@WithMockUser(username = "1", roles = {"CUSTOMER"})
	void clearCart_Success() throws Exception {
		Long userId = 1L;

		when(cartService.clearCartItems())
			.thenReturn("사용자 1의 장바구니가 성공적으로 비워졌습니다.");

		mockMvc.perform(delete("/customer/cart/item")
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value("COMMON200"))
			.andExpect(jsonPath("$.message").value("success"));

		verify(cartService).clearCartItems();
	}

	@Test
	@DisplayName("장바구니 전체 삭제 - 서비스 에러")
	@WithMockUser(username = "1", roles = {"CUSTOMER"})
	void clearCart_ServiceError() throws Exception {
		Long userId = 1L;

		when(cartService.clearCartItems())
			.thenThrow(new GeneralException(CartErrorStatus.CART_REDIS_SAVE_FAILED));

		mockMvc.perform(delete("/customer/cart/item")
				.with(csrf()))
			.andExpect(status().isInternalServerError())
			.andExpect(jsonPath("$.code").value("CART001"))
			.andExpect(jsonPath("$.message").value("장바구니 Redis 저장에 실패했습니다."));
	}

	@Test
	@DisplayName("잘못된 JSON 형식 - 요청 바디 매핑 실패")
	@WithMockUser(username = "1", roles = {"CUSTOMER"})
	void addItemToCart_InvalidJson() throws Exception {
		Long userId = 1L;
		String invalidJson = "{\"menuId\": \"invalid-uuid\", \"storeId\": \"valid-uuid\", \"quantity\": 2}";

		mockMvc.perform(post("/customer/cart/item")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(invalidJson))
			.andExpect(status().isBadRequest());

		verify(cartService, never()).addCartItem(any());
	}

	@Test
	@DisplayName("필수 파라미터 누락 - menuId 없음")
	@WithMockUser(username = "1", roles = {"CUSTOMER"})
	void addItemToCart_MissingMenuId() throws Exception {
		String jsonWithoutMenuId = "{\"storeId\": \"" + UUID.randomUUID() + "\", \"quantity\": 2}";

		mockMvc.perform(post("/customer/cart/item")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonWithoutMenuId))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value("COMMON400"))
			.andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
			.andExpect(jsonPath("$.result.menuId").value("메뉴 ID는 필수입니다."));

		verify(cartService, never()).addCartItem(any());
	}

	@Test
	@DisplayName("필수 파라미터 누락 - storeId 없음")
	@WithMockUser(username = "1", roles = {"CUSTOMER"})
	void addItemToCart_MissingStoreId() throws Exception {
		String jsonWithoutStoreId = "{\"menuId\": \"" + UUID.randomUUID() + "\", \"quantity\": 2}";

		mockMvc.perform(post("/customer/cart/item")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonWithoutStoreId))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value("COMMON400"))
			.andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
			.andExpect(jsonPath("$.result.storeId").value("매장 ID는 필수입니다."));

		verify(cartService, never()).addCartItem(any());
	}

	@Test
	@DisplayName("필수 파라미터 누락 - quantity 없음")
	@WithMockUser(username = "1", roles = {"CUSTOMER"})
	void addItemToCart_MissingQuantity() throws Exception {
		String jsonWithoutQuantity =
			"{\"menuId\": \"" + UUID.randomUUID() + "\", \"storeId\": \"" + UUID.randomUUID() + "\"}";

		mockMvc.perform(post("/customer/cart/item")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonWithoutQuantity))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value("COMMON400"))
			.andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
			.andExpect(jsonPath("$.result.quantity").value("수량은 필수입니다."));

		verify(cartService, never()).addCartItem(any());
	}
}