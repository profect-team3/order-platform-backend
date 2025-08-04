package app.domain.ai;

import app.domain.ai.model.dto.request.AiRequest;
import app.domain.ai.model.dto.response.AiResponse;

public interface AiService {
	AiResponse generateDescription(AiRequest aiRequest);
}
