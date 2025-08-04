package app.domain.customer.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import app.domain.order.model.entity.Orders;
import app.domain.order.model.entity.enums.OrderChannel;
import app.domain.order.model.entity.enums.OrderStatus;
import app.domain.order.model.entity.enums.PaymentMethod;
import app.domain.order.model.entity.enums.ReceiptMethod;

public class CustomerOrderResponse {
    private UUID ordersId;
    private String storeName;
    private Long totalPrice;
    private String deliveryAddress;
    private PaymentMethod paymentMethod;
    private OrderChannel orderChannel;
    private ReceiptMethod receiptMethod;
    private OrderStatus orderStatus;
    private boolean isRefundable;
    private String orderHistory;
    private String requestMessage;
    private LocalDateTime createdAt;

    public CustomerOrderResponse() {
    }

    public CustomerOrderResponse(UUID ordersId, String storeName, Long totalPrice, String deliveryAddress, PaymentMethod paymentMethod, OrderChannel orderChannel, ReceiptMethod receiptMethod, OrderStatus orderStatus, boolean isRefundable, String orderHistory, String requestMessage, LocalDateTime createdAt) {
        this.ordersId = ordersId;
        this.storeName = storeName;
        this.totalPrice = totalPrice;
        this.deliveryAddress = deliveryAddress;
        this.paymentMethod = paymentMethod;
        this.orderChannel = orderChannel;
        this.receiptMethod = receiptMethod;
        this.orderStatus = orderStatus;
        this.isRefundable = isRefundable;
        this.orderHistory = orderHistory;
        this.requestMessage = requestMessage;
        this.createdAt = createdAt;
    }

    public static CustomerOrderResponse of(Orders orders) {
        return new CustomerOrderResponse(
            orders.getOrdersId(),
            orders.getStore().getStoreName(),
            orders.getTotalPrice(),
            orders.getDeliveryAddress(),
            orders.getPaymentMethod(),
            orders.getOrderChannel(),
            orders.getReceiptMethod(),
            orders.getOrderStatus(),
            orders.isRefundable(),
            orders.getOrderHistory(),
            orders.getRequestMessage(),
            orders.getCreatedAt()
        );
    }

    public UUID getOrdersId() {
        return ordersId;
    }

    public void setOrdersId(UUID ordersId) {
        this.ordersId = ordersId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public Long getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Long totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public OrderChannel getOrderChannel() {
        return orderChannel;
    }

    public void setOrderChannel(OrderChannel orderChannel) {
        this.orderChannel = orderChannel;
    }

    public ReceiptMethod getReceiptMethod() {
        return receiptMethod;
    }

    public void setReceiptMethod(ReceiptMethod receiptMethod) {
        this.receiptMethod = receiptMethod;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public boolean isRefundable() {
        return isRefundable;
    }

    public void setRefundable(boolean refundable) {
        isRefundable = refundable;
    }

    public String getOrderHistory() {
        return orderHistory;
    }

    public void setOrderHistory(String orderHistory) {
        this.orderHistory = orderHistory;
    }

    public String getRequestMessage() {
        return requestMessage;
    }

    public void setRequestMessage(String requestMessage) {
        this.requestMessage = requestMessage;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
