package app.unit.domain.cart.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import app.domain.cart.model.dto.RedisCartItem;
import app.domain.cart.service.CartRedisService;
import app.global.apiPayload.exception.GeneralException;

@SpringBootTest
class CartRedisServiceTest {

	@Autowired
	private CartRedisService cartRedisService;

	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	@AfterEach
	void cleanup() {
		redisTemplate.getConnectionFactory().getConnection().flushAll();
	}

	@Test
	@DisplayName("장바구니 아이템을 Redis에 제대로 저장하고 조회할 수 있다")
	void saveAndGetCartItems_Success() {
		Long userId = 1L;
		UUID menuId1 = UUID.randomUUID();
		UUID menuId2 = UUID.randomUUID();
		UUID storeId = UUID.randomUUID();
		List<RedisCartItem> cartItems = List.of(
			RedisCartItem.builder()
				.menuId(menuId1)
				.storeId(storeId)
				.quantity(2)
				.build(),
			RedisCartItem.builder()
				.menuId(menuId2)
				.storeId(storeId)
				.quantity(3)
				.build()
		);

		String result = cartRedisService.saveCartToRedis(userId, cartItems);
		List<RedisCartItem> retrievedItems = cartRedisService.getCartFromRedis(userId);

		assertThat(result).contains("성공적으로 저장");
		assertThat(retrievedItems).hasSize(2);
		assertThat(retrievedItems).extracting(RedisCartItem::getMenuId).containsExactlyInAnyOrder(menuId1, menuId2);
		assertThat(retrievedItems).extracting(RedisCartItem::getQuantity).containsExactlyInAnyOrder(2, 3);
	}

	@Test
	@DisplayName("Redis에 TTL이 제대로 설정된다")
	void saveCartItems_TTLSet() {
		Long userId = 2L;
		List<RedisCartItem> cartItems = List.of(
			RedisCartItem.builder()
				.menuId(UUID.randomUUID())
				.storeId(UUID.randomUUID())
				.quantity(1)
				.build()
		);

		cartRedisService.saveCartToRedis(userId, cartItems);
		String key = "cart:" + userId;
		Long ttl = redisTemplate.getExpire(key, TimeUnit.MINUTES);

		assertThat(ttl).isGreaterThan(0);
		assertThat(ttl).isLessThanOrEqualTo(30);
	}

	@Test
	@DisplayName("장바구니 전체 삭제 시 비어있는 상태가 된다")
	void clearCartItems_BecomesEmpty() {
		Long userId = 3L;
		UUID storeId = UUID.randomUUID();
		List<RedisCartItem> cartItems = List.of(
			RedisCartItem.builder()
				.menuId(UUID.randomUUID())
				.storeId(storeId)
				.quantity(1)
				.build(),
			RedisCartItem.builder()
				.menuId(UUID.randomUUID())
				.storeId(storeId)
				.quantity(2)
				.build()
		);
		cartRedisService.saveCartToRedis(userId, cartItems);

		String result = cartRedisService.clearCartItems(userId);
		List<RedisCartItem> retrievedItems = cartRedisService.getCartFromRedis(userId);

		assertThat(result).contains("성공적으로 비워졌습니다");
		assertThat(retrievedItems).isEmpty();
	}

	@Test
	@DisplayName("장바구니 전체 삭제 시 TTL이 갱신된다")
	void clearCartItems_TTLRenewed() {
		Long userId = 4L;
		UUID storeId = UUID.randomUUID();
		List<RedisCartItem> cartItems = List.of(
			RedisCartItem.builder()
				.menuId(UUID.randomUUID())
				.storeId(storeId)
				.quantity(1)
				.build(),
			RedisCartItem.builder()
				.menuId(UUID.randomUUID())
				.storeId(storeId)
				.quantity(3)
				.build()
		);
		cartRedisService.saveCartToRedis(userId, cartItems);
		String key = "cart:" + userId;
		redisTemplate.expire(key, 5, TimeUnit.MINUTES);

		cartRedisService.clearCartItems(userId);
		Long ttl = redisTemplate.getExpire(key, TimeUnit.MINUTES);
		System.out.println(ttl);
		assertThat(ttl).isGreaterThan(25);
		assertThat(ttl).isLessThanOrEqualTo(30);
	}

	@Test
	@DisplayName("특정 메뉴 아이템이 삭제된다")
	void removeCartItem_ItemDeleted() {
		Long userId = 5L;
		UUID menuId1 = UUID.randomUUID();
		UUID menuId2 = UUID.randomUUID();
		UUID storeId = UUID.randomUUID();
		List<RedisCartItem> cartItems = List.of(
			RedisCartItem.builder()
				.menuId(menuId1)
				.storeId(storeId)
				.quantity(1)
				.build(),
			RedisCartItem.builder()
				.menuId(menuId2)
				.storeId(storeId)
				.quantity(2)
				.build()
		);
		cartRedisService.saveCartToRedis(userId, cartItems);

		String result = cartRedisService.removeCartItem(userId, menuId1);
		List<RedisCartItem> retrievedItems = cartRedisService.getCartFromRedis(userId);

		assertThat(result).contains("성공적으로 삭제");
		assertThat(retrievedItems).hasSize(1);
		assertThat(retrievedItems.get(0).getMenuId()).isEqualTo(menuId2);
	}

	@Test
	@DisplayName("메뉴 아이템 삭제 시 TTL이 갱신된다")
	void removeCartItem_TTLRenewed() {
		Long userId = 6L;
		UUID menuId = UUID.randomUUID();
		List<RedisCartItem> cartItems = List.of(
			RedisCartItem.builder()
				.menuId(menuId)
				.storeId(UUID.randomUUID())
				.quantity(1)
				.build()
		);
		cartRedisService.saveCartToRedis(userId, cartItems);
		String key = "cart:" + userId;
		redisTemplate.expire(key, 5, TimeUnit.MINUTES);

		cartRedisService.removeCartItem(userId, menuId);
		Long ttl = redisTemplate.getExpire(key, TimeUnit.MINUTES);

		assertThat(ttl).isGreaterThan(25);
		assertThat(ttl).isLessThanOrEqualTo(30);
	}

	@Test
	@DisplayName("존재하는 장바구니는 true를 반환한다")
	void existsCartInRedis_ExistingCart_ReturnsTrue() {
		Long userId = 7L;
		List<RedisCartItem> cartItems = List.of(
			RedisCartItem.builder()
				.menuId(UUID.randomUUID())
				.storeId(UUID.randomUUID())
				.quantity(1)
				.build()
		);

		cartRedisService.saveCartToRedis(userId, cartItems);
		boolean exists = cartRedisService.existsCartInRedis(userId);

		assertThat(exists).isTrue();
	}

	@Test
	@DisplayName("존재하지 않는 장바구니는 false를 반환한다")
	void existsCartInRedis_NonExistingCart_ReturnsFalse() {
		Long userId = 8L;

		boolean exists = cartRedisService.existsCartInRedis(userId);

		assertThat(exists).isFalse();
	}

	@Test
	@DisplayName("장바구니 키들만 조회된다")
	void getAllCartKeys_OnlyCartKeys() {
		Long userId1 = 9L;
		Long userId2 = 10L;
		List<RedisCartItem> cartItems = List.of(
			RedisCartItem.builder()
				.menuId(UUID.randomUUID())
				.storeId(UUID.randomUUID())
				.quantity(1)
				.build()
		);
		redisTemplate.opsForValue().set("other:key", "value");

		cartRedisService.saveCartToRedis(userId1, cartItems);
		cartRedisService.saveCartToRedis(userId2, cartItems);
		Set<String> keys = cartRedisService.getAllCartKeys();

		assertThat(keys).hasSize(2);
		assertThat(keys).contains("cart:" + userId1, "cart:" + userId2);
		assertThat(keys).doesNotContain("other:key");
	}

	@Test
	@DisplayName("키에서 userId가 올바르게 추출된다")
	void extractUserIdFromKey_ValidKey() {
		String key1 = "cart:123";
		String key2 = "cart:999";

		Long userId1 = cartRedisService.extractUserIdFromKey(key1);
		Long userId2 = cartRedisService.extractUserIdFromKey(key2);

		assertThat(userId1).isEqualTo(123L);
		assertThat(userId2).isEqualTo(999L);
	}

	@Test
	@DisplayName("잘못된 키 형식에서 예외가 발생한다")
	void extractUserIdFromKey_InvalidKey_ThrowsException() {
		String invalidKey = "invalid:key";

		assertThrows(GeneralException.class, () -> {
			cartRedisService.extractUserIdFromKey(invalidKey);
		});
	}

	@Test
	@DisplayName("장바구니가 비면 Redis 키 타입이 Hash에서 String으로 변경된다")
	void cartKeyType_ChangesFromHashToString() {
		Long userId = 11L;
		UUID menuId = UUID.randomUUID();
		UUID storeId = UUID.randomUUID();
		List<RedisCartItem> cartItems = List.of(
			RedisCartItem.builder()
				.menuId(menuId)
				.storeId(storeId)
				.quantity(1)
				.build()
		);
		String key = "cart:" + userId;

		cartRedisService.saveCartToRedis(userId, cartItems);
		String keyTypeAfterSave = redisTemplate.type(key).code();

		cartRedisService.removeCartItem(userId, menuId);
		String keyTypeAfterRemove = redisTemplate.type(key).code();
		List<RedisCartItem> retrievedItems = cartRedisService.getCartFromRedis(userId);

		assertThat(keyTypeAfterSave).isEqualTo("hash");
		assertThat(keyTypeAfterRemove).isEqualTo("string");
		assertThat(retrievedItems).isEmpty();
	}
}