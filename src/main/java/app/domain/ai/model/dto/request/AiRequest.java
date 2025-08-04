package app.domain.ai.model.dto.request;

import app.domain.ai.model.entity.enums.ReqType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@ConditionalNotNull
public class AiRequest {
	@NotBlank(message = "가게 이름은 필수입니다.")
	private String storeName;

	private String menuName;

	@NotNull(message = "요청 타입은 필수입니다.")
	private ReqType reqType;

	@NotBlank(message = "프롬프트 텍스트는 필수입니다.")
	private String promptText;

	public AiRequest() {
	}

	public AiRequest(String storeName, String menuName, ReqType reqType, String promptText) {
		this.storeName = storeName;
		this.menuName = menuName;
		this.reqType = reqType;
		this.promptText = promptText;
	}

	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

	public String getMenuName() {
		return menuName;
	}

	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}

	public ReqType getReqType() {
		return reqType;
	}

	public void setReqType(ReqType reqType) {
		this.reqType = reqType;
	}

	public String getPromptText() {
		return promptText;
	}

	public void setPromptText(String promptText) {
		this.promptText = promptText;
	}
}