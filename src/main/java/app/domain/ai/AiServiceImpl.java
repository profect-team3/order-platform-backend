package app.domain.ai;

import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import app.domain.ai.model.AiHistoryRepository;
import app.domain.ai.model.dto.request.AiRequest;
import app.domain.ai.model.dto.response.AiResponse;
import app.domain.ai.model.entity.AiHistory;
import app.domain.ai.model.entity.enums.AiRequestStatus;
import app.domain.ai.model.entity.enums.ReqType;
import app.domain.ai.status.AiErrorStatus;
import app.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AiServiceImpl implements AiService {

	private final AiHistoryRepository aiHistoryRepository;
	private final ChatClient chatClient;

	@Override
	public AiResponse generateDescription(AiRequest aiRequest) {
		if (!StringUtils.hasText(aiRequest.getStoreName())) {
			throw new GeneralException(AiErrorStatus.AI_INVALID_INPUT_VALUE);
		}
		if (aiRequest.getReqType() == ReqType.MENU_DESCRIPTION && !StringUtils.hasText(aiRequest.getMenuName())) {
			throw new GeneralException(AiErrorStatus.AI_INVALID_INPUT_VALUE);
		}

		AiHistory aiRequestEntity = AiHistory.builder()
			.storeName(aiRequest.getStoreName())
			.menuName(StringUtils.hasText(aiRequest.getMenuName()) ? aiRequest.getMenuName() : "")
			.reqType(aiRequest.getReqType())
			.promptText(aiRequest.getPromptText())
			.status(AiRequestStatus.PENDING)
			.build();

		AiHistory savedAiRequestEntity = aiHistoryRepository.save(aiRequestEntity);
		PromptTemplate promptTemplate = new PromptTemplate("""
			너는 사용자의 요청에 맞춰 배달앱에 적합한 마케팅 문구를 생성하는 AI야. 아래 주어진 정보를 바탕으로 멋진 결과물을 만들어줘.
			
			- 가게 이름: {storeName}
			- 메뉴 이름: {menuName}
			- 요청 종류: {reqType}
			- 핵심 요청사항 : {promptText}
			
			요청 종류가 MENU_DESCRIPTION 이면 30자 이내로 작성해주고 STORE_DESCRIPTION 이면 100자 이내로 작성해줘.
			""");
		Prompt prompt = promptTemplate.create(Map.of(
			"storeName", aiRequestEntity.getStoreName(), "menuName", aiRequestEntity.getMenuName()
			, "reqType", aiRequestEntity.getReqType(), "promptText", aiRequestEntity.getPromptText())
		);
		String generatedContent;
		try {

			generatedContent = chatClient.prompt()
				.options(OpenAiChatOptions.builder().model("gpt-4.1-mini").build())
				.user(prompt.getContents())
				.call()
				.content();
			savedAiRequestEntity.updateGeneratedContent(generatedContent, AiRequestStatus.SUCCESS);
		} catch (Exception e) {
			savedAiRequestEntity.updateGeneratedContent("Error: " + e.getMessage(), AiRequestStatus.FAILED);
			throw new GeneralException(AiErrorStatus.AI_GENERATION_FAILED);
		}

		return new AiResponse(savedAiRequestEntity.getAiRequestId().toString(), generatedContent);
	}
}