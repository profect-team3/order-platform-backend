package app.domain.cart.model.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import app.domain.cart.model.entity.CartItem;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, UUID> {
	List<CartItem> findByCart_CartId(UUID cartId);

	void deleteByCart_CartId(UUID cartId);
}