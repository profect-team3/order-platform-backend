package app.domain.review.repository;

import app.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {
    
    @Query("SELECT r FROM Review r LEFT JOIN FETCH r.store WHERE r.user.userId = :customerId")
    List<Review> findByCustomerId(@Param("customerId") Long customerId);
    
    @Query("SELECT r FROM Review r LEFT JOIN FETCH r.user LEFT JOIN FETCH r.store WHERE r.store.storeId = :storeId")
    List<Review> findByStoreId(@Param("storeId") UUID storeId);
    
    @Query("SELECT r FROM Review r WHERE r.Orders.OrdersId = :orderId")
    List<Review> findByOrderId(@Param("orderId") UUID orderId);
}