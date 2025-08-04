package app.unit.domain.store.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import app.domain.store.model.entity.Region;
import app.domain.store.model.entity.Store;
import app.domain.store.repository.StoreRepository;
import app.domain.user.model.entity.User;

@ExtendWith(MockitoExtension.class)
class StoreRepositoryTest {

	@Mock
	private StoreRepository storeRepository;

	@Test
	@DisplayName("Success : 사용자 ID로 가게 조회 정상 동작 Test")
	void findByUserUserIdSuccess() {
		Long userId = 1L;
		UUID storeId = UUID.randomUUID();

		User mockUser = User.builder()
			.userId(userId)
			.username("testuser")
			.email("test@example.com")
			.password("password")
			.nickname("testnick")
			.realName("테스트유저")
			.phoneNumber("01012345678")
			.userRole(app.domain.user.model.entity.enums.UserRole.OWNER)
			.build();

		Store mockStore = Store.builder()
			.storeId(storeId)
			.user(mockUser)
			.storeName("테스트 가게")
			.address("테스트 주소")
			.minOrderAmount(0)
			.build();

		when(storeRepository.findByUser_UserId(userId)).thenReturn(Optional.of(mockStore));

		Optional<Store> foundStoreOptional = storeRepository.findByUser_UserId(userId);

		assertTrue(foundStoreOptional.isPresent());
		Store foundStore = foundStoreOptional.get();
		assertEquals(storeId, foundStore.getStoreId());
		assertEquals(userId, foundStore.getUser().getUserId());
		assertEquals("테스트 가게", foundStore.getStoreName());
		verify(storeRepository, times(1)).findByUser_UserId(userId);
	}

	@Test
	@DisplayName("Fail : 사용자ID로 가게 조회 - 사용자 존재하지 않음 ")
	void findByUserUserIdFailNotFound() {
		Long nonExistentUserId = 999L;

		when(storeRepository.findByUser_UserId(nonExistentUserId)).thenReturn(Optional.empty());

		Optional<Store> foundStoreOptional = storeRepository.findByUser_UserId(nonExistentUserId);

		assertFalse(foundStoreOptional.isPresent());

		verify(storeRepository, times(1)).findByUser_UserId(nonExistentUserId);
	}

	@Test
	@DisplayName("Fail : 동일 지역, 동일 가게명 ")
	void saveFail_DuplicateStoreNameRegion() {
		Store invalidStore = Store.builder()
			.storeName("중복가게")
			.region(mock(Region.class))
			.build();

		when(storeRepository.save(invalidStore))
			.thenThrow(new RuntimeException("조건 위반"));

		RuntimeException ex = assertThrows(RuntimeException.class, () -> {
			storeRepository.save(invalidStore);
		});

		assertEquals("조건 위반", ex.getMessage());
		verify(storeRepository).save(invalidStore);
	}

}