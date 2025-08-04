package app.domain.store.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import app.domain.store.model.entity.Region;
import app.domain.store.model.entity.Store;
import app.domain.store.status.StoreAcceptStatus;
import app.domain.user.model.entity.User;

@Repository
public interface StoreRepository extends JpaRepository<Store, UUID> {

	Optional<Store> findByUser_UserId(Long userId);

	boolean existsByStoreNameAndRegion(String storeName, Region region);

	Optional<Store> findByStoreIdAndDeletedAtIsNull(UUID storeId);

	Optional<Store> findByStoreIdAndStoreAcceptStatusAndDeletedAtIsNull(UUID storeId, StoreAcceptStatus status);

	boolean existsByStoreIdAndDeletedAtIsNull(UUID storeId);
	List<Store> user(User user);
}
