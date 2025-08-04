package app.domain.menu.model.dto.request;

import java.util.UUID;

public class MenuUpdateRequest {
    private UUID menuId;
    private String name;
    private Long price;
    private String description;
    private Boolean isHidden;

    public MenuUpdateRequest() {
    }

    public MenuUpdateRequest(UUID menuId, String name, Long price, String description, Boolean isHidden) {
        this.menuId = menuId;
        this.name = name;
        this.price = price;
        this.description = description;
        this.isHidden = isHidden;
    }

    public UUID getMenuId() {
        return menuId;
    }

    public void setMenuId(UUID menuId) {
        this.menuId = menuId;
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

    public void setPrice(Long price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsHidden() {
        return isHidden;
    }

    public void setIsHidden(Boolean isHidden) {
        this.isHidden = isHidden;
    }
}
