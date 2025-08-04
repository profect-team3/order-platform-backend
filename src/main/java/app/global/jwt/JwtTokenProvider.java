package app.global.jwt;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import app.domain.user.model.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtTokenProvider implements InitializingBean {

	private static final String AUTHORITIES_KEY = "auth";

	private final String secret;
	private final long accessTokenValidityInMilliseconds;
	private final long refreshTokenValidityInMilliseconds;

	private SecretKey key;

	public JwtTokenProvider(
		@Value("${jwt.secret}") String secret,
		@Value("${jwt.access-token-validity-in-milliseconds}") long accessTokenValidityInMilliseconds,
		@Value("${jwt.refresh-token-validity-in-milliseconds}") long refreshTokenValidityInMilliseconds) {
		this.secret = secret;
		this.accessTokenValidityInMilliseconds = accessTokenValidityInMilliseconds;
		this.refreshTokenValidityInMilliseconds = refreshTokenValidityInMilliseconds;
	}

	/**
	 * 1. application.yml에서 주입받은 secret 값을 Base64 디코딩하여 Key 변수에 할당
	 */
	@Override
	public void afterPropertiesSet() {
		byte[] keyBytes = Decoders.BASE64.decode(secret);
		this.key = Keys.hmacShaKeyFor(keyBytes);
	}

	/**
	 * 2. User 정보를 가지고 AccessToken, RefreshToken을 생성하는 메서드
	 */
	public String createAccessToken(User user) {
		long now = (new Date()).getTime();
		Date accessTokenExpiresIn = new Date(now + this.accessTokenValidityInMilliseconds);

		return Jwts.builder()
			.subject(String.valueOf(user.getUserId())) // 토큰 주체 (사용자 ID)
			.claim(AUTHORITIES_KEY, user.getUserRole().name()) // 사용자 권한
			.expiration(accessTokenExpiresIn) // 만료 시간
			.signWith(key) // 서명 키
			.compact();
	}

	public String createRefreshToken(User user) {
		long now = (new Date()).getTime();
		Date refreshTokenExpiresIn = new Date(now + this.refreshTokenValidityInMilliseconds);

		return Jwts.builder()
			.subject(String.valueOf(user.getUserId()))
			.expiration(refreshTokenExpiresIn)
			.signWith(key)
			.compact();
	}

	/**
	 * 3. Jwt 토큰을 복호화해 토큰에 들어있는 정보를 꺼내는 메서드
	 */
	public Authentication getAuthentication(String accessToken) {
		// 토큰 복호화
		Claims claims = getClaims(accessToken);

		// 클레임에서 권한 정보 가져오기
		Collection<? extends GrantedAuthority> authorities =
			Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
				.map(SimpleGrantedAuthority::new)
				.collect(Collectors.toList());

		// UserDetails 객체를 만들어서 Authentication 리턴
		org.springframework.security.core.userdetails.User principal = new org.springframework.security.core.userdetails.User(
			claims.getSubject(), "", authorities);

		return new UsernamePasswordAuthenticationToken(principal, accessToken, authorities);
	}

	/**
	 * 4. 토큰 정보를 검증하는 메서드
	 */
	public boolean validateToken(String token) {
		try {
			Jwts.parser().verifyWith(key).build().parse(token);
			return true;
		} catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
			log.info("잘못된 JWT 서명입니다.");
		} catch (ExpiredJwtException e) {
			log.info("만료된 JWT 토큰입니다.");
		} catch (UnsupportedJwtException e) {
			log.info("지원되지 않는 JWT 토큰입니다.");
		} catch (IllegalArgumentException e) {
			log.info("JWT 토큰이 잘못되었습니다.");
		}
		return false;
	}

	// JWT 내부를 안전하게 열어보는 역할(내부 helper)
	private Claims getClaims(String accessToken) {
		return Jwts.parser()
			.verifyWith(key)
			.build()
			.parseSignedClaims(accessToken)
			.getPayload();
	}

	public long getRefreshTokenValidityInMilliseconds() {
		return refreshTokenValidityInMilliseconds;
	}

	public Long getExpiration(String accessToken) {
		// accessToken 남은 유효시간
		Date expiration = getClaims(accessToken).getExpiration();
		// 현재 시간
		long now = new Date().getTime();
		return (expiration.getTime() - now);
	}
}