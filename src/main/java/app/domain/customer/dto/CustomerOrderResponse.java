package app.domain.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerOrderResponse {
    private UUID orderId;
    private String storeName;
    private int totalPrice;
    private String deliveryAddress;
    private String paymentMethod;
    private String orderStatus;
    private LocalDateTime orderDate;
    private List<OrderItemDto> items;
    
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemDto {
        private String menuName;
        private int price;
        private int quantity;
    }
}