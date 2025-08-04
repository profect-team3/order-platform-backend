package app.domain.store.model.dto.response;

import java.util.UUID;

public class StoreInfoUpdateResponse {
    private UUID storeId;

    public StoreInfoUpdateResponse() {
    }

    public StoreInfoUpdateResponse(UUID storeId) {
        this.storeId = storeId;
    }

    public UUID getStoreId() {
        return storeId;
    }

    public void setStoreId(UUID storeId) {
        this.storeId = storeId;
    }
}