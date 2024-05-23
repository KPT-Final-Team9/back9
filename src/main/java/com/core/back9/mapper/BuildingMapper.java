package com.core.back9.mapper;

import com.core.back9.dto.BuildingDTO;
import com.core.back9.dto.RoomDTO;
import com.core.back9.entity.Building;
import com.core.back9.entity.Room;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Mapper(
  componentModel = MappingConstants.ComponentModel.SPRING,
  unmappedSourcePolicy = ReportingPolicy.IGNORE,
  unmappedTargetPolicy = ReportingPolicy.IGNORE,
  uses = RoomMapper.class
)
public interface BuildingMapper {

	@Autowired
	RoomMapper roomMapper = new RoomMapperImpl();

	Building toEntity(BuildingDTO.Request request);

	BuildingDTO.Response toResponse(Building building);

	@Mapping(
	  target = "roomPage",
	  expression = "java(toRoomPage(building.getRoomList(), pageable))"
	)
	BuildingDTO.Info toInfo(Building building, Pageable pageable);

	@Named("toRoomPage")
	default Page<RoomDTO.Info> toRoomPage(List<Room> roomList, Pageable pageable) {
		return roomMapper.toInfoPage(roomList, pageable);
	}

}
