package app.domain.menu.model.dto.response;

import java.util.List;
import java.util.UUID;

public class MenuListResponse {
    private UUID storeId;
    private List<MenuDetail> menus;

    public MenuListResponse() {
    }

    public MenuListResponse(UUID storeId, List<MenuDetail> menus) {
        this.storeId = storeId;
        this.menus = menus;
    }

    public UUID getStoreId() {
        return storeId;
    }

    public void setStoreId(UUID storeId) {
        this.storeId = storeId;
    }

    public List<MenuDetail> getMenus() {
        return menus;
    }

    public void setMenus(List<MenuDetail> menus) {
        this.menus = menus;
    }

    public static class MenuDetail {
        private UUID menuId;
        private String name;
        private Long price;
        private String description;
        private boolean isHidden;

        public MenuDetail() {
        }

        public MenuDetail(UUID menuId, String name, Long price, String description, boolean isHidden) {
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

        public boolean isHidden() {
            return isHidden;
        }

        public void setHidden(boolean hidden) {
            isHidden = hidden;
        }
    }
}
