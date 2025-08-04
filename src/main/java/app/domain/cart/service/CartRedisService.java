package app.domain.cart.service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import app.domain.cart.model.dto.RedisCartItem;

public interface CartRedisService {
	String saveCartToRedis(Long userId, List<RedisCartItem> cartItems);

	List<RedisCartItem> getCartFromRedis(Long userId);

	String removeCartItem(Long userId, UUID menuId);

	String clearCartItems(Long userId);

	boolean existsCartInRedis(Long userId);

	Set<String> getAllCartKeys();

	Long extractUserIdFromKey(String key);
}
