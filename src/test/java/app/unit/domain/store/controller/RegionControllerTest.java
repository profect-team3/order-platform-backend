package app.unit.domain.store.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


import app.domain.store.RegionController;
import app.domain.store.RegionService;
import app.domain.store.status.StoreErrorCode;
import app.global.apiPayload.exception.GeneralException;

@ExtendWith(MockitoExtension.class)
class RegionControllerTest {

	@InjectMocks
	RegionController regionController;

	@Mock
	RegionService regionService;

	@Test
	@DisplayName("Success : 유효한 지역 코드")
	void getRegionIdByCodeSuccess() {
		String regionCode = "1111010600"; //광화문 지역 코드
		UUID regionId = UUID.randomUUID();

		when(regionService.getRegionIdByCode(regionCode)).thenReturn(regionId);

		ResponseEntity<UUID> response = regionController.getRegionIdByCode(regionCode);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(regionId, response.getBody());
		verify(regionService, times(1)).getRegionIdByCode(regionCode);
	}

	@Test
	@DisplayName("Fail : RegionCode 존재하지 않음")
	void getRegionIdByCodeRegionCodeNotFound() {
		GeneralException exception = assertThrows(GeneralException.class, () -> {
			regionController.getRegionIdByCode(null);
		});
		assertEquals(StoreErrorCode.REGIONCODE_NOT_FOUND.name(), exception.getCode());
	}

	@Test
	@DisplayName("Fail : RegionId 존재하지 않음")
	void getRegionIdByCodeRegionIdNotFound() {
		String regionCode = "1111010600";

		when(regionService.getRegionIdByCode(regionCode)).thenThrow(
			new GeneralException(StoreErrorCode.REGION_NOT_FOUND));

		GeneralException exception = assertThrows(GeneralException.class, () -> {
			regionController.getRegionIdByCode(regionCode);
		});
		assertEquals(StoreErrorCode.REGION_NOT_FOUND.name(), exception.getCode());
	}
}

