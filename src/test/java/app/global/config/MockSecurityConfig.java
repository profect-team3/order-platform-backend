package app.global.config;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import app.global.jwt.JwtAccessDeniedHandler;
import app.global.jwt.JwtAuthenticationEntryPoint;
import app.global.jwt.JwtAuthenticationFilter;
import app.global.jwt.JwtTokenProvider;

@TestConfiguration
public class MockSecurityConfig {

	@Bean
	public JwtTokenProvider jwtTokenProvider() {
		return Mockito.mock(JwtTokenProvider.class);
	}

	@Bean
	public JwtAuthenticationFilter jwtAuthenticationFilter() {
		return Mockito.mock(JwtAuthenticationFilter.class);
	}

	@Bean
	public JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint() {
		return Mockito.mock(JwtAuthenticationEntryPoint.class);
	}

	@Bean
	public JwtAccessDeniedHandler jwtAccessDeniedHandler() {
		return Mockito.mock(JwtAccessDeniedHandler.class);
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			// 1. CSRF 설정을 람다 스타일로 변경
			.csrf(AbstractHttpConfigurer::disable)
			// 2. HTTP 요청 인가 설정을 람다 스타일로 변경
			.authorizeHttpRequests(authorize -> authorize
				.requestMatchers("/user/signup", "/user/login").permitAll() // 허용할 경로들
				.anyRequest().denyAll() // 나머지는 모두 거부
			);
		return http.build();
	}

	@Bean
	@SuppressWarnings("unchecked")
	public RedisTemplate<String, String> redisTemplate() {
		return Mockito.mock(RedisTemplate.class);
	}
}