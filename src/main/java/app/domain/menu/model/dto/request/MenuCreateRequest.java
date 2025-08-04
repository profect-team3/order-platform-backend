package app.domain.menu.model.dto.request;

import java.util.UUID;

public class MenuCreateRequest {
	private UUID storeId;
	private String name;
	private Long price;
	private String description;

	public MenuCreateRequest() {
	}

	public MenuCreateRequest(UUID storeId, String name, Long price, String description) {
		this.storeId = storeId;
		this.name = name;
		this.price = price;
		this.description = description;
	}

	public UUID getStoreId() {
		return storeId;
	}

	public void setStoreId(UUID storeId) {
		this.storeId = storeId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getPrice() {
		return price;
	}

	public void setPrice(long price) {
		this.price = price;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
