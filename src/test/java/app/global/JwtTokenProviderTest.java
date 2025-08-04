package app.global;

import static org.assertj.core.api.Assertions.*;

import java.util.Collection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import app.domain.user.model.entity.User;
import app.domain.user.model.entity.enums.UserRole;
import app.global.jwt.JwtTokenProvider;

@DisplayName("JwtTokenProvider 단위 테스트")
class JwtTokenProviderTest {

	private JwtTokenProvider jwtTokenProvider;

	// 테스트용으로 사용할 고정된 secret key와 유효 기간
	private final String testSecret = "V29vTmVvUmVhbGx5TG9uZ0FuZFNlY3JldEtleUZvckpXVFNpZ25pbmdXaGljaElzQmFzZTY0RW5jb2RlZA==";
	private final long accessTokenValidity = 3600000L; // 1 hour
	private final long refreshTokenValidity = 1209600000L; // 14 days

	private User testUser;

	@BeforeEach
	void setUp() {
		// Spring 컨텍스트 없이 테스트 대상 클래스를 직접 생성합니다.
		jwtTokenProvider = new JwtTokenProvider(testSecret, accessTokenValidity, refreshTokenValidity);
		// afterPropertiesSet()을 수동으로 호출하여 secret key로부터 암호화 키(SecretKey)를 생성합니다.
		jwtTokenProvider.afterPropertiesSet();

		// 테스트에 사용할 User 객체를 생성합니다.
		// User 엔티티에 userId 필드가 있다고 가정합니다.
		testUser = User.builder()
			.userId(1L)
			.username("testuser")
			.userRole(UserRole.CUSTOMER)
			.build();
	}

	@Test
	@DisplayName("성공: 사용자 정보로 Access Token과 Refresh Token을 생성한다.")
	void createTokens_Success() {
		// when
		String accessToken = jwtTokenProvider.createAccessToken(testUser);
		String refreshToken = jwtTokenProvider.createRefreshToken(testUser);

		// --- [확인] 생성된 토큰을 눈으로 확인 ---
		System.out.println("\n--- [Test: createTokens_Success] ---");
		System.out.println("Generated Access Token: " + accessToken);
		System.out.println("Generated Refresh Token: " + refreshToken);
		System.out.println("------------------------------------");

		// then
		assertThat(accessToken).isNotNull().isNotEmpty();
		assertThat(refreshToken).isNotNull().isNotEmpty();
	}

	@Test
	@DisplayName("성공: 유효한 토큰을 검증하고 Authentication 객체를 생성한다.")
	void getAuthentication_ValidToken_Success() {
		// given
		String accessToken = jwtTokenProvider.createAccessToken(testUser);

		// when: 토큰의 유효성을 검증하고, 토큰으로부터 Authentication 객체를 가져옵니다.
		boolean isValid = jwtTokenProvider.validateToken(accessToken);
		Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);

		// --- [확인] 검증 과정의 값들을 눈으로 확인 ---
		System.out.println("\n--- [Test: getAuthentication_ValidToken_Success] ---");
		System.out.println("Token to validate: " + accessToken);
		System.out.println("Expected User ID: " + testUser.getUserId());
		System.out.println("Expected Role: " + UserRole.CUSTOMER.name());
		System.out.println("------------------------------------");
		System.out.println("Is Token Valid? " + isValid);
		System.out.println("Extracted User ID (Principal Name): " + authentication.getName());
		System.out.println("Extracted Authorities: " + authentication.getAuthorities());
		System.out.println("------------------------------------");

		// then: 토큰이 유효하며, Authentication 객체에 사용자 정보가 올바르게 담겨있는지 확인합니다.
		assertThat(isValid).isTrue();
		assertThat(authentication).isNotNull();

		// Authentication의 'name'은 토큰의 subject, 즉 사용자 ID와 일치해야 합니다.
		assertThat(authentication.getName()).isEqualTo(String.valueOf(testUser.getUserId()));

		// Authentication의 'authorities'는 토큰에 담긴 사용자 권한과 일치해야 합니다.
		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		assertThat(authorities).hasSize(1);
		assertThat(authorities.iterator().next().getAuthority()).isEqualTo(UserRole.CUSTOMER.name());
	}

	@Nested
	@DisplayName("토큰 유효성 검증 실패 케이스")
	class TokenValidationFailure {

		@Test
		@DisplayName("실패: 만료된 토큰은 검증에 실패한다.")
		void validate_ExpiredToken_Fails() {
			// given: 유효 기간이 0초인 토큰 프로바이더를 만들어 만료된 토큰을 생성합니다.
			JwtTokenProvider expiredTokenProvider = new JwtTokenProvider(testSecret, 0, 0);
			expiredTokenProvider.afterPropertiesSet();
			String expiredToken = expiredTokenProvider.createAccessToken(testUser);

			// --- [확인] 만료된 토큰을 눈으로 확인 ---
			System.out.println("\n--- [Test: validate_ExpiredToken_Fails] ---");
			System.out.println("Expired Token (generated with 0ms validity): " + expiredToken);
			System.out.println("------------------------------------");

			// when
			boolean isValid = jwtTokenProvider.validateToken(expiredToken);

			// then
			assertThat(isValid).isFalse();
		}

		@Test
		@DisplayName("실패: 잘못된 서명을 가진 토큰은 검증에 실패한다.")
		void validate_InvalidSignature_Fails() {
			// given: 다른 secret key를 사용하는 토큰 프로바이더로 토큰을 생성합니다.
			String anotherSecret = "VGhpcyBpcyBhIHRvdGFsbHkgZGlmZmVyZW50IHNlY3JldCBmb3IgdGVzdGluZyBwdXJwb3Nlcw==";
			JwtTokenProvider anotherProvider = new JwtTokenProvider(anotherSecret, accessTokenValidity,
				refreshTokenValidity);
			anotherProvider.afterPropertiesSet();
			String tokenWithWrongSignature = anotherProvider.createAccessToken(testUser);

			// --- [확인] 잘못된 서명을 가진 토큰을 눈으로 확인 ---
			System.out.println("\n--- [Test: validate_InvalidSignature_Fails] ---");
			System.out.println("Token with wrong signature: " + tokenWithWrongSignature);
			System.out.println("This token was signed with a different secret key and should fail validation.");
			System.out.println("------------------------------------");

			// when: 원래 프로바이더로 검증을 시도합니다.
			boolean isValid = jwtTokenProvider.validateToken(tokenWithWrongSignature);

			// then
			assertThat(isValid).isFalse();
		}

		@Test
		@DisplayName("실패: 잘못된 형식의 토큰은 검증에 실패한다.")
		void validate_MalformedToken_Fails() {
			// given
			String malformedToken = "this-is-not-a-jwt-token";

			// --- [확인] 잘못된 형식의 토큰을 눈으로 확인 ---
			System.out.println("\n--- [Test: validate_MalformedToken_Fails] ---");
			System.out.println("Malformed Token String: " + malformedToken);
			System.out.println("This is not a valid JWT structure and should fail validation.");
			System.out.println("------------------------------------");

			// when
			boolean isValid = jwtTokenProvider.validateToken(malformedToken);

			// then
			assertThat(isValid).isFalse();
		}
	}
}