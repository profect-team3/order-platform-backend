package app.unit.domain.user.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;

import app.domain.user.UserController;
import app.domain.user.UserService;
import app.domain.user.model.dto.request.CreateUserRequest;
import app.domain.user.model.dto.response.CreateUserResponse;
import app.domain.user.model.entity.enums.UserRole;
import app.domain.user.status.UserErrorStatus;
import app.global.apiPayload.exception.GeneralException;
import app.global.config.MockSecurityConfig;

@WebMvcTest(controllers = UserController.class)
@Import(MockSecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("UserController 테스트")
class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private UserService userService;

	private CreateUserRequest createValidUserReq(UserRole role) {
		CreateUserRequest req = new CreateUserRequest();
		req.setUsername("testuser");
		req.setPassword("password123!");
		req.setEmail("test@example.com");
		req.setNickname("testnick");
		req.setRealName("김테스트");
		req.setPhoneNumber("01012345678");
		req.setUserRole(role);
		return req;
	}

	@Nested
	@DisplayName("회원가입 API [/user/signup] 테스트")
	class CreateUserTest {

		@Test
		@DisplayName("성공: 유효한 정보로 회원가입을 요청하면 201 Created와 생성된 사용자 정보를 반환한다.")
		void createUser_Success() throws Exception {
			// given
			CreateUserRequest req = createValidUserReq(UserRole.CUSTOMER);
			CreateUserResponse mockResponse = CreateUserResponse.builder()
				.userId(1L)
				.build();
			given(userService.createUser(any(CreateUserRequest.class))).willReturn(mockResponse);

			// when
			ResultActions resultActions = mockMvc.perform(post("/user/signup")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req)));

			// then
			resultActions
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.result.userId").value(mockResponse.getUserId()))
				.andDo(print());
		}

		@Test
		@DisplayName("실패(유효성 검증): 아이디가 누락된 요청은 400 Bad Request를 반환한다.")
		void createUser_Fail_Validation() throws Exception {
			// given
			CreateUserRequest req = createValidUserReq(UserRole.CUSTOMER);
			req.setUsername(" ");

			// when
			ResultActions resultActions = mockMvc.perform(post("/user/signup") // 올바른 API 경로
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req)));

			// then
			resultActions
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.isSuccess").value(false))
				.andExpect(
					jsonPath("$.code").value(app.global.apiPayload.code.status.ErrorStatus._BAD_REQUEST.getCode()))
				.andExpect(jsonPath("$.message").value(
					app.global.apiPayload.code.status.ErrorStatus._BAD_REQUEST.getMessage()))
				.andExpect(jsonPath("$.result.username").exists()) // 유효성 검증 실패 필드 확인
				.andDo(print());
		}

		@Test
		@DisplayName("실패(비즈니스 로직): 이미 존재하는 아이디로 회원가입을 요청하면 409 Conflict를 반환한다.")
		void createUser_Fail_DuplicateUsername() throws Exception {
			// given
			CreateUserRequest req = createValidUserReq(UserRole.CUSTOMER);
			// 💡 서비스가 GeneralException을 던지도록 설정합니다.
			given(userService.createUser(any(CreateUserRequest.class)))
				.willThrow(new GeneralException(UserErrorStatus.USER_ALREADY_EXISTS));

			// when
			ResultActions resultActions = mockMvc.perform(post("/user/signup") // 올바른 API 경로
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(req)));

			// then
			resultActions
				.andExpect(status().isConflict()) // HTTP 409 Conflict 검증
				.andExpect(jsonPath("$.isSuccess").value(false))
				.andExpect(
					jsonPath("$.code").value(UserErrorStatus.USER_ALREADY_EXISTS.getCode())) // 'resultCode' -> 'code'
				.andExpect(jsonPath("$.message").value(UserErrorStatus.USER_ALREADY_EXISTS.getMessage()))
				.andExpect(jsonPath("$.result").doesNotExist()) // 실패 시 result는 없음
				.andDo(print());
		}
	}
}