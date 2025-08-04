package app.domain.cart.model.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import app.domain.cart.model.entity.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart, UUID> {
	Optional<Cart> findByUser_UserId(Long userId);
}