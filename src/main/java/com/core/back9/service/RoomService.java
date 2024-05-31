package com.core.back9.service;

import com.core.back9.dto.MemberDTO;
import com.core.back9.dto.RoomDTO;
import com.core.back9.entity.Building;
import com.core.back9.entity.Member;
import com.core.back9.entity.Room;
import com.core.back9.entity.Setting;
import com.core.back9.entity.constant.Role;
import com.core.back9.entity.constant.Status;
import com.core.back9.exception.ApiErrorCode;
import com.core.back9.exception.ApiException;
import com.core.back9.mapper.RoomMapper;
import com.core.back9.repository.BuildingRepository;
import com.core.back9.repository.MemberRepository;
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
	private final MemberRepository memberRepository;

	public RoomDTO.Response create(
	  MemberDTO.Info member,
	  Long buildingId,
	  RoomDTO.Request request
	) {
		if (member.getRole() == Role.ADMIN) {
			Building validBuilding = buildingRepository.getValidBuildingWithIdOrThrow(buildingId, Status.REGISTER);
			Room newRoom = roomMapper.toEntity(validBuilding, request, createSetting());
			Room savedRoom = roomRepository.save(newRoom);
			validBuilding.addRoom(savedRoom);
			return roomMapper.toResponse(savedRoom);
		}
		throw new ApiException(ApiErrorCode.DO_NOT_HAVE_PERMISSION);
	}

	public RoomDTO.Info update(
	  MemberDTO.Info member,
	  Long buildingId,
	  Long roomId,
	  RoomDTO.Request request
	) {
		if (member.getRole() == Role.ADMIN) {
			Room validRoom = currentRoom(buildingId, roomId);
			validRoom.update(request);
			return roomMapper.toInfo(validRoom);
		}
		throw new ApiException(ApiErrorCode.DO_NOT_HAVE_PERMISSION);
	}

	public boolean delete(
	  MemberDTO.Info member,
	  Long buildingId,
	  Long roomId
	) {
		if (member.getRole() == Role.ADMIN) {
			Building validBuilding = buildingRepository.getValidBuildingWithIdOrThrow(buildingId, Status.REGISTER);
			Room validRoom = currentRoom(buildingId, roomId);
			validBuilding.removeRoom(validRoom);
			validRoom.delete();
			return validRoom.getStatus() == Status.UNREGISTER;
		}
		throw new ApiException(ApiErrorCode.DO_NOT_HAVE_PERMISSION);
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

	public RoomDTO.Info updateSwitch(
	  MemberDTO.Info member,
	  Long buildingId,
	  Long roomId,
	  boolean value
	) {
		if (member.getRole() == Role.OWNER) {
			Room validRoom = currentRoom(buildingId, roomId);
			Setting currentSetting = validRoom.getSetting();
			currentSetting.updateToggle(value);
			return roomMapper.toInfo(validRoom);
		}
		throw new ApiException(ApiErrorCode.DO_NOT_HAVE_PERMISSION);
	}

	public RoomDTO.Info updateEncourageMessage(
	  MemberDTO.Info member,
	  Long buildingId,
	  Long roomId,
	  String encourageMessage
	) {
		if (member.getRole() == Role.OWNER) {
			Room validRoom = currentRoom(buildingId, roomId);
			Setting currentSetting = validRoom.getSetting();
			currentSetting.updateEncourageMessage(encourageMessage);
			return roomMapper.toInfo(validRoom);
		}
		throw new ApiException(ApiErrorCode.DO_NOT_HAVE_PERMISSION);
	}

	public RoomDTO.InfoWithOwner settingOwner(
	  MemberDTO.Info member,
	  Long buildingId,
	  Long roomId,
	  Long ownerId
	) {
		if (member.getRole() == Role.ADMIN) {    // 소유자의 방 배정은 관리자만 할 수 있다 (초대 개념)
			Member validOwner = memberRepository.getValidMemberWithIdAndRole(ownerId, Role.OWNER, Status.REGISTER);
			Room validRoom = roomRepository.getValidRoomWithIdOrThrow(buildingId, roomId, Status.REGISTER);
			validRoom.setOwner(validOwner);

			// 관리자가 소유자에게 호실 배정 시 현재 빌딩의 호실 목록 중 대표호실로 설정된게 있는지 확인하고 없으면 대표호실로 설정한다
			if (roomRepository.existsRepresentRoom(buildingId, member.getId())) {
				validRoom.addRepresent();
			}

			return roomMapper.toInfoWithOwner(validRoom, validOwner);
		}
		throw new ApiException(ApiErrorCode.DO_NOT_HAVE_PERMISSION);
	}

	public Page<RoomDTO.Info> updateRepresent(
	  MemberDTO.Info member,
	  Long buildingId,
	  Long roomId,
	  Pageable pageable
	) {
		roomRepository.getValidRepresentRoom(buildingId, member.getId()).removeRepresent();
		roomRepository.getValidSpecificRoom(buildingId, roomId, member.getId(), Status.REGISTER).addRepresent();

		return roomRepository.findAllByBuildingIdAndStatus(buildingId, Status.REGISTER, pageable)
		  .map(roomMapper::toInfo);
	}

	private Room currentRoom(Long buildingId, Long roomId) {
		return roomRepository.getValidRoomWithIdOrThrow(buildingId, roomId, Status.REGISTER);
	}

	public Setting createSetting() {
		Setting newSetting = Setting.builder().build();
		return settingRepository.save(newSetting);
	}

}
