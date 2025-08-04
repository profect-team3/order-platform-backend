package app.domain.review.model;

import java.util.List;
import java.util.UUID;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import app.domain.order.model.entity.Orders;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import app.domain.review.model.entity.Review;
import org.springframework.data.repository.query.Param;
import app.domain.order.model.entity.Orders;
import app.domain.review.model.entity.Review;
import app.domain.user.model.entity.User;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {

	List<Review> findByUser(User user);
	@Query("SELECT AVG(r.rating) FROM Review r WHERE r.store.storeId = :storeId")
	Double getAverageRatingByStore(@Param("storeId") UUID storeId);

	boolean existsByOrders(Orders orders);
}
