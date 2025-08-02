package app.domain.ai.model.dto.request;

import app.domain.ai.model.entity.enums.ReqType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@ConditionalNotNull
public record AiRequest(
	@NotBlank(message = "가게 이름은 필수입니다.")
	String storeName,

	String menuName,

	@NotNull(message = "요청 타입은 필수입니다.")
	ReqType reqType,

	String promptText
) {
}