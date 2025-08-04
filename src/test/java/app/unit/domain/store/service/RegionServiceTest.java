package app.unit.domain.store.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import app.domain.store.RegionService;
import app.domain.store.model.entity.Region;
import app.domain.store.repository.RegionRepository;

@ExtendWith(MockitoExtension.class)
class RegionServiceTest {

	@InjectMocks
	private RegionService regionService;

	@Mock
	private RegionRepository regionRepository;

	@Nested
	@DisplayName("getRegionIdByCode 테스트")
	class GetRegionIdByCodeTest {

		@Test
		@DisplayName("Success")
		void getRegionIdByCodeSuccess() {
			String regionCode = "SEOUL";
			UUID expectedRegionId = UUID.randomUUID();
			Region mockRegion = Region.builder()
				.regionId(expectedRegionId)
				.regionCode(regionCode)
				.regionName("서울")
				.build();

			when(regionRepository.findByRegionCode(regionCode)).thenReturn(Optional.of(mockRegion));

			UUID actualRegionId = regionService.getRegionIdByCode(regionCode);

			assertNotNull(actualRegionId);
			assertEquals(expectedRegionId, actualRegionId);
			verify(regionRepository, times(1)).findByRegionCode(regionCode);
		}
	}
}


