package app.domain.menu.model.dto.response;

import java.util.UUID;

public class MenuDeleteResponse {
    private UUID menuId;
    private String status;

    public MenuDeleteResponse() {
    }

    public MenuDeleteResponse(UUID menuId, String status) {
        this.menuId = menuId;
        this.status = status;
    }

    public UUID getMenuId() {
        return menuId;
    }

    public void setMenuId(UUID menuId) {
        this.menuId = menuId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
