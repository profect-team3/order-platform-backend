package app.global.config;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import app.domain.user.model.entity.enums.UserRole;
import app.global.jwt.JwtAccessDeniedHandler;
import app.global.jwt.JwtAuthenticationEntryPoint;
import app.global.jwt.JwtAuthenticationFilter;
import app.global.jwt.JwtTokenProvider;

@TestConfiguration
public class MockSecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf(csrf -> csrf.disable())
			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/customer/review/**").hasRole(UserRole.CUSTOMER.name())
				.requestMatchers("/customer/order/**").hasRole(UserRole.CUSTOMER.name())
				.requestMatchers("/customer/cart/**").hasRole(UserRole.CUSTOMER.name())
				.requestMatchers("/payment/**").hasRole(UserRole.CUSTOMER.name())
				.requestMatchers("/manager/**").hasRole(UserRole.MANAGER.name())
				.requestMatchers("/customer/**").hasRole(UserRole.CUSTOMER.name())
				.requestMatchers("/owner/**").hasRole(UserRole.OWNER.name())
				.requestMatchers("/user/signup", "/user/login").permitAll()
				.anyRequest().denyAll()

			);
		return http.build();
	}

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
	@SuppressWarnings("unchecked")
	public RedisTemplate<String, String> redisTemplate() {
		return Mockito.mock(RedisTemplate.class);
	}
}