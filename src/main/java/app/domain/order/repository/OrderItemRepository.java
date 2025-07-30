package app.domain.order.repository;

import app.domain.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {
    
    @Query("SELECT oi FROM OrderItem oi WHERE oi.orders.OrdersId = :orderId")
    List<OrderItem> findByOrderId(@Param("orderId") UUID orderId);
}