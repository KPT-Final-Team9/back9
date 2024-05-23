package com.core.back9.service;

import com.core.back9.dto.RoomDTO;
import com.core.back9.entity.Building;
import com.core.back9.entity.Room;
import com.core.back9.entity.Setting;
import com.core.back9.entity.constant.Status;
import com.core.back9.mapper.RoomMapper;
import com.core.back9.repository.BuildingRepository;
import com.core.back9.repository.RoomRepository;
import com.core.back9.repository.SettingRepository;
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
	private final SettingRepository settingRepository;

	public RoomDTO.Response create(Long buildingId, RoomDTO.Request request) {
		Building validBuilding = buildingRepository.getValidBuildingWithIdOrThrow(buildingId, Status.REGISTER);
		Room newRoom = roomMapper.toEntity(validBuilding, request, createSetting());
		Room savedRoom = roomRepository.save(newRoom);
		validBuilding.addRoom(savedRoom);
		return roomMapper.toResponse(savedRoom);
	}

	public RoomDTO.Info update(Long buildingId, Long roomId, RoomDTO.Request request) {
		Room validRoom = currentRoom(buildingId, roomId);
		validRoom.update(request);
		return roomMapper.toInfo(validRoom);
	}

	public boolean delete(Long buildingId, Long roomId) {
		Building validBuilding = buildingRepository.getValidBuildingWithIdOrThrow(buildingId, Status.REGISTER);
		Room validRoom = currentRoom(buildingId, roomId);
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
	public RoomDTO.Info selectOne(Long buildingId, Long roomId) {
		Room validRoom = currentRoom(buildingId, roomId);
		return roomMapper.toInfo(validRoom);
	}

	public RoomDTO.Info updateSwitch(Long buildingId, Long roomId, boolean value) {
		Room validRoom = currentRoom(buildingId, roomId);
		Setting currentSetting = validRoom.getSetting();
		currentSetting.updateToggle(value);
		return roomMapper.toInfo(validRoom);
	}

	public RoomDTO.Info updateEncourageMessage(Long buildingId, Long roomId, String encourageMessage) {
		Room validRoom = currentRoom(buildingId, roomId);
		Setting currentSetting = validRoom.getSetting();
		currentSetting.updateEncourageMessage(encourageMessage);
		return roomMapper.toInfo(validRoom);
	}

	private Room currentRoom(Long buildingId, Long roomId) {
		return roomRepository.getValidRoomWithIdOrThrow(buildingId, roomId, Status.REGISTER);
	}

	public Setting createSetting() {
		Setting newSetting = Setting.builder().build();
		return settingRepository.save(newSetting);
	}

	// 소유자 추가하기

}
