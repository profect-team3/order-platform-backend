package app.domain.store.model.dto.request;

import java.util.UUID;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StoreApproveRequest {
    @NotNull private UUID regionId;
    @NotNull private UUID categoryId;
    @NotNull private String address;
    @NotNull private String storeName;
    private String desc;
    private String phoneNumber;
    @NotNull private Long minOrderAmount;
}