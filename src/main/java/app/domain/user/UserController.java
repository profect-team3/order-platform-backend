package app.domain.user;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import app.domain.user.model.dto.request.CreateUserRequest;
import app.domain.user.model.dto.request.LoginRequest;
import app.domain.user.model.dto.response.CreateUserResponse;
import app.domain.user.model.dto.response.LoginResponse;
import app.domain.user.status.UserSuccessStatus;
import app.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "User", description = "로그인, 회원가입")
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

	private final UserService userService;

	@Operation(summary = "회원가입 API", description = "새로운 사용자를 등록합니다. 모든 필드는 유효성 검사를 거칩니다.")
	@PostMapping("/signup")
	public ApiResponse<CreateUserResponse> createUser(@Valid @RequestBody CreateUserRequest createUserRequest) {
		CreateUserResponse response = userService.createUser(createUserRequest);
		return ApiResponse.onSuccess(UserSuccessStatus.USER_CREATED, response);
	}

	@PostMapping("/login")
	@Operation(summary = "로그인 API", description = "아이디와 비밀번호로 로그인하여 토큰을 발급받습니다.")
	public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
		LoginResponse response = userService.login(request);
		return ApiResponse.onSuccess(UserSuccessStatus.LOGIN_SUCCESS, response);
	}

	@PostMapping("/logout")
	@Operation(summary = "로그아웃 API", description = "서버에 저장된 Refresh Token을 삭제하고 현재 Access Token을 비활성화 처리합니다.")
	public ApiResponse<Void> logout() {
		userService.logout();
		return ApiResponse.onSuccess(UserSuccessStatus.LOGOUT_SUCCESS, null);
	}

	@DeleteMapping("/withdraw")
	@Operation(summary = "회원 탈퇴 API", description = "현재 로그인된 사용자의 계정을 비활성화하고 개인정보를 익명화 처리합니다.")
	public ApiResponse<Void> withdraw() {
		userService.withdrawMembership();
		return ApiResponse.onSuccess(UserSuccessStatus.WITHDRAW_SUCCESS, null);
	}
}