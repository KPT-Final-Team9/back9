package com.core.back9.mapper;

import com.core.back9.dto.RoomDTO;
import com.core.back9.entity.Room;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
  componentModel = MappingConstants.ComponentModel.SPRING,
  unmappedSourcePolicy = ReportingPolicy.IGNORE,
  unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface RoomMapper {

	Room toEntity(Long buildingId, RoomDTO.Request request);

	RoomDTO.Response toResponse(Room room);

	RoomDTO.Info toInfo(Room room);

}
