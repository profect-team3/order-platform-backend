package app.unit.domain.customer;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
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

import app.domain.customer.CustomerAddressController;
import app.domain.customer.CustomerAddressService;
import app.domain.customer.dto.request.AddCustomerAddressRequest;
import app.domain.customer.dto.response.AddCustomerAddressResponse;
import app.domain.customer.dto.response.GetCustomerAddressListResponse;
import app.domain.customer.status.CustomerErrorStatus;
import app.domain.customer.status.CustomerSuccessStatus;
import app.global.apiPayload.exception.GeneralException;
import app.global.config.MockSecurityConfig;

@WebMvcTest(CustomerAddressController.class)
@Import({MockSecurityConfig.class})
@DisplayName("CustomerAddressController 테스트")
class CustomerAddressControllerTest {

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private CustomerAddressService customerAddressService;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders
			.webAppContextSetup(context)
			.apply(springSecurity())
			.build();
	}

	@Test
	@DisplayName("주소 목록 조회 - 성공")
	@WithMockUser(username = "1", roles = "CUSTOMER")
	void getCustomerAddresses_Success() throws Exception {
		List<GetCustomerAddressListResponse> addressResponse = List.of(
			new GetCustomerAddressListResponse(
				"우리집",
				"서울시 강남구",
				"101호",
				true
			),
			new GetCustomerAddressListResponse(
				"회사",
				"서울시 서초구",
				"202호",
				false
			)
		);

		when(customerAddressService.getCustomerAddresses()).thenReturn(addressResponse);

		mockMvc.perform(get("/customer/address/list"))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.code").value(CustomerSuccessStatus.ADDRESS_LIST_FOUND.getCode()))
			.andExpect(jsonPath("$.message").value(CustomerSuccessStatus.ADDRESS_LIST_FOUND.getMessage()))
			.andExpect(jsonPath("$.result").isArray())
			.andExpect(jsonPath("$.result.length()").value(2))
			.andExpect(jsonPath("$.result[0].alias").value("우리집"))
			.andExpect(jsonPath("$.result[0].address").value("서울시 강남구"))
			.andExpect(jsonPath("$.result[0].addressDetail").value("101호"))
			.andExpect(jsonPath("$.result[0].isDefault").value(true))
			.andExpect(jsonPath("$.result[1].alias").value("회사"))
			.andExpect(jsonPath("$.result[1].address").value("서울시 서초구"))
			.andExpect(jsonPath("$.result[1].addressDetail").value("202호"))
			.andExpect(jsonPath("$.result[1].isDefault").value(false));
	}

	@Test
	@DisplayName("주소 목록 조회 - 실패 (DB 조회 실패)")
	@WithMockUser(username = "1", roles = "CUSTOMER")
	void getCustomerAddresses_Fail_InternalServiceException() throws Exception {
		when(customerAddressService.getCustomerAddresses())
			.thenThrow(new GeneralException(CustomerErrorStatus.ADDRESS_READ_FAILED));

		mockMvc.perform(get("/customer/address/list"))
			.andExpect(status().isInternalServerError())
			.andExpect(jsonPath("$.code").value(CustomerErrorStatus.ADDRESS_READ_FAILED.getCode()))
			.andExpect(jsonPath("$.message").value(CustomerErrorStatus.ADDRESS_READ_FAILED.getMessage()));
	}

	@Test
	@DisplayName("주소 등록 - 성공")
	@WithMockUser(username = "1", roles = "CUSTOMER")
	void addCustomerAddresses_Success() throws Exception {
		AddCustomerAddressRequest request = new AddCustomerAddressRequest(
			"새로운 집",
			"서울시 종로구",
			"303호",
			false
		);

		UUID newAddressId = UUID.randomUUID();
		AddCustomerAddressResponse mockResponse = new AddCustomerAddressResponse(newAddressId);

		when(customerAddressService.addCustomerAddress(any(AddCustomerAddressRequest.class)))
			.thenReturn(mockResponse);

		mockMvc.perform(post("/customer/address/add")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.code").value(CustomerSuccessStatus.ADDRESS_ADDED.getCode()))
			.andExpect(jsonPath("$.message").value(CustomerSuccessStatus.ADDRESS_ADDED.getMessage()))
			.andExpect(jsonPath("$.result.address_id").value(newAddressId.toString()));
	}

	@Test
	@DisplayName("주소 등록 - 실패 (주소 중복)")
	@WithMockUser(username = "1", roles = "CUSTOMER")
	void addCustomerAddresses_Fail_ServiceException() throws Exception {
		AddCustomerAddressRequest request = new AddCustomerAddressRequest(
			"새로운 집",
			"서울시 종로구",
			"303호",
			false
		);

		when(customerAddressService.addCustomerAddress(any(AddCustomerAddressRequest.class)))
			.thenThrow(new GeneralException(CustomerErrorStatus.ADDRESS_ALREADY_EXISTS));

		mockMvc.perform(post("/customer/address/add")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isConflict())
			.andExpect(jsonPath("$.code").value(CustomerErrorStatus.ADDRESS_ALREADY_EXISTS.getCode()))
			.andExpect(jsonPath("$.message").value(CustomerErrorStatus.ADDRESS_ALREADY_EXISTS.getMessage()));
	}

	@Test
	@DisplayName("주소 등록 조회 - 실패 (DB 조회 실패)")
	@WithMockUser(username = "1", roles = "CUSTOMER")
	void addCustomerAddresses_Fail_InternalServiceException() throws Exception {
		AddCustomerAddressRequest request = new AddCustomerAddressRequest(
			"새로운 집",
			"서울시 종로구",
			"303호",
			false
		);

		when(customerAddressService.addCustomerAddress(any(AddCustomerAddressRequest.class)))
			.thenThrow(new GeneralException(CustomerErrorStatus.ADDRESS_ADD_FAILED));

		mockMvc.perform(post("/customer/address/add")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isInternalServerError())
			.andExpect(jsonPath("$.code").value(CustomerErrorStatus.ADDRESS_ADD_FAILED.getCode()))
			.andExpect(jsonPath("$.message").value(CustomerErrorStatus.ADDRESS_ADD_FAILED.getMessage()));
	}

	@ParameterizedTest(name = "실패 - alias가 \"{0}\"일 때 (수동 검증)")
	@NullAndEmptySource
	@ValueSource(strings = {" "})
	@WithMockUser(username = "1", roles = "CUSTOMER")
	void addCustomerAddresses_Fail_InvalidAlias(String invalidAlias) throws Exception {
		AddCustomerAddressRequest request = new AddCustomerAddressRequest(invalidAlias, "서울시 종로구", "303호", false);

		mockMvc.perform(post("/customer/address/add")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value(CustomerErrorStatus.ADDRESS_ALIAS_INVALID.getCode()));
	}

	@ParameterizedTest(name = "실패 - address가 \"{0}\"일 때 (수동 검증)")
	@NullAndEmptySource
	@ValueSource(strings = {" "})
	@WithMockUser(username = "1", roles = "CUSTOMER")
	void addCustomerAddresses_Fail_InvalidAddress(String invalidAddress) throws Exception {
		AddCustomerAddressRequest request = new AddCustomerAddressRequest("우리 집", invalidAddress, "303호", false);

		mockMvc.perform(post("/customer/address/add")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value(CustomerErrorStatus.ADDRESS_ADDRESS_INVALID.getCode()));
	}

	@ParameterizedTest(name = "실패 - addressDetail이 \"{0}\"일 때 (수동 검증)")
	@NullAndEmptySource
	@ValueSource(strings = {" "})
	@WithMockUser(username = "1", roles = "CUSTOMER")
	void addCustomerAddresses_Fail_InvalidAddressDetail(String invalidAddressDetail) throws Exception {
		AddCustomerAddressRequest request = new AddCustomerAddressRequest("우리 집", "서울시 종로구", invalidAddressDetail,
			false);

		mockMvc.perform(post("/customer/address/add")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value(CustomerErrorStatus.ADDRESS_ADDRESSDETAIL_INVALID.getCode()));
	}
}