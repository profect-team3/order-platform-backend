package app.domain.store.model.dto.request;

import java.util.UUID;

public class StoreInfoUpdateRequest {
    private UUID storeId;
    private UUID categoryId;
    private String name;
    private String address;
    private String phoneNumber;
    private Long minOrderAmount;
    private String desc;

    public StoreInfoUpdateRequest() {
    }

    public StoreInfoUpdateRequest(UUID storeId, UUID categoryId, String name, String address, String phoneNumber, Long minOrderAmount, String desc) {
        this.storeId = storeId;
        this.categoryId = categoryId;
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.minOrderAmount = minOrderAmount;
        this.desc = desc;
    }

    public UUID getStoreId() {
        return storeId;
    }

    public void setStoreId(UUID storeId) {
        this.storeId = storeId;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}