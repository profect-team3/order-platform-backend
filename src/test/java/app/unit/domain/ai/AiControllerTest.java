package app.unit.domain.ai;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import app.domain.ai.AiController;
import app.domain.ai.AiService;
import app.domain.ai.model.dto.request.AiRequest;
import app.domain.ai.model.dto.response.AiResponse;
import app.domain.ai.model.entity.enums.ReqType;
import app.domain.review.ReviewService;
import app.global.config.MockSecurityConfig;

@WebMvcTest(AiController.class)
@Import({MockSecurityConfig.class})
@DisplayName("AiController 테스트")
class AiControllerTest {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private WebApplicationContext context;

	@MockitoBean
	private ReviewService reviewService;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders
			.webAppContextSetup(context)
			.apply(springSecurity())
			.build();
	}

	@MockitoBean
	private AiService aiService;

	@Test
	@DisplayName("AI 생성 요청 성공")
	@WithMockUser(username = "1", roles = "OWNER")
	void givenValidRequest_whenGenerateDescription_thenReturnsSuccess() throws Exception {
		AiRequest request =
			new AiRequest("맛있는 족발집", "반반 족발", ReqType.MENU_DESCRIPTION, "쫄깃하고 부드러운 식감을 강조해주세요.");
		AiResponse response = new AiResponse(UUID.randomUUID().toString(), "서울시 최고의 반반 족발을 느껴보세요.");
		given(aiService.generateDescription(any(AiRequest.class))).willReturn(response);

		mockMvc
			.perform(
				post("/owner/ai/generate")
					.with(csrf())
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value("AI201"))
			.andExpect(jsonPath("$.message").value("AI 응답 생성이 성공했습니다."))
			.andExpect(jsonPath("$.result.requestId").value(response.getRequestId()))
			.andExpect(jsonPath("$.result.generatedContent").value(response.getGeneratedContent()));
	}

	@Test
	@DisplayName("AI 생성 요청 시 입력값이 유효하지 않으면 실패")
	@WithMockUser(username = "1", roles = "OWNER")
	void givenInvalidRequest_whenGenerateDescription_thenReturnsFailure() throws Exception {
		AiRequest invalidRequest =
			new AiRequest(null, "반반 족발", ReqType.MENU_DESCRIPTION, "쫄깃하고 부드러운 식감을 강조해주세요.");

		mockMvc
			.perform(
				post("/owner/ai/generate")
					.with(csrf())
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(invalidRequest)))
			.andExpect(status().isBadRequest());
	}
}
