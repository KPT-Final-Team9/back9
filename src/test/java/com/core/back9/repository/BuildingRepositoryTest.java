package com.core.back9.repository;

import com.core.back9.common.config.AuditingConfig;
import com.core.back9.dto.BuildingDTO;
import com.core.back9.entity.Building;
import com.core.back9.entity.constant.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@DataJpaTest
@Import(value = AuditingConfig.class)
class BuildingRepositoryTest {

	@Autowired
	private BuildingRepository buildingRepository;

	private Building building;

	@BeforeEach
	public void initSetting() {
		building = Building.builder()
		  .name("first building")
		  .address("first address")
		  .zipCode("first zipCode")
		  .build();
	}

	@DisplayName("빌딩 저장")
	@Test
	public void givenBuildingEntityWhenSaveBuilding() {
		Building savedBuilding = buildingRepository.save(building);

		assertThat(savedBuilding).isNotNull();
		assertThat(savedBuilding.getName()).isEqualTo(building.getName());
		assertThat(savedBuilding.getAddress()).isEqualTo(building.getAddress());
		assertThat(savedBuilding.getZipCode()).isEqualTo(building.getZipCode());
		assertThat(savedBuilding.getStatus()).isEqualTo(Status.REGISTER);
		assertThat(savedBuilding.getCreatedAt()).isNotNull();
	}

	@DisplayName("빌딩 전체 조회")
	@Test
	public void givenNothingWhenFindAllBuildings() {
		int buildingSize = 5;
		for (int i = 0; i < buildingSize; i++) {
			buildingRepository.save(
			  Building.builder()
				.name(String.format("building name %s", i))
				.address(String.format("building address %s", i))
				.zipCode(String.format("building zipCode %s", i))
				.build()
			);
		}

		Page<Building> allBuildings = buildingRepository.findAllByStatus(Status.REGISTER, any(Pageable.class));

		assertThat(allBuildings).isNotNull();
		assertThat(allBuildings.getSize()).isEqualTo(buildingSize);
	}

	@DisplayName("빌딩 아이디로 조회")
	@Test
	public void givenBuildingIdWhenFindFirstBuilding() {
		Building savedBuilding = buildingRepository.save(building);
		long savedBuildingId = savedBuilding.getId();

		Building validBuilding = buildingRepository.getValidBuildingWithIdOrThrow(savedBuildingId, Status.REGISTER);

		assertThat(validBuilding).isNotNull();
		assertThat(validBuilding.getName()).isEqualTo(building.getName());
		assertThat(validBuilding.getAddress()).isEqualTo(building.getAddress());
		assertThat(validBuilding.getZipCode()).isEqualTo(building.getZipCode());
		assertThat(validBuilding.getStatus()).isEqualTo(Status.REGISTER);
		assertThat(validBuilding.getCreatedAt()).isNotNull();
	}

	@DisplayName("빌딩 정보 수정")
	@Test
	public void givenBuildingIdAndRequestWhenUpdateBuildingData() {
		BuildingDTO.Request request = BuildingDTO.Request.builder()
		  .name("update name")
		  .address("update address")
		  .zipCode("update zipCode")
		  .build();
		Building savedBuilding = buildingRepository.save(building);
		long savedBuildingId = savedBuilding.getId();

		Building validBuilding = buildingRepository.getValidBuildingWithIdOrThrow(savedBuildingId, Status.REGISTER);
		validBuilding.update(request);

		assertThat(validBuilding.getName()).isEqualTo(request.getName());
		assertThat(validBuilding.getAddress()).isEqualTo(request.getAddress());
		assertThat(validBuilding.getZipCode()).isEqualTo(request.getZipCode());
	}

	@DisplayName("빌딩 삭제")
	@Test
	public void givenBuildingIdWhenDeleteBuilding() {
		Building savedBuilding = buildingRepository.save(building);
		long savedBuildingId = savedBuilding.getId();
		savedBuilding.delete();

		Optional<Building> deletedBuilding = buildingRepository.findFirstByIdAndStatus(savedBuildingId, Status.REGISTER);

		assertThat(deletedBuilding.isPresent()).isFalse();
	}

}