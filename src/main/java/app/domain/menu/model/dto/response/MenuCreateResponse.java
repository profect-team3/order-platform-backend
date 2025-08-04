package app.domain.menu.model.dto.response;

import java.util.UUID;

public class MenuCreateResponse {
    private UUID menuId;
    private String name;

    public MenuCreateResponse() {
    }

    public MenuCreateResponse(UUID menuId, String name) {
        this.menuId = menuId;
        this.name = name;
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
}
