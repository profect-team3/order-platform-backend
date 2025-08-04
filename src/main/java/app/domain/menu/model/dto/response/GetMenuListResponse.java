package app.domain.menu.model.dto.response;

import java.util.UUID;

import app.domain.menu.model.entity.Menu;

public class GetMenuListResponse {
    private UUID menuId;
    private String name;
    private String description;
    private Long price;

    public GetMenuListResponse() {
    }

    public GetMenuListResponse(UUID menuId, String name, String description, Long price) {
        this.menuId = menuId;
        this.name = name;
        this.description = description;
        this.price = price;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public static GetMenuListResponse from(Menu menu) {
        return new GetMenuListResponse(
            menu.getMenuId(),
            menu.getName(),
            menu.getDescription(),
            menu.getPrice()
        );
    }
}
