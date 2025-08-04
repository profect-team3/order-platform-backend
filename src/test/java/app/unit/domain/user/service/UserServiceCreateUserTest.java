package app.unit.domain.user.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;

import app.domain.user.UserService;
import app.domain.user.model.UserRepository;
import app.domain.user.model.dto.request.CreateUserRequest;
import app.domain.user.model.dto.response.CreateUserResponse;
import app.domain.user.model.entity.User;
import app.domain.user.model.entity.enums.UserRole;
import app.domain.user.status.UserErrorStatus;
import app.global.apiPayload.exception.GeneralException;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService.createUser 테스트")
class UserServiceCreateUserTest {

	@Mock
	private UserRepository userRepository;
	@Mock
	private PasswordEncoder passwordEncoder;
	@InjectMocks
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

	private void givenNoDuplicatesFound(CreateUserRequest req) {
		given(userRepository.findFirstByUniqueFields(
			req.getUsername(),
			req.getEmail(),
			req.getNickname(),
			req.getPhoneNumber()
		)).willReturn(Optional.empty());
	}

	@Nested
	@DisplayName("성공 케이스")
	class SuccessCase {

		@Test
		@DisplayName("고객 계정 생성 성공")
		void createUser_ValidInput_Success() {
			// given
			CreateUserRequest req = createValidUserReq(UserRole.CUSTOMER);
			User user = User.builder()
				.userId(1L)
				.username(req.getUsername())
				.password("encodedPassword")
				.userRole(req.getUserRole())
				.build();

			givenNoDuplicatesFound(req);
			given(passwordEncoder.encode(req.getPassword())).willReturn("encodedPassword");
			given(userRepository.save(any(User.class))).willReturn(user);

			// when
			CreateUserResponse res = userService.createUser(req);

			// then
			ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
			verify(userRepository).save(userArgumentCaptor.capture());
			User capturedUser = userArgumentCaptor.getValue();

			assertThat(capturedUser.getUsername()).isEqualTo(req.getUsername());
			assertThat(capturedUser.getPassword()).isEqualTo("encodedPassword");
			assertThat(capturedUser.getEmail()).isEqualTo(req.getEmail());
			assertThat(capturedUser.getNickname()).isEqualTo(req.getNickname());
			assertThat(capturedUser.getUserRole()).isEqualTo(UserRole.CUSTOMER);
			assertThat(res.getUserId()).isEqualTo(1L);
		}

		@Test
		@DisplayName("점주 계정 생성 성공")
		void createOwner_Success() {
			// given
			CreateUserRequest req = createValidUserReq(UserRole.OWNER);
			User user = User.builder()
				.userId(2L)
				.username(req.getUsername())
				.password("encodedPassword")
				.userRole(req.getUserRole())
				.build();

			givenNoDuplicatesFound(req);
			given(passwordEncoder.encode(req.getPassword())).willReturn("encodedPassword");
			given(userRepository.save(any(User.class))).willReturn(user);

			// when
			CreateUserResponse res = userService.createUser(req);

			// then
			ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
			verify(userRepository).save(userArgumentCaptor.capture());
			User capturedUser = userArgumentCaptor.getValue();

			assertThat(capturedUser.getUserRole()).isEqualTo(UserRole.OWNER);
			assertThat(res.getUserId()).isEqualTo(2L);
		}
	}

	@Nested
	@DisplayName("중복 검증 실패")
	class ValidationFailure {

		@Test
		@DisplayName("중복된 아이디로 가입 시 예외 발생")
		void duplicateUsername_ThrowsException() {
			// given
			CreateUserRequest req = createValidUserReq(UserRole.CUSTOMER);

			User existingUser = User.builder().username(req.getUsername()).build();
			given(userRepository.findFirstByUniqueFields(
				req.getUsername(), req.getEmail(), req.getNickname(), req.getPhoneNumber()
			)).willReturn(Optional.of(existingUser));

			// when & then
			assertThatThrownBy(() -> userService.createUser(req))
				.isInstanceOf(GeneralException.class)
				.extracting("errorReasonHttpStatus.code")
				.isEqualTo(UserErrorStatus.USER_ALREADY_EXISTS.getCode());

			verify(userRepository, never()).save(any(User.class));
		}

		@Test
		@DisplayName("중복된 이메일로 가입 시 예외 발생")
		void duplicateEmail_ThrowsException() {
			// given
			CreateUserRequest req = createValidUserReq(UserRole.CUSTOMER);

			User existingUser = User.builder()
				.username("anotherUser")
				.email(req.getEmail())
				.build();

			given(userRepository.findFirstByUniqueFields(
				req.getUsername(), req.getEmail(), req.getNickname(), req.getPhoneNumber()
			)).willReturn(Optional.of(existingUser));

			// when & then
			assertThatThrownBy(() -> userService.createUser(req))
				.isInstanceOf(GeneralException.class)
				.extracting("errorReasonHttpStatus.code")
				.isEqualTo(UserErrorStatus.EMAIL_ALREADY_EXISTS.getCode());

			verify(userRepository, never()).save(any(User.class));
		}

		@Test
		@DisplayName("중복된 닉네임으로 가입 시 예외 발생")
		void duplicateNickname_ThrowsException() {
			// given
			CreateUserRequest req = createValidUserReq(UserRole.CUSTOMER);

			User existingUser = User.builder()
				.username("anotherUser")
				.email("another@example.com")
				.nickname(req.getNickname())
				.build();

			given(userRepository.findFirstByUniqueFields(
				req.getUsername(), req.getEmail(), req.getNickname(), req.getPhoneNumber()
			)).willReturn(Optional.of(existingUser));

			// when & then
			assertThatThrownBy(() -> userService.createUser(req))
				.isInstanceOf(GeneralException.class)
				.extracting("errorReasonHttpStatus.code")
				.isEqualTo(UserErrorStatus.NICKNAME_ALREADY_EXISTS.getCode());

			verify(userRepository, never()).save(any(User.class));
		}

		@Test
		@DisplayName("중복된 휴대폰 번호로 가입 시 예외 발생")
		void duplicatePhoneNumber_ThrowsException() {
			// given
			CreateUserRequest req = createValidUserReq(UserRole.CUSTOMER);

			User existingUser = User.builder()
				.username("anotherUser")
				.email("another@example.com")
				.nickname("anotherNickname")
				.phoneNumber(req.getPhoneNumber())
				.build();

			given(userRepository.findFirstByUniqueFields(
				req.getUsername(), req.getEmail(), req.getNickname(), req.getPhoneNumber()
			)).willReturn(Optional.of(existingUser));

			// when & then
			assertThatThrownBy(() -> userService.createUser(req))
				.isInstanceOf(GeneralException.class)
				.extracting("errorReasonHttpStatus.code")
				.isEqualTo(UserErrorStatus.PHONE_NUMBER_ALREADY_EXISTS.getCode());

			verify(userRepository, never()).save(any(User.class));
		}
	}

	@Nested
	@DisplayName("시스템 예외 상황")
	class SystemException {

		@Test
		@DisplayName("데이터베이스 저장 실패 시 예외 발생")
		void databaseError_OnSave_ThrowsException() {
			// given
			CreateUserRequest req = createValidUserReq(UserRole.CUSTOMER);

			givenNoDuplicatesFound(req);
			given(userRepository.save(any(User.class))).willThrow(new DataAccessException("DB connection failed") {
			});

			// when & then
			assertThatThrownBy(() -> userService.createUser(req))
				.isInstanceOf(GeneralException.class)
				.extracting("errorReasonHttpStatus.code")
				.isEqualTo(app.global.apiPayload.code.status.ErrorStatus._INTERNAL_SERVER_ERROR.getCode());
		}

		@Test
		@DisplayName("비밀번호 인코딩 실패 시 예외 발생")
		void passwordEncoding_Fail_ThrowsException() {
			// given
			CreateUserRequest req = createValidUserReq(UserRole.CUSTOMER);

			givenNoDuplicatesFound(req);
			given(passwordEncoder.encode(req.getPassword())).willThrow(new RuntimeException("Encoding failed"));

			// when & then
			assertThatThrownBy(() -> userService.createUser(req))
				.isInstanceOf(RuntimeException.class)
				.hasMessage("Encoding failed");

			verify(userRepository, never()).save(any(User.class));
		}
	}
}