package app.domain.menu.model.dto.response;

import java.util.UUID;

public class MenuVisibleResponse {
    private UUID menuId;
    private Boolean visible;

    public MenuVisibleResponse() {
    }

    public MenuVisibleResponse(UUID menuId, Boolean visible) {
        this.menuId = menuId;
        this.visible = visible;
    }

    public UUID getMenuId() {
        return menuId;
    }

    public void setMenuId(UUID menuId) {
        this.menuId = menuId;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }
}
