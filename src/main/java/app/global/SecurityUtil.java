package app.global;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import app.domain.user.model.UserRepository;
import app.domain.user.model.entity.User;
import app.global.apiPayload.code.status.ErrorStatus;
import app.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SecurityUtil {

	private final UserRepository userRepository;

	/**
	 * 현재 인증된 사용자의 User 엔티티를 반환합니다.
	 * @return 현재 사용자 User 엔티티
	 * @throws GeneralException 인증 정보가 없거나 사용자를 찾을 수 없는 경우
	 */
	public User getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(
			authentication.getPrincipal())) {
			throw new GeneralException(ErrorStatus._UNAUTHORIZED);
		}

		Long userId = Long.parseLong(authentication.getName());
		return userRepository.findById(userId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
	}
}
