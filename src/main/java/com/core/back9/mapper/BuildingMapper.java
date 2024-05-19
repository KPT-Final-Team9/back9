package com.core.back9.mapper;

import com.core.back9.dto.BuildingDTO;
import com.core.back9.entity.Building;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
  componentModel = MappingConstants.ComponentModel.SPRING,
  unmappedSourcePolicy = ReportingPolicy.IGNORE,
  unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface BuildingMapper {

	Building toEntity(BuildingDTO.Request request);

	BuildingDTO.Response toResponse(Building building);

	BuildingDTO.Info toInfo(Building building);

}
