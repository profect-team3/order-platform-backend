package app.domain.cart.service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import app.domain.cart.model.dto.RedisCartItem;
import app.domain.cart.status.CartErrorStatus;
import app.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartRedisServiceImpl implements CartRedisService {
	private final RedisTemplate<String, String> redisTemplate;
	private final ObjectMapper redisObjectMapper;
	private static final Duration CART_TTL = Duration.ofMinutes(30);

	@Override
	public String saveCartToRedis(Long userId, List<RedisCartItem> cartItems) {
		try {
			String key = "cart:" + userId;
			redisTemplate.delete(key);

			for (RedisCartItem item : cartItems) {
				String itemJson = redisObjectMapper.writeValueAsString(item);
				redisTemplate.opsForHash().put(key, item.getMenuId().toString(), itemJson);
			}

			if (cartItems.isEmpty()) {
				redisTemplate.opsForValue().set(key, "");
			}

			redisTemplate.expire(key, CART_TTL);
			return "사용자 " + userId + "의 장바구니가 성공적으로 저장되었습니다.";
		} catch (Exception e) {
			throw new GeneralException(CartErrorStatus.CART_REDIS_SAVE_FAILED);
		}
	}

	@Override
	public List<RedisCartItem> getCartFromRedis(Long userId) {
		try {
			String key = "cart:" + userId;

			String keyType = redisTemplate.type(key).code();
			if ("string".equals(keyType)) {
				return new ArrayList<>();
			}

			return redisTemplate.opsForHash().values(key).stream()
				.map(value -> {
					try {
						return redisObjectMapper.readValue((String)value, RedisCartItem.class);
					} catch (JsonProcessingException e) {
						throw new GeneralException(CartErrorStatus.CART_ITEM_PARSE_FAILED);
					}
				})
				.collect(Collectors.toList());
		} catch (GeneralException e) {
			throw e;
		} catch (Exception e) {
			throw new GeneralException(CartErrorStatus.CART_REDIS_LOAD_FAILED);
		}
	}

	@Override
	public String clearCartItems(Long userId) {
		try {
			saveCartToRedis(userId, List.of());
			return "사용자 " + userId + "의 장바구니가 성공적으로 비워졌습니다.";
		} catch (GeneralException e) {
			throw e;
		} catch (Exception e) {
			throw new GeneralException(CartErrorStatus.CART_REDIS_SAVE_FAILED);
		}
	}

	@Override
	public String removeCartItem(Long userId, UUID menuId) {
		try {
			String key = "cart:" + userId;

			if (!redisTemplate.hasKey(key)) {
				return "사용자 " + userId + "의 장바구니에서 메뉴 " + menuId + "가 성공적으로 삭제되었습니다.";
			}

			String keyType = redisTemplate.type(key).code();
			if ("string".equals(keyType)) {
				redisTemplate.expire(key, CART_TTL);
				return "사용자 " + userId + "의 장바구니에서 메뉴 " + menuId + "가 성공적으로 삭제되었습니다.";
			}

			Long hashSize = redisTemplate.opsForHash().size(key);
			redisTemplate.opsForHash().delete(key, menuId.toString());

			if (hashSize == 1) {
				redisTemplate.opsForValue().set(key, "");
			}

			redisTemplate.expire(key, CART_TTL);
			return "사용자 " + userId + "의 장바구니에서 메뉴 " + menuId + "가 성공적으로 삭제되었습니다.";
		} catch (Exception e) {
			throw new GeneralException(CartErrorStatus.CART_REDIS_SAVE_FAILED);
		}
	}

	@Override
	public boolean existsCartInRedis(Long userId) {
		try {
			String key = "cart:" + userId;
			return redisTemplate.hasKey(key);
		} catch (Exception e) {
			throw new GeneralException(CartErrorStatus.CART_REDIS_LOAD_FAILED);
		}
	}

	@Override
	public Set<String> getAllCartKeys() {
		try {
			return redisTemplate.keys("cart:*");
		} catch (Exception e) {
			throw new GeneralException(CartErrorStatus.CART_REDIS_LOAD_FAILED);
		}
	}

	@Override
	public Long extractUserIdFromKey(String key) {
		return Long.parseLong(key.substring(5));
	}
}
