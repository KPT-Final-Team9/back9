package com.core.back9.service;

import com.core.back9.dto.BuildingDTO;
import com.core.back9.dto.MemberDTO;
import com.core.back9.entity.Building;
import com.core.back9.entity.constant.Role;
import com.core.back9.entity.constant.Status;
import com.core.back9.exception.ApiErrorCode;
import com.core.back9.exception.ApiException;
import com.core.back9.mapper.BuildingMapper;
import com.core.back9.repository.BuildingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class BuildingService {

	private final BuildingRepository buildingRepository;
	private final BuildingMapper buildingMapper;

	public BuildingDTO.Response create(MemberDTO.Info member, BuildingDTO.Request request) {
		if (member.getRole() == Role.ADMIN) {
			Building requestBuilding = buildingMapper.toEntity(request);
			Building savedBuilding = buildingRepository.save(requestBuilding);
			return buildingMapper.toResponse(savedBuilding);
		}
		throw new ApiException(ApiErrorCode.DO_NOT_HAVE_PERMISSION);
	}

	@Transactional(readOnly = true)
	public Page<BuildingDTO.Info> selectAll(Pageable pageable) {
		return buildingRepository.findAllByStatus(Status.REGISTER, pageable)
		  .map(building -> buildingMapper.toInfo(building, pageable));
	}

	@Transactional(readOnly = true)
	public List<BuildingDTO.SimpleInfo> selectAll(MemberDTO.Info member) {

		return buildingRepository.findAllBuildingWithRoomsByBuildingIdAndMemberId(member.getId())
		  .stream().map(buildingMapper::toSimpleInfo).toList();

	}

	@Transactional(readOnly = true)
	public BuildingDTO.Info selectOne(Long buildingId, Pageable pageable) {
		Building validBuilding = buildingRepository.getValidBuildingWithIdOrThrow(buildingId, Status.REGISTER);
		return buildingMapper.toInfo(validBuilding, pageable);
	}

	@Transactional(readOnly = true)
	public BuildingDTO.Info selectOne(MemberDTO.Info member, Long buildingId, Pageable pageable) {
		Building validBuilding = buildingRepository.findValidBuildingWithRoomsByBuildingIdAndMemberId(buildingId, member.getId());
		return buildingMapper.toInfo(validBuilding, pageable);
	}

	public BuildingDTO.Info update(MemberDTO.Info member, Long buildingId, BuildingDTO.Request request, Pageable pageable) {
		if (member.getRole() == Role.ADMIN) {
			Building validBuilding = buildingRepository.getValidBuildingWithIdOrThrow(buildingId, Status.REGISTER);
			validBuilding.update(request);
			return buildingMapper.toInfo(validBuilding, pageable);
		}
		throw new ApiException(ApiErrorCode.DO_NOT_HAVE_PERMISSION);
	}

	public boolean delete(MemberDTO.Info member, Long buildingId) {
		if (member.getRole() == Role.ADMIN) {
			Building validBuilding = buildingRepository.getValidBuildingWithIdOrThrow(buildingId, Status.REGISTER);
			validBuilding.delete();
			return validBuilding.getStatus() == Status.UNREGISTER;
		}
		throw new ApiException(ApiErrorCode.DO_NOT_HAVE_PERMISSION);
	}

}
