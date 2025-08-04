package app.domain.store.model.dto.request;

import java.util.UUID;
import jakarta.validation.constraints.NotNull;

public class StoreApproveRequest {
    @NotNull private UUID regionId;
    @NotNull private UUID categoryId;
    @NotNull private String address;
    @NotNull private String storeName;
    private String desc;
    private String phoneNumber;
    @NotNull private Long minOrderAmount;

    public StoreApproveRequest() {
    }

    public StoreApproveRequest(UUID regionId, UUID categoryId, String address, String storeName, String desc, String phoneNumber, Long minOrderAmount) {
        this.regionId = regionId;
        this.categoryId = categoryId;
        this.address = address;
        this.storeName = storeName;
        this.desc = desc;
        this.phoneNumber = phoneNumber;
        this.minOrderAmount = minOrderAmount;
    }

    public UUID getRegionId() {
        return regionId;
    }

    public void setRegionId(UUID regionId) {
        this.regionId = regionId;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Long getMinOrderAmount() {
        return minOrderAmount;
    }

    public void setMinOrderAmount(Long minOrderAmount) {
        this.minOrderAmount = minOrderAmount;
    }
}
