package app.domain.menu.model.dto.request;

import java.util.UUID;

public class MenuListRequest {
    private UUID storeId;

    public MenuListRequest() {
    }

    public MenuListRequest(UUID storeId) {
        this.storeId = storeId;
    }

    public UUID getStoreId() {
        return storeId;
    }

    public void setStoreId(UUID storeId) {
        this.storeId = storeId;
    }
}
