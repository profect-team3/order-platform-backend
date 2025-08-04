package app.domain.store.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import app.domain.store.model.entity.Region;

@Repository
public interface RegionRepository extends JpaRepository<Region, UUID> {
	Optional<Region> findByRegionCode(String regionCode);
}
