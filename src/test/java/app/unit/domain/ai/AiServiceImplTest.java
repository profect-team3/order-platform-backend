package app.unit.domain.ai;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import app.domain.ai.AiService;
import app.domain.ai.AiServiceImpl;
import app.domain.ai.model.AiHistoryRepository;
import app.domain.ai.model.dto.request.AiRequest;
import app.domain.ai.model.dto.response.AiResponse;
import app.domain.ai.model.entity.AiHistory;
import app.domain.ai.model.entity.enums.AiRequestStatus;
import app.domain.ai.model.entity.enums.ReqType;
import app.domain.ai.status.AiErrorStatus;
import app.global.apiPayload.exception.GeneralException;

@DisplayName("AiService 테스트")
class AiServiceImplTest {

	@Nested
	@DisplayName("단위 테스트 (Mockito)")
	@ExtendWith(MockitoExtension.class)
	class AiServiceUnitTest {

		@InjectMocks
		private AiServiceImpl aiService;

		@Mock
		private AiHistoryRepository aiHistoryRepository;

		@Captor
		private ArgumentCaptor<String> promptTextCaptor;
		@Captor
		private ArgumentCaptor<AiHistory> aiHistoryCaptor;
		@Captor
		private ArgumentCaptor<ChatOptions> chatOptionsCaptor;

		@Mock
		private ChatClient chatClient;
		@Mock
		private ChatClient.ChatClientRequestSpec chatClientRequestSpec;
		@Mock
		private ChatClient.CallResponseSpec callResponseSpec;

		private AiHistory savedHistory;

		@BeforeEach
		void setUp() {

			savedHistory = mock(AiHistory.class);
			lenient().when(savedHistory.getAiRequestId()).thenReturn(UUID.randomUUID());

			lenient().when(aiHistoryRepository.save(any(AiHistory.class))).thenReturn(savedHistory);

			lenient().when(chatClient.prompt()).thenReturn(chatClientRequestSpec);
			lenient().when(chatClientRequestSpec.user(anyString())).thenReturn(chatClientRequestSpec);
			lenient().when(chatClientRequestSpec.options(any(ChatOptions.class))).thenReturn(chatClientRequestSpec);
			lenient().when(chatClientRequestSpec.call()).thenReturn(callResponseSpec);
		}

		@Test
		@DisplayName("필수 입력값 storeName 누락 시 예외 발생 확인")
		void givenMissingStoreName_whenGenerateDescription_thenThrowsException() {
			AiRequest invalidRequest = new AiRequest(
				null,
				"반반 족발",
				ReqType.MENU_DESCRIPTION,
				"쫄깃하고 부드러운 식감을 강조해주세요."
			);

			assertThatThrownBy(() -> aiService.generateDescription(invalidRequest))
				.isInstanceOf(GeneralException.class)
				.hasFieldOrPropertyWithValue("code", AiErrorStatus.AI_INVALID_INPUT_VALUE);

			verify(chatClient, never()).prompt();
			verify(aiHistoryRepository, never()).save(any(AiHistory.class));
		}

		@Test
		@DisplayName("요청 종류가 MENU_DESCRIPTION일 때 메뉴 이름 누락 시 예외 발생 확인")
		void givenMissingMenuNameForMenuDescription_whenGenerateDescription_thenThrowsException() {
			AiRequest invalidRequest = new AiRequest(
				"맛있는 족발집",
				null,
				ReqType.MENU_DESCRIPTION,
				"쫄깃하고 부드러운 식감을 강조해주세요."
			);

			assertThatThrownBy(() -> aiService.generateDescription(invalidRequest))
				.isInstanceOf(GeneralException.class)
				.hasFieldOrPropertyWithValue("code", AiErrorStatus.AI_INVALID_INPUT_VALUE);

			verify(chatClient, never()).prompt();
			verify(aiHistoryRepository, never()).save(any(AiHistory.class));
		}

		@Test
		@DisplayName("AI 요청 시 DB에 PENDING 상태로 저장되는지 확인")
		void givenAiRequest_whenGenerateDescription_thenHistorySavedAsPending() {
			AiRequest aiRequest = new AiRequest(
				"맛있는 족발집",
				"반반 족발",
				ReqType.MENU_DESCRIPTION,
				"쫄깃하고 부드러운 식감을 강조해주세요."
			);
			String expectedContent = "AI 응답";
			when(callResponseSpec.content()).thenReturn(expectedContent);

			aiService.generateDescription(aiRequest);

			verify(aiHistoryRepository, times(1)).save(aiHistoryCaptor.capture());
			assertEquals(AiRequestStatus.PENDING, aiHistoryCaptor.getValue().getStatus());
			assertEquals("맛있는 족발집", aiHistoryCaptor.getValue().getStoreName());
		}

		@Test
		@DisplayName("AI 모델에 올바른 프롬프트가 전달되는지 확인")
		void givenAiRequest_whenGenerateDescription_thenCorrectPromptIsSentToAiModel() {
			AiRequest aiRequest = new AiRequest(
				"맛있는 족발집",
				"반반 족발",
				ReqType.MENU_DESCRIPTION,
				"쫄깃하고 부드러운 식감을 강조해주세요."
			);
			String expectedContent = "AI 응답";
			when(callResponseSpec.content()).thenReturn(expectedContent);

			aiService.generateDescription(aiRequest);

			verify(chatClientRequestSpec, times(1)).user(promptTextCaptor.capture());
			String capturedPrompt = promptTextCaptor.getValue();

			assertTrue(capturedPrompt.contains(aiRequest.getStoreName()));
			assertTrue(capturedPrompt.contains(aiRequest.getMenuName()));
			assertTrue(capturedPrompt.contains(aiRequest.getPromptText()));

			verify(aiHistoryRepository, times(1)).save(aiHistoryCaptor.capture());
			assertEquals(AiRequestStatus.PENDING, aiHistoryCaptor.getValue().getStatus());
		}

		@Test
		@DisplayName("AI 응답 시 ChatOptions가 올바르게 전달되는지 확인")
		void givenAiRequest_whenGenerateDescription_thenChatOptionsAreCorrectlySet() {
			AiRequest aiRequest = new AiRequest(
				"맛있는 족발집",
				"반반 족발",
				ReqType.MENU_DESCRIPTION,
				"쫄깃하고 부드러운 식감을 강조해주세요."
			);
			String expectedContent = "AI 응답";
			when(callResponseSpec.content()).thenReturn(expectedContent);

			aiService.generateDescription(aiRequest);

			verify(chatClientRequestSpec, times(1)).options(chatOptionsCaptor.capture());
			ChatOptions capturedOptions = chatOptionsCaptor.getValue();

			assertNotNull(capturedOptions, "ChatOptions 객체가 ChatClient에 전달되어야 합니다.");

			verify(aiHistoryRepository, times(1)).save(aiHistoryCaptor.capture());
			assertEquals(AiRequestStatus.PENDING, aiHistoryCaptor.getValue().getStatus());
		}

		@Test
		@DisplayName("AI 응답 성공 시 응답 내용과 DB 상태가 올바르게 업데이트되는지 확인")
		void givenSuccessfulAiResponse_whenGenerateDescription_thenResponseAndHistoryAreCorrect() {
			AiRequest aiRequest = new AiRequest(
				"맛있는 족발집",
				"반반 족발",
				ReqType.MENU_DESCRIPTION,
				"쫄깃하고 부드러운 식감을 강조해주세요."
			);
			String expectedContent = "쫄깃함과 부드러움이 공존하는 환상의 맛! 저희 가게 대표 메뉴 반반 족발입니다.";
			when(callResponseSpec.content()).thenReturn(expectedContent);

			doAnswer(invocation -> {
				when(savedHistory.getStatus()).thenReturn(AiRequestStatus.SUCCESS);
				when(savedHistory.getGeneratedContent()).thenReturn(invocation.getArgument(0));
				return null;
			}).when(savedHistory).updateGeneratedContent(anyString(), eq(AiRequestStatus.SUCCESS));

			AiResponse response = aiService.generateDescription(aiRequest);

			assertNotNull(response);
			assertEquals(expectedContent, response.getGeneratedContent());

			verify(aiHistoryRepository, times(1)).save(aiHistoryCaptor.capture());
			verify(savedHistory).updateGeneratedContent(expectedContent, AiRequestStatus.SUCCESS);

			assertEquals(AiRequestStatus.SUCCESS, savedHistory.getStatus());
			assertEquals(expectedContent, savedHistory.getGeneratedContent());
		}

		@Test
		@DisplayName("AI 응답 실패 시 예외 발생 및 DB 상태가 FAILED로 업데이트되는지 확인")
		void givenFailedAiResponse_whenGenerateDescription_thenThrowsExceptionAndHistoryIsFailed() {
			AiRequest aiRequest = new AiRequest(
				"맛있는 족발집",
				"반반 족발",
				ReqType.MENU_DESCRIPTION,
				"쫄깃하고 부드러운 식감을 강조해주세요."
			);
			RuntimeException aiCallException = new RuntimeException("AI 모델 호출 실패");
			String errorMessage = "Error: " + aiCallException.getMessage();
			when(callResponseSpec.content()).thenThrow(aiCallException);

			doAnswer(invocation -> {
				when(savedHistory.getStatus()).thenReturn(AiRequestStatus.FAILED);
				when(savedHistory.getGeneratedContent()).thenReturn(invocation.getArgument(0));
				return null;
			}).when(savedHistory).updateGeneratedContent(anyString(), eq(AiRequestStatus.FAILED));

			assertThrows(GeneralException.class, () -> aiService.generateDescription(aiRequest));

			verify(aiHistoryRepository, times(1)).save(aiHistoryCaptor.capture());
			verify(savedHistory).updateGeneratedContent(errorMessage, AiRequestStatus.FAILED);

			assertEquals(AiRequestStatus.FAILED, savedHistory.getStatus());
			assertTrue(savedHistory.getGeneratedContent().contains("Error: AI 모델 호출 실패"));
		}
	}

	@Nested
	@DisplayName("통합 테스트 (@SpringBootTest)")
	@SpringBootTest
	@ActiveProfiles("test")
	class AiServiceIntegrationTest {

		@Autowired
		private AiService aiService;

		@Autowired
		private AiHistoryRepository aiHistoryRepository;

		@Test
		@DisplayName("AI 응답 연동 및 DB 저장 확인")
		@Transactional
		void givenRealAiRequest_whenGenerateDescription_thenHistorySavedAsSuccess() {
			AiRequest realRequest = new AiRequest(
				"미스터피자",
				"고구마 피자",
				ReqType.MENU_DESCRIPTION,
				"달콤하고 부드러운 점을 강조해서 30자 이내로 간략한 상품 설명을 작성해줘"
			);
			AiResponse aiResponse = aiService.generateDescription(realRequest);

			assertNotNull(aiResponse.getRequestId(), "응답으로 받은 ID가 null이 아니어야 합니다.");
			assertNotNull(aiResponse.getGeneratedContent(), "생성된 내용이 null이 아니어야 합니다.");
			assertFalse(aiResponse.getGeneratedContent().isEmpty(), "생성된 내용이 비어있지 않아야 합니다.");
			System.out.println("AI 생성 결과: " + aiResponse.getGeneratedContent());

			AiHistory savedHistory = aiHistoryRepository.findById(UUID.fromString(aiResponse.getRequestId()))
				.orElse(null);

			assertNotNull(savedHistory, "DB에 해당 ID의 기록이 저장되어 있어야 합니다.");
			assertEquals(AiRequestStatus.SUCCESS, savedHistory.getStatus(), "최종 상태는 SUCCESS여야 합니다.");
			assertEquals(aiResponse.getGeneratedContent(), savedHistory.getGeneratedContent(),
				"응답 내용과 DB에 저장된 내용이 같아야 합니다.");
		}
	}
}
