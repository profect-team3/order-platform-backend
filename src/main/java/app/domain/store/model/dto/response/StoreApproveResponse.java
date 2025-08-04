package app.domain.store.model.dto.response;

import java.util.UUID;

public class StoreApproveResponse {
    private UUID storeId;
    private String storeApprovalStatus;

    public StoreApproveResponse() {
    }

    public StoreApproveResponse(UUID storeId, String storeApprovalStatus) {
        this.storeId = storeId;
        this.storeApprovalStatus = storeApprovalStatus;
    }

    public UUID getStoreId() {
        return storeId;
    }

    public void setStoreId(UUID storeId) {
        this.storeId = storeId;
    }

    public String getStoreApprovalStatus() {
        return storeApprovalStatus;
    }

    public void setStoreApprovalStatus(String storeApprovalStatus) {
        this.storeApprovalStatus = storeApprovalStatus;
    }
}