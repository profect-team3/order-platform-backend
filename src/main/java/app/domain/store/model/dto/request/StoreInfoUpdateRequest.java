package app.domain.store.model.dto.request;

import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StoreInfoUpdateRequest {
    private UUID storeId;
    private UUID categoryId;
    private String name;
    private String address;
    private String phoneNumber;
    private Long minOrderAmount;
    private String desc;
}
