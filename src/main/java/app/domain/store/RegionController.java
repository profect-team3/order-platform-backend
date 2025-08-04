package app.domain.store;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import app.domain.store.status.StoreErrorCode;
import app.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/region")
public class RegionController {

	private final RegionService regionService;

	@PostMapping("/code/{regionId}")
	public ResponseEntity<UUID> getRegionIdByCode(@PathVariable("regionId") String regionCode) {
		UUID regionId = regionService.getRegionIdByCode(regionCode);
		if (regionCode == null || regionCode.isBlank()) {
			throw new GeneralException(StoreErrorCode.REGIONCODE_NOT_FOUND);
		}

		return ResponseEntity.ok(regionId);
	}
}
