package com.core.back9.mapper;

import com.core.back9.dto.RoomDTO;
import com.core.back9.entity.Building;
import com.core.back9.entity.Room;
import com.core.back9.entity.Setting;
import org.mapstruct.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Mapper(
  componentModel = MappingConstants.ComponentModel.SPRING,
  unmappedSourcePolicy = ReportingPolicy.IGNORE,
  unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface RoomMapper {

	@Mapping(target = "building", expression = "java(building)")
	@Mapping(target = "setting", expression = "java(setting)")
	Room toEntity(@Context Building building, RoomDTO.Request request, @Context Setting setting);

	RoomDTO.Response toResponse(Room room);

	RoomDTO.Info toInfo(Room room);

	List<RoomDTO.Info> toInfoList(List<Room> rooms);

	@Named("toInfoPage")
	default Page<RoomDTO.Info> toInfoPage(List<Room> roomList, Pageable pageable) {
		int start = (int) pageable.getOffset();
		int end = Math.min((start + pageable.getPageSize()), roomList.size());
		List<RoomDTO.Info> dtoList = toInfoList(roomList.subList(start, end));
		return new PageImpl<>(dtoList, pageable, roomList.size());
	}

}
