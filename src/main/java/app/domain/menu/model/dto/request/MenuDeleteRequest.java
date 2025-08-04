package app.domain.menu.model.dto.request;

import java.util.UUID;

public class MenuDeleteRequest {
    private UUID menuId;

    public MenuDeleteRequest() {
    }

    public MenuDeleteRequest(UUID menuId) {
        this.menuId = menuId;
    }

    public UUID getMenuId() {
        return menuId;
    }

    public void setMenuId(UUID menuId) {
        this.menuId = menuId;
    }
}
