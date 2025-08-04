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
import app.domain.store.repository.RegionRepository;

@ExtendWith(MockitoExtension.class)
class RegionRepositoryTest {

	@Mock
	private RegionRepository regionRepository;

	@Test
	@DisplayName("Success: findById - ID로 지역 조회")
	void findByIdSuccess() {
		UUID regionId = UUID.randomUUID();
		Region mockRegion = Region.builder()
			.regionId(regionId)
			.regionName("테스트 지역")
			.build();
		when(regionRepository.findById(regionId)).thenReturn(Optional.of(mockRegion));

		Optional<Region> foundRegionOptional = regionRepository.findById(regionId);

		assertTrue(foundRegionOptional.isPresent());
		assertEquals(regionId, foundRegionOptional.get().getRegionId());
		verify(regionRepository, times(1)).findById(regionId);
	}

	@Test
	@DisplayName("Fail: findById - ID로 지역 조회 - 지역 존재하지 않음")
	void findByIdFailNotFound() {
		UUID nonExistentRegionId = UUID.randomUUID();
		when(regionRepository.findById(nonExistentRegionId)).thenReturn(Optional.empty());

		Optional<Region> foundRegionOptional = regionRepository.findById(nonExistentRegionId);

		assertFalse(foundRegionOptional.isPresent());
		verify(regionRepository, times(1)).findById(nonExistentRegionId);
	}

	@Test
	@DisplayName("Success: save - 지역 저장")
	void saveSuccess() {
		Region newRegion = Region.builder()
			.regionName("새로운 지역")
			.regionCode("NEW001")
			.build();
		when(regionRepository.save(any(Region.class))).thenAnswer(invocation -> {
			Region regionToSave = invocation.getArgument(0);
			return Region.builder()
				.regionId(UUID.randomUUID())
				.regionCode(regionToSave.getRegionCode())
				.regionName(regionToSave.getRegionName())
				.isActive(regionToSave.isActive())
				.fullName(regionToSave.getFullName())
				.sido(regionToSave.getSido())
				.sigungu(regionToSave.getSigungu())
				.eupmyendong(regionToSave.getEupmyendong())
				.build();
		});

		Region savedRegion = regionRepository.save(newRegion);

		assertNotNull(savedRegion.getRegionId());
		assertEquals("새로운 지역", savedRegion.getRegionName());
		verify(regionRepository, times(1)).save(newRegion);
	}
}
