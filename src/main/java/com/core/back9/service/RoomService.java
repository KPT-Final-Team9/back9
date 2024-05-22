package com.core.back9.service;

import com.core.back9.dto.RoomDTO;
import com.core.back9.entity.Building;
import com.core.back9.entity.Room;
import com.core.back9.entity.constant.Status;
import com.core.back9.mapper.RoomMapper;
import com.core.back9.repository.BuildingRepository;
import com.core.back9.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class RoomService {

	private final BuildingRepository buildingRepository;
	private final RoomRepository roomRepository;
	private final RoomMapper roomMapper;

	public RoomDTO.Response create(Long buildingId, RoomDTO.Request request) {
		Building validBuilding = buildingRepository.getValidBuildingWithIdOrThrow(buildingId, Status.REGISTER);
		Room newRoom = roomMapper.toEntity(validBuilding, request);
		Room savedRoom = roomRepository.save(newRoom);
		validBuilding.addRoom(savedRoom);
		return roomMapper.toResponse(savedRoom);
	}

	public RoomDTO.Info update(Long roomId, RoomDTO.Request request) {
		Room validRoom = roomRepository.getValidRoomWithIdOrThrow(roomId, Status.REGISTER);
		validRoom.update(request);
		return roomMapper.toInfo(validRoom);
	}

	public boolean delete(Long buildingId, Long roomId) {
		Building validBuilding = buildingRepository.getValidBuildingWithIdOrThrow(buildingId, Status.REGISTER);
		Room validRoom = roomRepository.getValidRoomWithIdOrThrow(roomId, Status.REGISTER);
		validBuilding.removeRoom(validRoom);
		validRoom.delete();
		return validRoom.getStatus() == Status.UNREGISTER;
	}

	@Transactional(readOnly = true)
	public Page<RoomDTO.Info> selectAll(Long buildingId, Pageable pageable) {
		return roomRepository.findAllByBuildingIdAndStatus(buildingId, Status.REGISTER, pageable)
		  .map(roomMapper::toInfo);
	}

	@Transactional(readOnly = true)
	public RoomDTO.Info selectOne(Long roomId) {
		Room validRoom = roomRepository.getValidRoomWithIdOrThrow(roomId, Status.REGISTER);
		return roomMapper.toInfo(validRoom);
	}

	// 소유자 추가하기

}
