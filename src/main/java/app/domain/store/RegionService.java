package app.domain.store;

import java.util.UUID;

import org.springframework.stereotype.Service;

import app.domain.store.repository.RegionRepository;
import app.domain.store.status.StoreErrorCode;
import app.domain.store.status.StoreException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RegionService {

    private final RegionRepository regionRepository;

    public UUID getRegionIdByCode(String regionCode) {
        return regionRepository.findByRegionCode(regionCode)
                .orElseThrow(() -> new StoreException(StoreErrorCode.REGION_NOT_FOUND))
                .getRegionId();
    }
}