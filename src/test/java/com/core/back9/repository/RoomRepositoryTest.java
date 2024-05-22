package com.core.back9.repository;

import com.core.back9.common.config.AuditingConfig;
import com.core.back9.dto.RoomDTO;
import com.core.back9.entity.Building;
import com.core.back9.entity.Room;
import com.core.back9.entity.constant.Status;
import com.core.back9.entity.constant.Usage;
import org.junit.jupiter.api.AfterEach;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@DataJpaTest
@Import(value = AuditingConfig.class)
class RoomRepositoryTest {

	@Autowired
	private RoomRepository roomRepository;

	@Autowired
	private BuildingRepository buildingRepository;

	private Building building;
	private Room room;

	@BeforeEach
	public void initSetting() {
		building = Building.builder()
		  .name("building name 1")
		  .address("building address 1")
		  .zipCode("building zipCode 1")
		  .build();
		buildingRepository.save(building);
		room = Room.builder()
		  .building(building)
		  .name("room name 1")
		  .floor("room floor 1")
		  .area(84F)
		  .usage(Usage.OFFICES)
		  .build();
	}

	@AfterEach
	public void closing() {
		buildingRepository.delete(building);
	}

	@DisplayName("호실 저장")
	@Test
	public void givenRoomEntityWhenSaveRoom() {
		Room newRoom = Room.builder()
		  .building(building)
		  .name("room name 1")
		  .floor("room floor 1")
		  .area(84F)
		  .usage(Usage.OFFICES)
		  .build();

		Room savedRoom = roomRepository.save(newRoom);
		assertThat(savedRoom).isNotNull();
		assertThat(savedRoom.getName()).isEqualTo(newRoom.getName());
		assertThat(savedRoom.getFloor()).isEqualTo(newRoom.getFloor());
		assertThat(savedRoom.getArea()).isEqualTo(newRoom.getArea());
		assertThat(savedRoom.getUsage()).isEqualTo(newRoom.getUsage());
		assertThat(savedRoom.getStatus()).isEqualTo(newRoom.getStatus());
		assertThat(savedRoom.getCreatedAt()).isNotNull();
	}

	@DisplayName("호실 전체 조회")
	@Test
	public void givenNothingWhenFindAllRoomThenRoomPageList() {
		int roomSize = 10;
		for (int i = 0; i < roomSize; i++) {
			roomRepository.save(
			  Room.builder()
				.building(building)
				.name(String.format("room name %s", i))
				.floor(String.format("room floor %s", i))
				.area(i + 10F)
				.usage(Usage.FINANCIAL_BUSINESSES)
				.build()
			);
		}

		Page<Room> roomPage = roomRepository.findAllByBuildingIdAndStatus(building.getId(), Status.REGISTER, any(Pageable.class));

		assertThat(roomPage).isNotNull();
		assertThat(roomPage.getSize()).isEqualTo(roomSize);
	}

	@DisplayName("호실 아이디로 조회")
	@Test
	public void givenRoomIdWhenFindFirstRoomThenRoomInfo() {
		Room savedRoom = roomRepository.save(room);
		long savedRoomId = savedRoom.getId();

		Room validRoom = roomRepository.getValidRoomWithIdOrThrow(savedRoomId, Status.REGISTER);

		assertThat(savedRoom).isNotNull();
		assertThat(savedRoomId).isEqualTo(savedRoomId);
		assertThat(validRoom).isNotNull();
		assertThat(validRoom.getName()).isEqualTo(room.getName());
		assertThat(validRoom.getFloor()).isEqualTo(room.getFloor());
		assertThat(validRoom.getArea()).isEqualTo(room.getArea());
		assertThat(validRoom.getUsage()).isEqualTo(room.getUsage());
		assertThat(validRoom.getStatus()).isEqualTo(Status.REGISTER);
	}

	@DisplayName("호실 정보 수정")
	@Test
	public void givenRoomIdAndRequestWhenUpdateRoomDataThenRoomInfo() {
		RoomDTO.Request request = RoomDTO.Request.builder()
		  .name("updated room name")
		  .floor("updated room floor")
		  .build();

		Room savedRoom = roomRepository.save(room);
		long savedRoomId = savedRoom.getId();

		Room validRoom = roomRepository.getValidRoomWithIdOrThrow(savedRoomId, Status.REGISTER);
		validRoom.update(request);

		assertThat(validRoom).isNotNull();
		assertThat(validRoom.getName()).isEqualTo(request.getName());
		assertThat(validRoom.getFloor()).isEqualTo(request.getFloor());
	}

	@DisplayName("호실 삭제")
	@Test
	public void givenRoomIdWhenDeleteRoom() {
		Room savedRoom = roomRepository.save(room);
		long savedRoomId = savedRoom.getId();

		Room validRoom = roomRepository.getValidRoomWithIdOrThrow(savedRoomId, Status.REGISTER);
		validRoom.delete();

		assertThat(validRoom.getStatus()).isEqualTo(Status.UNREGISTER);
	}

}