package com.core.back9.service;

import com.core.back9.dto.BuildingDTO;
import com.core.back9.entity.Building;
import com.core.back9.entity.constant.Status;
import com.core.back9.mapper.BuildingMapper;
import com.core.back9.repository.BuildingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class BuildingService {

	private final BuildingRepository buildingRepository;
	private final BuildingMapper buildingMapper;

	public BuildingDTO.Response create(BuildingDTO.Request request) {
		Building requestBuilding = buildingMapper.toEntity(request);
		Building savedBuilding = buildingRepository.save(requestBuilding);
		return buildingMapper.toResponse(savedBuilding);
	}

	@Transactional(readOnly = true)
	public Page<BuildingDTO.Info> selectAll(Pageable pageable) {
		return buildingRepository.findAllByStatus(Status.REGISTER, pageable)
		  .map(building -> buildingMapper.toInfo(building, pageable));
	}

	@Transactional(readOnly = true)
	public BuildingDTO.Info selectOne(Long buildingId) {
		Building validBuilding = buildingRepository.getValidBuildingWithIdOrThrow(buildingId, Status.REGISTER);
		return buildingMapper.toInfo(validBuilding);
	}

	public BuildingDTO.Info update(Long buildingId, BuildingDTO.Request request) {
		Building validBuilding = buildingRepository.getValidBuildingWithIdOrThrow(buildingId, Status.REGISTER);
		validBuilding.update(request);
		return buildingMapper.toInfo(validBuilding);
	}

	public boolean delete(Long buildingId) {
		Building validBuilding = buildingRepository.getValidBuildingWithIdOrThrow(buildingId, Status.REGISTER);
		validBuilding.delete();
		return validBuilding.getStatus() == Status.UNREGISTER;
	}

}
