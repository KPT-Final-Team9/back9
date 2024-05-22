package com.core.back9.service;

import com.core.back9.dto.BuildingDTO;
import com.core.back9.entity.Building;
import com.core.back9.entity.constant.Status;
import com.core.back9.mapper.BuildingMapper;
import com.core.back9.repository.BuildingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest
@Transactional
class BuildingServiceTest {

	@Mock
	private BuildingRepository buildingRepository;

	@Mock
	private BuildingMapper buildingMapper;

	@InjectMocks
	private BuildingService buildingService;

	private Building building;
	private BuildingDTO.Request request;
	private BuildingDTO.Info info;

	@BeforeEach
	public void initSetting() {
		buildingService = new BuildingService(buildingRepository, buildingMapper);
		building = Building.builder()
		  .name("building name")
		  .address("building address")
		  .zipCode("building zipCode")
		  .build();
		request = BuildingDTO.Request.builder()
		  .name("request name")
		  .address("request address")
		  .zipCode("request zipCode")
		  .build();
		info = BuildingDTO.Info.builder()
		  .name("info name")
		  .address("info address")
		  .zipCode("info zipCode")
		  .build();
	}

	@DisplayName("빌딩 등록 성공")
	@Test
	public void givenRequestWhenCreateBuildingThenResponse() {
		Building building = Building.builder()
		  .name(request.getName())
		  .address(request.getAddress())
		  .zipCode(request.getZipCode())
		  .build();
		Building savedBuilding = Building.builder()
		  .name(request.getName())
		  .address(request.getAddress())
		  .zipCode(request.getZipCode())
		  .build();
		BuildingDTO.Response response = BuildingDTO.Response.builder()
		  .id(1L)
		  .name(request.getName())
		  .address(request.getAddress())
		  .zipCode(request.getZipCode())
		  .build();
		given(buildingMapper.toEntity(request)).willReturn(building);
		given(buildingRepository.save(building)).willReturn(savedBuilding);
		given(buildingMapper.toResponse(savedBuilding)).willReturn(response);

		BuildingDTO.Response result = buildingService.create(request);

		assertThat(result).isEqualTo(response);
		verify(buildingMapper).toEntity(request);
		verify(buildingRepository).save(building);
		verify(buildingMapper).toResponse(savedBuilding);
	}

	@DisplayName("빌딩 전체 조회 성공")
	@Test
	public void givenNothingWhenSelectAllBuildingsThenBuildingPageList() {
		Pageable pageable = PageRequest.of(0, 10);
		Page<Building> buildingPage = new PageImpl<>(List.of(building), pageable, 1);
		Page<BuildingDTO.Info> buildingInfoPage = new PageImpl<>(List.of(info), pageable, 1);
		given(buildingRepository.findAllByStatus(Status.REGISTER, pageable)).willReturn(buildingPage);
		given(buildingMapper.toInfo(building, pageable)).willReturn(info);

		Page<BuildingDTO.Info> result = buildingService.selectAll(pageable);

		assertThat(result).isEqualTo(buildingInfoPage);
		assertThat(result.getSize()).isEqualTo(buildingInfoPage.getSize());
	}

	@DisplayName("빌딩 아이디로 조회 성공")
	@Test
	public void givenBuildingIdWhenSelectOneBuildingThenBuildingInfo() {
		long buildingId = 1L;
		given(buildingRepository.getValidBuildingWithIdOrThrow(buildingId, Status.REGISTER))
		  .willReturn(building);
		given(buildingMapper.toInfo(building)).willReturn(info);

		BuildingDTO.Info result = buildingService.selectOne(buildingId);

		assertThat(result).isEqualTo(info);
		verify(buildingRepository).getValidBuildingWithIdOrThrow(buildingId, Status.REGISTER);
		verify(buildingMapper).toInfo(building);
	}

	@DisplayName("빌딩 정보 수정 성공")
	@Test
	public void givenBuildingIdAndRequestWhenUpdateBuildingDataThenBuildingInfo() {
		long buildingId = 1L;
		// 변경 요청 리퀘스트
		BuildingDTO.Request updateRequest = BuildingDTO.Request.builder()
		  .name("updated name")
		  .address("updated address")
		  .build();
		// 반환할 변경된 인포
		BuildingDTO.Info updatedInfo = BuildingDTO.Info.builder()
		  .name(updateRequest.getName())
		  .address(updateRequest.getAddress())
		  .build();
		given(buildingRepository.getValidBuildingWithIdOrThrow(buildingId, Status.REGISTER)).willReturn(building);
		given(buildingMapper.toInfo(building)).willReturn(updatedInfo);

		BuildingDTO.Info result = buildingService.update(buildingId, updateRequest);

		assertThat(result).isEqualTo(updatedInfo);
		assertThat(building.getName()).isEqualTo(updateRequest.getName());
		assertThat(building.getAddress()).isEqualTo(updateRequest.getAddress());
		verify(buildingRepository).getValidBuildingWithIdOrThrow(buildingId, Status.REGISTER);
		verify(buildingMapper).toInfo(building);
	}

	@DisplayName("빌딩 삭제 성공")
	@Test
	public void givenBuildingIdWhenDeleteBuildingThenSuccessResult() {
		long buildingId = 1L;
		given(buildingRepository.getValidBuildingWithIdOrThrow(buildingId, Status.REGISTER)).willReturn(building);

		boolean result = buildingService.delete(buildingId);

		assertThat(result).isTrue();
		assertThat(building.getStatus()).isEqualTo(Status.UNREGISTER);
		verify(buildingRepository).getValidBuildingWithIdOrThrow(buildingId, Status.REGISTER);
	}

}