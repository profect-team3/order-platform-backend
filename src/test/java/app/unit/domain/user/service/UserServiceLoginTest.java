package app.unit.domain.user.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.BDDMockito.anyLong;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;

import app.domain.user.UserService;
import app.domain.user.model.UserRepository;
import app.domain.user.model.dto.request.LoginRequest;
import app.domain.user.model.dto.response.LoginResponse;
import app.domain.user.model.entity.User;
import app.domain.user.status.UserErrorStatus;
import app.global.apiPayload.code.status.ErrorStatus;
import app.global.apiPayload.exception.GeneralException;
import app.global.jwt.JwtTokenProvider;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService 로그인 테스트")
class UserServiceLoginTest {

	@InjectMocks
	private UserService userService;

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private JwtTokenProvider jwtTokenProvider;

	@Mock
	private RedisTemplate<String, String> redisTemplate;

	@Mock
	private ValueOperations<String, String> valueOperations;

	private LoginRequest createLoginRequest(String username, String password) {
		LoginRequest request = new LoginRequest();
		request.setUsername(username);
		request.setPassword(password);
		return request;
	}

	@Nested
	@DisplayName("로그인 기능")
	class LoginTest {

		@Test
		@DisplayName("성공: 올바른 아이디와 비밀번호로 로그인 시, 토큰을 발급하고 Redis에 Refresh Token을 저장한다.")
		void login_Success() {
			// given
			LoginRequest request = createLoginRequest("testuser", "password123!");
			User mockUser = User.builder()
				.userId(1L)
				.username("testuser")
				.password("encodedPassword")
				.build();

			given(redisTemplate.opsForValue()).willReturn(valueOperations);

			given(userRepository.findByUsername(request.getUsername())).willReturn(Optional.of(mockUser));
			given(passwordEncoder.matches(request.getPassword(), mockUser.getPassword())).willReturn(true);
			given(jwtTokenProvider.createAccessToken(mockUser)).willReturn("dummy-access-token");
			given(jwtTokenProvider.createRefreshToken(mockUser)).willReturn("dummy-refresh-token");
			given(jwtTokenProvider.getRefreshTokenValidityInMilliseconds()).willReturn(1209600000L);

			// when
			LoginResponse response = userService.login(request);

			// then
			assertThat(response).isNotNull();
			assertThat(response.getAccessToken()).isEqualTo("dummy-access-token");
			assertThat(response.getRefreshToken()).isEqualTo("dummy-refresh-token");

			ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
			ArgumentCaptor<String> valueCaptor = ArgumentCaptor.forClass(String.class);
			ArgumentCaptor<Long> timeoutCaptor = ArgumentCaptor.forClass(Long.class);
			ArgumentCaptor<TimeUnit> timeUnitCaptor = ArgumentCaptor.forClass(TimeUnit.class);

			then(valueOperations).should().set(
				keyCaptor.capture(),
				valueCaptor.capture(),
				timeoutCaptor.capture(),
				timeUnitCaptor.capture()
			);

			// [검증 강화] 캡처된 값들이 예상과 일치하는지 개별적으로 검증
			assertThat(keyCaptor.getValue()).isEqualTo("RT:" + mockUser.getUserId());
			assertThat(valueCaptor.getValue()).isEqualTo("dummy-refresh-token");
			assertThat(timeoutCaptor.getValue()).isEqualTo(1209600000L);
			assertThat(timeUnitCaptor.getValue()).isEqualTo(TimeUnit.MILLISECONDS);
		}

		@Test
		@DisplayName("실패: 존재하지 않는 아이디로 로그인 시, GeneralException(USER_NOT_FOUND)을 던진다.")
		void login_Fail_UserNotFound() {
			// given
			LoginRequest request = createLoginRequest("nonexistentuser", "password123!");
			given(userRepository.findByUsername(request.getUsername())).willReturn(Optional.empty());

			// when & then
			assertThatThrownBy(() -> userService.login(request))
				.isInstanceOf(GeneralException.class)
				.extracting("code")
				.isEqualTo(ErrorStatus.USER_NOT_FOUND);

			then(redisTemplate).should(never()).opsForValue();
			then(passwordEncoder).should(never()).matches(anyString(), anyString());
			then(jwtTokenProvider).should(never()).createAccessToken(any(User.class));
		}

		@Test
		@DisplayName("실패: 비밀번호가 일치하지 않을 시, GeneralException(INVALID_PASSWORD)을 던진다.")
		void login_Fail_InvalidPassword() {
			// given
			LoginRequest request = createLoginRequest("testuser", "wrongpassword");
			User mockUser = User.builder()
				.userId(1L)
				.username("testuser")
				.password("encodedPassword")
				.build();

			given(userRepository.findByUsername(request.getUsername())).willReturn(Optional.of(mockUser));
			given(passwordEncoder.matches(request.getPassword(), mockUser.getPassword())).willReturn(false);

			// when & then
			assertThatThrownBy(() -> userService.login(request))
				.isInstanceOf(GeneralException.class)
				.extracting("code")
				.isEqualTo(UserErrorStatus.INVALID_PASSWORD);

			then(redisTemplate).should(never()).opsForValue();
			then(jwtTokenProvider).should(never()).createAccessToken(any(User.class));
			then(valueOperations).should(never()).set(anyString(), anyString(), anyLong(), any(TimeUnit.class));
		}
	}
}