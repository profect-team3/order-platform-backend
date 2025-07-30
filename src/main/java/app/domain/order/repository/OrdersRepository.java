package app.domain.order.repository;

import app.domain.order.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface OrdersRepository extends JpaRepository<Orders, UUID> {
    
    @Query("SELECT o FROM Orders o LEFT JOIN FETCH o.store WHERE o.user.userId = :customerId")
    List<Orders> findByCustomerId(@Param("customerId") Long customerId);
    
    @Query("SELECT o FROM Orders o LEFT JOIN FETCH o.store WHERE o.user.userId = :customerId AND o.createdAt BETWEEN :startDate AND :endDate")
    List<Orders> findByCustomerIdAndDateRange(
        @Param("customerId") Long customerId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
}