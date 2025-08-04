package app.domain.menu.model.dto.request;

import java.util.UUID;

public class MenuVisibleRequest {
    private UUID menuId;
    private Boolean visible;

    public MenuVisibleRequest() {
    }

    public MenuVisibleRequest(UUID menuId, Boolean visible) {
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
