package app.domain.store;

import java.util.UUID;

import org.springframework.stereotype.Service;

import app.domain.store.repository.RegionRepository;
import app.domain.store.status.StoreErrorCode;
import app.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RegionService {

	private final RegionRepository regionRepository;

	public UUID getRegionIdByCode(String regionCode) {
		return regionRepository.findByRegionCode(regionCode)
			.orElseThrow(() -> new GeneralException(StoreErrorCode.REGION_NOT_FOUND))
			.getRegionId();
	}
}