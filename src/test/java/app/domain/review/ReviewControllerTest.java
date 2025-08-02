package app.domain.review;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
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

import app.domain.review.model.dto.request.CreateReviewRequest;
import app.domain.review.model.dto.response.GetReviewResponse;
import app.global.apiPayload.code.status.ErrorStatus;
import app.global.apiPayload.exception.GeneralException;
import app.global.config.MockSecurityConfig;

@WebMvcTest(ReviewController.class)
@Import({MockSecurityConfig.class})
@DisplayName("ReviewController 테스트")
class ReviewControllerTest {

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

	@Test
	@DisplayName("리뷰 생성 - 성공")
	@WithMockUser(username = "1", roles = "CUSTOMER")
	void createReview_Success() throws Exception {
		CreateReviewRequest request = new CreateReviewRequest(UUID.randomUUID(), 5L, "맛있어요");
		String resultMessage = UUID.randomUUID() + " 가 생성되었습니다.";

		when(reviewService.createReview(eq(1L), any(CreateReviewRequest.class)))
			.thenReturn(resultMessage);

		mockMvc.perform(post("/customer/review")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.resultCode").value("COMMON200"))
			.andExpect(jsonPath("$.message").value("success"))
			.andExpect(jsonPath("$.result").value(resultMessage));
	}

	@Test
	@DisplayName("리뷰 조회 - 성공")
	@WithMockUser(username = "1", roles = "CUSTOMER")
	void getReviews_Success() throws Exception {
		GetReviewResponse reviewResponse = new GetReviewResponse(
			UUID.randomUUID(),
			"testuser",
			"teststore",
			5L,
			"Great!",
			LocalDateTime.now()
		);
		List<GetReviewResponse> responseList = Collections.singletonList(reviewResponse);

		when(reviewService.getReviews(1L))
			.thenReturn(responseList);

		mockMvc.perform(get("/customer/review"))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.resultCode").value("COMMON200"))
			.andExpect(jsonPath("$.message").value("success"))
			.andExpect(jsonPath("$.result").isArray())
			.andExpect(jsonPath("$.result.length()").value(1))
			.andExpect(jsonPath("$.result[0].customerName").value("testuser"))
			.andExpect(jsonPath("$.result[0].storeName").value("teststore"))
			.andExpect(jsonPath("$.result[0].rating").value(5))
			.andExpect(jsonPath("$.result[0].content").value("Great!"));
	}

	@Test
	@DisplayName("리뷰 생성 - 실패 (서비스 예외)")
	@WithMockUser(username = "1", roles = "CUSTOMER")
	void createReview_Fail_ServiceException() throws Exception {
		CreateReviewRequest request = new CreateReviewRequest(UUID.randomUUID(), 5L, "맛있어요");
		when(reviewService.createReview(eq(1L), any(CreateReviewRequest.class)))
			.thenThrow(new GeneralException(ErrorStatus.REVIEW_ALREADY_EXISTS));

		mockMvc.perform(post("/customer/review")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andDo(print())
			.andExpect(status().isConflict())
			.andExpect(jsonPath("$.resultCode").value("REVIEW001"))
			.andExpect(jsonPath("$.message").value(ErrorStatus.REVIEW_ALREADY_EXISTS.getMessage()));
	}

	@Test
	@DisplayName("리뷰 조회 - 실패 (사용자 없음)")
	@WithMockUser(username = "999", roles = "CUSTOMER")
	void getReviews_Fail_UserNotFound() throws Exception {
		when(reviewService.getReviews(999L))
			.thenThrow(new GeneralException(ErrorStatus.USER_NOT_FOUND));

		mockMvc.perform(get("/customer/review"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.resultCode").value("USER001"))
			.andExpect(jsonPath("$.message").value(ErrorStatus.USER_NOT_FOUND.getMessage()));
	}

	@Test
	@DisplayName("리뷰 조회 - 실패 (리뷰 없음)")
	@WithMockUser(username = "1", roles = "CUSTOMER")
	void getReviews_Fail_NoReviewsFound() throws Exception {
		when(reviewService.getReviews(1L))
			.thenThrow(new GeneralException(ErrorStatus.NO_REVIEWS_FOUND_FOR_USER));

		mockMvc.perform(get("/customer/review"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.resultCode").value("REVIEW001"))
			.andExpect(jsonPath("$.message").value(ErrorStatus.NO_REVIEWS_FOUND_FOR_USER.getMessage()));
	}

	@Test
	@DisplayName("리뷰 생성 - 실패 (입력 검증 실패)")
	@WithMockUser(username = "1", roles = "CUSTOMER")
	void createReview_Fail_InvalidInput() throws Exception {
		CreateReviewRequest request = new CreateReviewRequest(null, null, "");

		mockMvc.perform(post("/customer/review")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.resultCode").value("COMMON400"));
	}
}