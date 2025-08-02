package app.global.jwt;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import app.global.apiPayload.ApiResponse;
import app.global.apiPayload.code.status.ErrorStatus;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

// 필요한 권한 없이 접근 시 처리
@Component
@RequiredArgsConstructor
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

	private final ObjectMapper objectMapper;

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
		AccessDeniedException accessDeniedException) throws IOException {

		final ErrorStatus errorStatus = ErrorStatus._FORBIDDEN;

		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding("UTF-8");

		response.setStatus(errorStatus.getHttpStatus().value());

		ApiResponse<Object> errorResponse = ApiResponse.onFailure(errorStatus, null);

		objectMapper.writeValue(response.getWriter(), errorResponse);
	}
}
