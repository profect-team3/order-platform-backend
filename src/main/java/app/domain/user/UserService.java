package app.domain.user;

import java.util.concurrent.TimeUnit;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import app.domain.cart.model.entity.Cart;
import app.domain.cart.model.repository.CartRepository;
import app.domain.user.model.UserRepository;
import app.domain.user.model.dto.request.CreateUserRequest;
import app.domain.user.model.dto.request.LoginRequest;
import app.domain.user.model.dto.response.CreateUserResponse;
import app.domain.user.model.dto.response.LoginResponse;
import app.domain.user.model.entity.User;
import app.domain.user.status.UserErrorStatus;
import app.global.apiPayload.code.status.ErrorStatus;
import app.global.apiPayload.exception.GeneralException;
import app.global.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class UserService {

	private final UserRepository userRepository;
	private final CartRepository cartRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenProvider jwtTokenProvider;
	private final RedisTemplate<String, String> redisTemplate;
	private static final String REFRESH_TOKEN_PREFIX = "RT:";
	private static final String BLACKLIST_PREFIX = "BL:";

	@Transactional
	public CreateUserResponse createUser(CreateUserRequest createUserRequest) {

		validateUserUniqueness(createUserRequest);

		String encryptedPassword = passwordEncoder.encode(createUserRequest.getPassword());

		User user = User.builder()
			.username(createUserRequest.getUsername())
			.password(encryptedPassword)
			.email(createUserRequest.getEmail())
			.nickname(createUserRequest.getNickname())
			.realName(createUserRequest.getRealName())
			.phoneNumber(createUserRequest.getPhoneNumber())
			.userRole(createUserRequest.getUserRole())
			.build();

		try {
			User savedUser = userRepository.save(user);
			cartRepository.save(Cart.builder().user(savedUser).build());
			return CreateUserResponse.from(savedUser);
		} catch (DataAccessException e) {
			log.error("데이터베이스에 사용자 등록을 실패했습니다.", e);
			throw new GeneralException(ErrorStatus._INTERNAL_SERVER_ERROR);
		}
	}

	@Transactional
	public LoginResponse login(LoginRequest request) {
		User user = userRepository.findByUsername(request.getUsername())
			.orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			throw new GeneralException(UserErrorStatus.INVALID_PASSWORD);
		}

		String accessToken = jwtTokenProvider.createAccessToken(user);
		String refreshToken = jwtTokenProvider.createRefreshToken(user);

		redisTemplate.opsForValue().set(
			REFRESH_TOKEN_PREFIX + user.getUserId(),
			refreshToken,
			jwtTokenProvider.getRefreshTokenValidityInMilliseconds(),
			TimeUnit.MILLISECONDS
		);

		return LoginResponse.builder()
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.build();
	}

	@Transactional
	public void logout() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		String userId = authentication.getName();
		String accessToken = (String)authentication.getCredentials();

		String refreshTokenKey = REFRESH_TOKEN_PREFIX + userId;
		if (Boolean.TRUE.equals(redisTemplate.hasKey(refreshTokenKey))) {
			redisTemplate.delete(refreshTokenKey);
		}

		Long expiration = jwtTokenProvider.getExpiration(accessToken);
		if (expiration > 0) {
			redisTemplate.opsForValue().set(
				BLACKLIST_PREFIX + accessToken,
				"logout",
				expiration,
				TimeUnit.MILLISECONDS
			);
		}
	}

	@Transactional
	public void withdrawMembership() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Long currentUserId = Long.parseLong(authentication.getName());

		User user = userRepository.findById(currentUserId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

		user.anonymizeForWithdrawal();

		userRepository.delete(user);

		logout();
	}

	/**
	 * 사용자의 고유 필드(아이디, 이메일, 닉네임, 전화번호) 중복 여부 검사 - 회원가입에서만 사용
	 * @param createUserRequest 회원가입 요청 DTO
	 */
	private void validateUserUniqueness(CreateUserRequest createUserRequest) {
		userRepository.findFirstByUniqueFields(
			createUserRequest.getUsername(),
			createUserRequest.getEmail(),
			createUserRequest.getNickname(),
			createUserRequest.getPhoneNumber()
		).ifPresent(user -> {
			if (user.getUsername().equals(createUserRequest.getUsername())) {
				throw new GeneralException(UserErrorStatus.USER_ALREADY_EXISTS);
			}
			if (user.getEmail().equals(createUserRequest.getEmail())) {
				throw new GeneralException(UserErrorStatus.EMAIL_ALREADY_EXISTS);
			}
			if (user.getNickname().equals(createUserRequest.getNickname())) {
				throw new GeneralException(UserErrorStatus.NICKNAME_ALREADY_EXISTS);
			}
			if (user.getPhoneNumber().equals(createUserRequest.getPhoneNumber())) {
				throw new GeneralException(UserErrorStatus.PHONE_NUMBER_ALREADY_EXISTS);
			}
		});
	}
}