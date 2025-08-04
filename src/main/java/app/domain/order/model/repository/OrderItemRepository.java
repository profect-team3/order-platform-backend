package app.domain.order.model.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import app.domain.order.model.entity.OrderItem;
import app.domain.order.model.entity.Orders;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {
	List<OrderItem> findByOrders(Orders orders);
}
